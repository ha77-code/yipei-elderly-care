package com.yipei.controller;

import com.yipei.config.TtsProperties;
import com.yipei.service.TtsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tts")
public class TtsController {
    private final TtsService ttsService;
    private final TtsProperties ttsProperties;

    public TtsController(TtsService ttsService, TtsProperties ttsProperties) {
        this.ttsService = ttsService;
        this.ttsProperties = ttsProperties;
    }

    /** 获取可用音色列表 */
    @GetMapping("/voices")
    public ResponseEntity<List<Map<String, String>>> listVoices() {
        List<Map<String, String>> voices = ttsProperties.getVoices().stream()
                .map(v -> Map.of("name", v.getName(), "voiceType", v.getVoiceType()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(voices);
    }

    /** 通用语音合成，接受文本直接朗读 */
    @PostMapping("/speak")
    public ResponseEntity<?> speak(@RequestBody Map<String, String> body) {
        if (!ttsProperties.isConfigured()) {
            return ResponseEntity.status(503)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"code\":503,\"message\":\"语音功能未接入\"}");
        }
        String text = body.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"code\":400,\"message\":\"文本不能为空\"}");
        }
        Optional<byte[]> audio = ttsService.speak(text, body.get("voiceType"));
        if (audio.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                    .body(audio.get());
        }
        return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"code\":500,\"message\":\"语音生成失败，请稍后重试\"}");
    }

    /** 朗读陪诊师信息，可选 voiceType 参数指定音色 */
    @GetMapping("/companion/{id}")
    public ResponseEntity<?> speakCompanion(@PathVariable Long id,
                                            @RequestParam(required = false) String voiceType) {
        if (!ttsProperties.isConfigured()) {
            return ResponseEntity.status(503)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"code\":503,\"message\":\"语音功能未接入，请配置火山引擎 TTS 后重试\"}");
        }

        Optional<byte[]> audio = ttsService.generate(id, voiceType);
        if (audio.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=companion.mp3")
                    .body(audio.get());
        }
        return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"code\":500,\"message\":\"语音生成失败，请稍后重试\"}");
    }
}
