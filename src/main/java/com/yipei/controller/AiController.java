package com.yipei.controller;

import com.yipei.config.AiProperties;
import com.yipei.entity.ApiResponse;
import com.yipei.entity.ServiceRequest;
import com.yipei.service.AiSummaryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final AiSummaryService aiSummaryService;
    private final AiProperties aiProperties;

    public AiController(AiSummaryService aiSummaryService, AiProperties aiProperties) {
        this.aiSummaryService = aiSummaryService;
        this.aiProperties = aiProperties;
    }

    /** AI 服务需求摘要生成 */
    @PostMapping("/service-summary")
    public ApiResponse<Map<String, Object>> summary(@RequestBody Map<String, String> body) {
        if (!aiProperties.isConfigured()) {
            return ApiResponse.error(503, "AI 功能未接入，请配置 AI API Key 后重试");
        }

        String text = body.getOrDefault("text", "");
        if (text.isBlank()) {
            return ApiResponse.error(400, "需求文本不能为空");
        }

        ServiceRequest sr = new ServiceRequest();
        sr.setServiceType(body.get("serviceType"));
        sr.setRequirement(text);
        sr.setHospitalName(body.get("hospitalName"));
        sr.setDepartment(body.get("department"));
        sr.setSpecialNotes(body.get("specialNotes"));
        try {
            sr.setServiceDate(java.time.LocalDateTime.parse(body.get("serviceDate")));
        } catch (Exception ignored) {}

        Optional<String> result = aiSummaryService.generate(sr);
        return result.map(summary -> ApiResponse.success(Map.of(
                "summary", (Object) summary,
                "source", "ai"
        ))).orElseGet(() -> ApiResponse.error(500, "AI 调用失败，请稍后重试"));
    }

    /** AI 服务标签生成 */
    @PostMapping("/service-tags")
    public ApiResponse<Map<String, Object>> tags(@RequestBody Map<String, String> body) {
        if (!aiProperties.isConfigured()) {
            return ApiResponse.error(503, "AI 功能未接入，请配置 AI API Key 后重试");
        }
        String types = body.getOrDefault("serviceTypes", "");
        List<String> tags = List.of(types.split("[,，]"));
        return ApiResponse.success(Map.of("tags", tags));
    }

    /** AI 性格匹配：根据偏好推荐陪诊师标签，或根据陪诊师标签推荐匹配订单 */
    @PostMapping("/match")
    public ApiResponse<Map<String, Object>> match(@RequestBody Map<String, String> body) {
        if (!aiProperties.isConfigured()) {
            return ApiResponse.error(503, "AI 功能未接入");
        }
        String direction = body.getOrDefault("direction", "companion");
        String input = body.getOrDefault("input", "");
        if (input.isBlank()) {
            return ApiResponse.error(400, "输入文本不能为空");
        }

        String prompt;
        if ("order".equals(direction)) {
            String myTraits = body.getOrDefault("myTraits", "");
            prompt = "你是一个陪诊师，你的性格标签是：" + myTraits + "。下面是一些服务需求列表，请根据你的性格选出最适合你接单的需求，给出最多3个推荐。\n\n" + input;
        } else {
            String traitsList = body.getOrDefault("traitsList", "");
            prompt = "根据用户偏好：「" + input + "」，从以下陪诊师性格标签中推荐最匹配的标签：\n" + traitsList + "\n\n只输出推荐标签名，逗号分隔。";
        }

        Optional<String> result = aiSummaryService.callAi(prompt);
        return result.map(r -> ApiResponse.success(Map.of("result", (Object) r.trim(), "source", "ai")))
                .orElseGet(() -> ApiResponse.error(500, "AI 调用失败"));
    }
}
