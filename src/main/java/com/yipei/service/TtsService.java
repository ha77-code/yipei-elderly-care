package com.yipei.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yipei.config.TtsProperties;
import com.yipei.entity.CompanionProfile;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.CompanionProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TtsService {
    private static final Logger log = LoggerFactory.getLogger(TtsService.class);

    private final TtsProperties ttsProperties;
    private final CompanionProfileMapper companionProfileMapper;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public TtsService(TtsProperties ttsProperties,
                      CompanionProfileMapper companionProfileMapper,
                      ObjectMapper objectMapper) {
        this.ttsProperties = ttsProperties;
        this.companionProfileMapper = companionProfileMapper;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(Math.max(1, ttsProperties.getTimeoutSeconds())))
                .build();
    }

    /** 生成陪诊师信息语音，尚未接入时返回 empty */
    public Optional<byte[]> generate(Long companionProfileId, String voiceType) {
        CompanionProfile profile = companionProfileMapper.selectById(companionProfileId);
        if (profile == null) {
            throw new NotFoundException("陪诊师不存在，ID: " + companionProfileId);
        }
        if (profile.getAuditStatus() == null || profile.getAuditStatus() != 1) {
            throw new NotFoundException("该陪诊师未通过审核，不可朗读");
        }

        if (!ttsProperties.isConfigured()) {
            return Optional.empty();
        }

        String selectedVoice = voiceType != null && !voiceType.isBlank()
                ? voiceType : ttsProperties.getVoiceType();
        String text = buildText(profile);
        return callVolcengineTts(text, selectedVoice);
    }

    /** 通用语音合成，直接传入文本 */
    public Optional<byte[]> speak(String text, String voiceType) {
        if (!ttsProperties.isConfigured()) {
            return Optional.empty();
        }
        String selectedVoice = voiceType != null && !voiceType.isBlank()
                ? voiceType : ttsProperties.getVoiceType();
        return callVolcengineTts(text, selectedVoice);
    }

    /** 构建朗读文本 */
    String buildText(CompanionProfile profile) {
        StringBuilder sb = new StringBuilder();
        sb.append(profile.getRealName()).append("，");
        sb.append("服务区域").append(profile.getServiceArea()).append("，");
        sb.append("经验").append(profile.getExperienceYears()).append("年，");
        sb.append("擅长");
        if (profile.getServiceTypes() != null) {
            sb.append(profile.getServiceTypes().replace(",", "、"));
        }
        sb.append("。");
        if (profile.getIntroduction() != null && !profile.getIntroduction().isBlank()) {
            sb.append("个人介绍：").append(profile.getIntroduction());
        }
        return sb.toString();
    }

    /** 火山 HTTP 非流式接口单次文本上限约 1024 字节，留余量取 900 */
    private static final int MAX_CHUNK_BYTES = 900;

    /**
     * 调用火山引擎语音合成。文本过长时按句子切块逐段合成，再把 MP3 拼接为一段返回。
     * MP3 是帧序列，直接拼接字节即可连续播放。
     */
    private Optional<byte[]> callVolcengineTts(String text, String voiceType) {
        String cleanText = stripPunctuation(text);
        if (cleanText.isBlank()) {
            log.warn("TTS text is empty after stripping punctuation");
            return Optional.empty();
        }

        List<String> chunks = splitText(text);
        if (chunks.isEmpty()) {
            return Optional.empty();
        }

        ByteArrayOutputStream merged = new ByteArrayOutputStream();
        for (String chunk : chunks) {
            Optional<byte[]> part = synthesizeChunk(chunk, voiceType);
            if (part.isEmpty()) {
                return Optional.empty();
            }
            merged.writeBytes(part.get());
        }
        byte[] audioBytes = merged.toByteArray();
        log.info("TTS generated audio, size={} bytes, chunks={}", audioBytes.length, chunks.size());
        return audioBytes.length > 0 ? Optional.of(audioBytes) : Optional.empty();
    }

    /** 合成单个文本块 */
    private Optional<byte[]> synthesizeChunk(String text, String voiceType) {
        try {
            String cleanText = stripPunctuation(text);
            if (cleanText.isBlank()) {
                return Optional.of(new byte[0]);
            }

            ObjectNode body = objectMapper.createObjectNode();
            ObjectNode app = body.putObject("app");
            app.put("appid", ttsProperties.getAppId());
            app.put("token", ttsProperties.getAccessToken());
            app.put("cluster", "volcano_tts");
            body.putObject("user").put("uid", "yipei-tts");
            ObjectNode audio = body.putObject("audio");
            audio.put("voice_type", voiceType);
            audio.put("encoding", "mp3");
            audio.put("speed_ratio", 1.0);
            ObjectNode request = body.putObject("request");
            request.put("reqid", UUID.randomUUID().toString());
            request.put("text", cleanText);
            request.put("text_type", "plain");
            request.put("operation", "query");

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://openspeech.bytedance.com/api/v1/tts"))
                    .timeout(Duration.ofSeconds(Math.max(1, ttsProperties.getTimeoutSeconds())))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer;" + ttsProperties.getAccessToken().trim())
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("TTS request failed with HTTP {}", response.statusCode());
                return Optional.empty();
            }

            JsonNode json = objectMapper.readTree(response.body());
            int code = json.path("code").asInt(-1);
            if (code != 3000) {
                log.warn("TTS API error: code={}, message={}", code, json.path("message").asText());
                return Optional.empty();
            }

            String data = json.path("data").asText();
            if (data == null || data.isBlank()) {
                log.warn("TTS returned empty audio data");
                return Optional.empty();
            }

            byte[] audioBytes = Base64.getDecoder().decode(data);
            return Optional.of(audioBytes);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("TTS request was interrupted");
            return Optional.empty();
        } catch (IOException | RuntimeException exception) {
            log.warn("TTS request failed: {}", exception.getMessage());
            return Optional.empty();
        }
    }

    /** 按句末标点把长文本切成不超过 MAX_CHUNK_BYTES 的块，保证每块可单次合成 */
    private List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int length = text.length();
        int start = 0;
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            boolean isBoundary = c == '。' || c == '！' || c == '？' || c == '；'
                    || c == '.' || c == '!' || c == '?' || c == ';' || c == '\n';
            boolean isLast = i == length - 1;
            if (isBoundary || isLast) {
                String sentence = text.substring(start, i + 1);
                start = i + 1;
                // 单句本身就超限时，按字节强制截断
                if (sentence.getBytes(StandardCharsets.UTF_8).length > MAX_CHUNK_BYTES) {
                    flush(chunks, current);
                    chunks.addAll(hardSplit(sentence));
                    continue;
                }
                if (current.length() > 0
                        && (current.toString() + sentence).getBytes(StandardCharsets.UTF_8).length > MAX_CHUNK_BYTES) {
                    flush(chunks, current);
                }
                current.append(sentence);
            }
        }
        flush(chunks, current);
        return chunks;
    }

    private void flush(List<String> chunks, StringBuilder buffer) {
        if (buffer.length() > 0) {
            chunks.add(buffer.toString());
            buffer.setLength(0);
        }
    }

    /** 无句读的超长句，按字节窗口硬切 */
    private List<String> hardSplit(String sentence) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < sentence.length(); i++) {
            buffer.append(sentence.charAt(i));
            if (buffer.toString().getBytes(StandardCharsets.UTF_8).length >= MAX_CHUNK_BYTES) {
                parts.add(buffer.toString());
                buffer.setLength(0);
            }
        }
        if (buffer.length() > 0) {
            parts.add(buffer.toString());
        }
        return parts;
    }

    /** BV700_streaming 不支持标点符号，替换为空格 */
    private String stripPunctuation(String text) {
        return text.replaceAll("[\\p{P}\\p{S}，。！？；：、\"\"''【】《》（）…—～．]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
