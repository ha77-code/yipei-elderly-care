package com.yipei.service;

import com.yipei.entity.CompanionProfile;
import com.yipei.entity.Evaluation;
import com.yipei.entity.EvaluationCreateRequest;
import com.yipei.entity.ServiceOrder;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.EvaluationMapper;
import com.yipei.mapper.ServiceOrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationService {
    private final EvaluationMapper evaluationMapper;
    private final ServiceOrderMapper serviceOrderMapper;
    private final CompanionProfileMapper companionProfileMapper;

    public EvaluationService(EvaluationMapper evaluationMapper,
                             ServiceOrderMapper serviceOrderMapper,
                             CompanionProfileMapper companionProfileMapper) {
        this.evaluationMapper = evaluationMapper;
        this.serviceOrderMapper = serviceOrderMapper;
        this.companionProfileMapper = companionProfileMapper;
    }

    /** 创建评价 */
    public Evaluation create(Long userId, EvaluationCreateRequest request) {
        // 校验订单存在且已完成
        ServiceOrder order = serviceOrderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + request.getOrderId());
        }
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new ForbiddenException("仅已完成订单可以评价");
        }
        // 校验评价人是订单相关方
        if (!order.getCustomerId().equals(userId) && !order.getCompanionId().equals(userId)) {
            throw new ForbiddenException("只能评价自己参与的订单");
        }
        // 校验被评价人是订单另一方
        boolean isCustomerEvaluating = order.getCustomerId().equals(userId)
                && order.getCompanionId().equals(request.getToUserId());
        boolean isCompanionEvaluating = order.getCompanionId().equals(userId)
                && order.getCustomerId().equals(request.getToUserId());
        if (!isCustomerEvaluating && !isCompanionEvaluating) {
            throw new ForbiddenException("只能评价订单的另一方");
        }
        // 同订单同人只能评价一次
        int count = evaluationMapper.countByOrderAndUser(request.getOrderId(), userId);
        if (count > 0) {
            throw new ForbiddenException("您已评价过该订单");
        }

        Evaluation ev = new Evaluation();
        ev.setOrderId(request.getOrderId());
        ev.setFromUserId(userId);
        ev.setToUserId(request.getToUserId());
        ev.setScore(request.getScore());
        ev.setContent(request.getContent());
        evaluationMapper.insert(ev);

        // 如果评价的是陪诊师（companion_profile.user_id），更新平均评分
        CompanionProfile profile = companionProfileMapper.selectById(order.getCompanionId());
        if (profile != null && profile.getUserId().equals(request.getToUserId())) {
            double avg = evaluationMapper.avgScoreByUserId(request.getToUserId());
            companionProfileMapper.updateRating(profile.getId(), Math.round(avg * 10) / 10.0);
        }

        return ev;
    }

    /** 订单下的评价 */
    public List<Evaluation> getByOrderId(Long orderId) {
        return evaluationMapper.selectByOrderId(orderId);
    }

    /** 陪诊师收到的评价（companionId 为 companion_profile.id） */
    public List<Evaluation> getByCompanionId(Long companionId) {
        return evaluationMapper.selectByCompanionId(companionId);
    }
}
