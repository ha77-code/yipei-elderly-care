package com.yipei.controller;

import com.yipei.config.TtsProperties;
import com.yipei.service.TtsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/tts")
public class TtsController {
    private final TtsService ttsService;
    private final TtsProperties ttsProperties;

    public TtsController(TtsService ttsService, TtsProperties ttsProperties) {
        this.ttsService = ttsService;
        this.ttsProperties = ttsProperties;
    }

    /** 朗读陪诊师信息 */
    @GetMapping("/companion/{id}")
    public ResponseEntity<?> speakCompanion(@PathVariable Long id) {
        if (!ttsProperties.isConfigured()) {
            return ResponseEntity.status(503)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"code\":503,\"message\":\"语音功能未接入，请配置火山引擎 TTS 后重试\"}");
        }

        Optional<byte[]> audio = ttsService.generate(id);
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
