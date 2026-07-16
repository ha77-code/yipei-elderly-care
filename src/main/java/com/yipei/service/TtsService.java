package com.yipei.service;

import com.yipei.config.TtsProperties;
import com.yipei.entity.CompanionProfile;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.CompanionProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TtsService {
    private static final Logger log = LoggerFactory.getLogger(TtsService.class);

    private final TtsProperties ttsProperties;
    private final CompanionProfileMapper companionProfileMapper;

    public TtsService(TtsProperties ttsProperties,
                      CompanionProfileMapper companionProfileMapper) {
        this.ttsProperties = ttsProperties;
        this.companionProfileMapper = companionProfileMapper;
    }

    /** 生成陪诊师信息语音，尚未接入时返回 empty */
    public Optional<byte[]> generate(Long companionProfileId) {
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

        String text = buildText(profile);
        return callVolcengineTts(text);
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

    /** 调用火山引擎 TTS API（待实现） */
    private Optional<byte[]> callVolcengineTts(String text) {
        // TODO: 接入火山引擎语音合成 API
        // POST https://openspeech.bytedance.com/api/v1/tts
        // 参数: appid, access_token, text, voice_type, format=mp3
        log.info("TTS stub: text length={}, voice={}", text.length(), ttsProperties.getVoiceType());
        return Optional.empty();
    }
}
