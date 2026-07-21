package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.Evaluation;
import com.yipei.entity.EvaluationCreateRequest;
import com.yipei.security.SecurityUtils;
import com.yipei.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {
    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /** 创建评价 */
    @PostMapping("/create")
    public ApiResponse<Evaluation> create(
            @Valid @RequestBody EvaluationCreateRequest request) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(evaluationService.create(userId, request));
    }

    /** 订单下的评价 */
    @GetMapping("/order/{orderId}")
    public ApiResponse<List<Evaluation>> getByOrder(@PathVariable Long orderId) {
        return ApiResponse.success(evaluationService.getByOrderId(orderId));
    }

    /** 陪诊师收到的评价 */
    @GetMapping("/companion/{companionId}")
    public ApiResponse<List<Evaluation>> getByCompanion(@PathVariable Long companionId) {
        return ApiResponse.success(evaluationService.getByCompanionId(companionId));
    }
}