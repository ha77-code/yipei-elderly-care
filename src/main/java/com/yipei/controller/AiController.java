package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    /** AI 服务需求摘要生成（stub） */
    @PostMapping("/service-summary")
    public ApiResponse<Map<String, String>> summary(@RequestBody Map<String, String> body) {
        String text = body.getOrDefault("text", "");
        // Stub: 简单提取关键词，生产环境接入真实 AI API
        String serviceType = text.contains("医院") || text.contains("就诊") ? "陪诊" : "生活陪伴";
        String summary = "服务类型：" + serviceType + "。原始需求：" +
                (text.length() > 50 ? text.substring(0, 50) + "..." : text);
        return ApiResponse.success(Map.of("serviceType", serviceType, "summary", summary));
    }

    /** AI 服务标签生成（stub） */
    @PostMapping("/service-tags")
    public ApiResponse<Map<String, Object>> tags(@RequestBody Map<String, String> body) {
        String intro = body.getOrDefault("introduction", "");
        String types = body.getOrDefault("serviceTypes", "");
        // Stub: 从服务类型中拆分标签
        List<String> tags = List.of(types.split(","));
        return ApiResponse.success(Map.of("tags", tags));
    }
}
