package com.yipei.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "yipei.tts")
public class TtsProperties {

    private boolean enabled = false;
    private String appId = "";
    private String accessToken = "";
    private String voiceType = "zh_female_qingxin";
    private int timeoutSeconds = 15;
    private List<VoiceOption> voices = new ArrayList<>();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getVoiceType() { return voiceType; }
    public void setVoiceType(String voiceType) { this.voiceType = voiceType; }

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

    public boolean isConfigured() {
        return enabled && hasText(appId) && hasText(accessToken);
    }

    public List<VoiceOption> getVoices() { return voices; }
    public void setVoices(List<VoiceOption> voices) { this.voices = voices; }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public static class VoiceOption {
        private String name;
        private String voiceType;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVoiceType() { return voiceType; }
        public void setVoiceType(String voiceType) { this.voiceType = voiceType; }
    }
}
