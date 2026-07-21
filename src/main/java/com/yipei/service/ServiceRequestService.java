package com.yipei.service;

import com.yipei.entity.AuditRecord;
import com.yipei.entity.CompanionProfile;
import com.yipei.entity.CompanionVO;
import com.yipei.entity.RequestPoolVO;
import com.yipei.entity.ServiceRequest;
import com.yipei.constant.RoleConstants;
import com.yipei.entity.ServiceRequestCreateRequest;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.AuditRecordMapper;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.ServiceRequestMapper;
import com.yipei.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ServiceRequestService {
    private final ServiceRequestMapper serviceRequestMapper;
    private final SysUserMapper sysUserMapper;
    private final AiSummaryService aiSummaryService;
    private final AuditRecordMapper auditRecordMapper;
    private final OrderService orderService;
    private final UserNotificationService notificationService;
    private final CompanionProfileMapper companionProfileMapper;

    public ServiceRequestService(ServiceRequestMapper serviceRequestMapper,
                                 SysUserMapper sysUserMapper,
                                 AiSummaryService aiSummaryService,
                                 AuditRecordMapper auditRecordMapper,
                                 OrderService orderService,
                                 UserNotificationService notificationService,
                                 CompanionProfileMapper companionProfileMapper) {
        this.serviceRequestMapper = serviceRequestMapper;
        this.sysUserMapper = sysUserMapper;
        this.aiSummaryService = aiSummaryService;
        this.auditRecordMapper = auditRecordMapper;
        this.orderService = orderService;
        this.notificationService = notificationService;
        this.companionProfileMapper = companionProfileMapper;
    }

    /** 发布服务需求 */
    @Transactional
    public ServiceRequest create(Long customerId, ServiceRequestCreateRequest request) {
        SysUser user = sysUserMapper.selectById(customerId);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + customerId);
        }
        if (!RoleConstants.CUSTOMER.equals(user.getRole())) {
            throw new ForbiddenException("仅客户角色可发布服务需求");
        }
        // 指定下单（通道B）：审核通过后会按预算自动生成订单，若此时预算无效会导致审核事务回滚、
        // 需求永远卡在待审核。所以在发布阶段就要求有效预算 + 指定的陪诊师已通过审核。
        if (request.getPreferredCompanionId() != null) {
            if (request.getBudget() == null || request.getBudget().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ForbiddenException("指定陪诊师时请填写大于 0 的预算");
            }
            CompanionProfile preferred = companionProfileMapper.selectById(request.getPreferredCompanionId());
            if (preferred == null || preferred.getAuditStatus() == null || preferred.getAuditStatus() != 1) {
                throw new NotFoundException("指定的陪诊师不存在或未通过审核");
            }
        }
        ServiceRequest sr = new ServiceRequest();
        sr.setCustomerId(customerId);
        sr.setServiceType(request.getServiceType());
        sr.setServiceDate(request.getServiceDate());
        sr.setHospitalName(request.getHospitalName());
        sr.setDepartment(request.getDepartment());
        sr.setRequirement(request.getRequirement());
        sr.setSpecialNotes(request.getSpecialNotes());
        sr.setAiSummary(normalizeAiSummary(request.getAiSummary()));
        sr.setPreferredTraits(request.getPreferredTraits());
        sr.setNeedPickup(request.getNeedPickup());
        sr.setContactName(request.getContactName());
        sr.setContactPhone(request.getContactPhone());
        sr.setBudget(request.getBudget());
        sr.setStatus("PENDING");
        // 所有需求（含指定下单）统一进入待审核队列；通道B 记录客户指定的陪诊师，
        // 审核通过后据此自动生成待接单订单。
        sr.setAuditStatus(0);
        sr.setPreferredCompanionId(request.getPreferredCompanionId());
        serviceRequestMapper.insert(sr);
        if (sr.getAiSummary() == null) {
            aiSummaryService.generate(sr).ifPresent(summary -> {
                sr.setAiSummary(summary);
                serviceRequestMapper.updateAiSummary(sr.getId(), summary);
            });
        }
        notificationService.sendToRole(RoleConstants.ADMIN, "REQUEST_AUDIT_PENDING", "有新的需求待审核",
                "需求 #" + sr.getId() + "（" + sr.getServiceType() + "）已提交，请及时审核。", sr.getId());
        return sr;
    }

    /**
     * 为指定需求推荐最适配的陪诊师（最多 topN 个）。
     * 按服务类型、服务区域、性格偏好、评分、经验多维打分，达不到最低分则不返回，前端据此决定是否弹出。
     */
    public List<CompanionVO> matchCompanions(Long requestId, Long customerId, int topN) {
        ServiceRequest request = serviceRequestMapper.selectById(requestId);
        if (request == null) {
            throw new NotFoundException("服务需求不存在，ID: " + requestId);
        }
        if (customerId != null && !customerId.equals(request.getCustomerId())) {
            throw new ForbiddenException("只能查看自己需求的推荐陪诊师");
        }
        // 已指定陪诊师的需求无需再推荐
        if (request.getPreferredCompanionId() != null) {
            return List.of();
        }
        return rankCompanions(request, topN);
    }

    /**
     * 根据一份（尚未保存的）需求内容即时推荐陪诊师，供发布表单里的“智能推荐”预览使用。
     * 只读打分，不落库；无合适则返回空。
     */
    public List<CompanionVO> previewMatches(ServiceRequest draft, int topN) {
        if (draft == null) {
            return List.of();
        }
        return rankCompanions(draft, topN);
    }

    /** 对全部审核通过的陪诊师打分排序，返回达到阈值的前 topN 个 */
    private List<CompanionVO> rankCompanions(ServiceRequest request, int topN) {
        List<CompanionVO> pool = companionProfileMapper.selectApproved(null, null, null);
        List<CompanionVO> scored = new ArrayList<>();
        for (CompanionVO c : pool) {
            scoreCompanion(c, request);
            if (c.getMatchScore() != null && c.getMatchScore() >= MATCH_THRESHOLD) {
                scored.add(c);
            }
        }
        scored.sort(Comparator
                .comparingInt(CompanionVO::getMatchScore).reversed()
                .thenComparing(c -> ratingValue(c.getRating()), Comparator.reverseOrder()));
        return scored.size() > topN ? scored.subList(0, topN) : scored;
    }

    /** 达到该分数才视为“比较适配”，否则不推荐 */
    private static final int MATCH_THRESHOLD = 45;

    /** 多维打分：类型 50，需求描述契合最高 40，区域 20，性格每个 10（上限 20），评分最高 10，经验最高 8 */
    private void scoreCompanion(CompanionVO c, ServiceRequest request) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        String types = c.getServiceTypes() == null ? "" : c.getServiceTypes();
        if (typeMatches(request.getServiceType(), types)) {
            score += 50;
            reasons.add("擅长" + request.getServiceType());
        }

        // 需求描述/特殊说明 与 陪诊师简介+服务类型+性格 的中文二元词重叠度
        String requestText = joinNonBlank(request.getRequirement(), request.getSpecialNotes());
        String profileText = joinNonBlank(c.getIntroduction(), c.getServiceTypes(), c.getTraits());
        int textScore = textFitScore(requestText, profileText);
        if (textScore > 0) {
            score += textScore;
            if (textScore >= 24) {
                reasons.add("资历与需求高度契合");
            } else if (textScore >= 12) {
                reasons.add("服务经验契合需求");
            }
        }

        String area = c.getServiceArea() == null ? "" : c.getServiceArea();
        String hospital = request.getHospitalName() == null ? "" : request.getHospitalName();
        if (!area.isBlank() && !hospital.isBlank() && shareRegion(area, hospital)) {
            score += 20;
            reasons.add("服务区域覆盖" + area);
        }

        int traitHits = 0;
        for (String want : tokenize(request.getPreferredTraits())) {
            String traits = c.getTraits() == null ? "" : c.getTraits();
            if (traits.contains(want)) {
                traitHits++;
                if (traitHits <= 2) {
                    reasons.add("性格契合（" + want + "）");
                    score += 10;
                }
            }
        }

        double rating = ratingValue(c.getRating());
        if (rating >= 4.5) { score += 10; reasons.add("口碑评分 " + rating); }
        else if (rating >= 4.0) { score += 6; }
        else if (rating > 0) { score += 2; }

        int years = c.getExperienceYears() == null ? 0 : c.getExperienceYears();
        if (years >= 5) { score += 8; reasons.add(years + " 年经验"); }
        else if (years >= 2) { score += 4; }

        c.setMatchScore(score);
        c.setMatchReason(reasons.isEmpty() ? "综合资历匹配" : String.join(" · ", reasons));
    }

    /**
     * 服务类型匹配：需求词汇（门诊陪诊/检查陪同/住院陪护/取药代办）与陪诊师标签
     * （陪诊/检查/住院/取药…）用词不完全一致，用 2 字子串重叠判定，比精确 contains 更鲁棒。
     */
    private boolean typeMatches(String requestType, String companionTypes) {
        if (requestType == null || requestType.isBlank() || companionTypes.isBlank()) {
            return false;
        }
        if (companionTypes.contains(requestType)) {
            return true;
        }
        for (int i = 0; i + 2 <= requestType.length(); i++) {
            String gram = requestType.substring(i, i + 2);
            if (companionTypes.contains(gram)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 需求描述与陪诊师资历的文本契合度（0~40）。取需求文本的中文二元词集合，
     * 计算落在陪诊师资历文本中的比例。直接复制陪诊师简介到需求里时重叠率接近 1，得满分。
     * 命中词过少（&lt;3）视为偶然重叠，不计分，避免泛泛需求把所有人推过阈值。
     */
    private int textFitScore(String requestText, String profileText) {
        if (requestText.isBlank() || profileText.isBlank()) {
            return 0;
        }
        java.util.Set<String> grams = chineseBigrams(requestText);
        if (grams.size() < 3) {
            return 0;
        }
        int hits = 0;
        for (String g : grams) {
            if (profileText.contains(g)) {
                hits++;
            }
        }
        if (hits < 3) {
            return 0;
        }
        double ratio = (double) hits / grams.size();
        return (int) Math.round(ratio * 40);
    }

    /** 抽取文本中相邻中文字符组成的二元词集合（去重） */
    private java.util.Set<String> chineseBigrams(String text) {
        java.util.Set<String> grams = new java.util.LinkedHashSet<>();
        for (int i = 0; i + 1 < text.length(); i++) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);
            if (isChinese(a) && isChinese(b)) {
                grams.add(text.substring(i, i + 2));
            }
        }
        return grams;
    }

    private boolean isChinese(char c) {
        return c >= '一' && c <= '鿿';
    }

    private String joinNonBlank(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.isBlank()) {
                sb.append(p).append(' ');
            }
        }
        return sb.toString().trim();
    }

    /** 区域与医院名共享市/区词根即视为覆盖 */
    private boolean shareRegion(String area, String hospital) {
        for (String token : area.split("[市区县省,，、\\s/]+")) {
            String t = token.trim();
            if (t.length() >= 2 && hospital.contains(t)) {
                return true;
            }
        }
        return false;
    }

    private List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (String t : text.split("[,，、\\s/]+")) {
            if (!t.isBlank()) {
                out.add(t.trim());
            }
        }
        return out;
    }

    private double ratingValue(BigDecimal rating) {
        return rating == null ? 0d : rating.doubleValue();
    }

    private String normalizeAiSummary(String summary) {
        if (summary == null || summary.isBlank()) {
            return null;
        }
        String normalized = summary.replaceAll("\\s+", " ").trim();
        return normalized.length() > 800 ? normalized.substring(0, 800) : normalized;
    }

    /** 查看我发布的需求列表 */
    public List<ServiceRequest> listByCustomerId(Long customerId) {
        return serviceRequestMapper.selectByCustomerId(customerId);
    }

    /** 管理员查看全部需求，支持按状态、服务类型、审核状态筛选 */
    public List<ServiceRequest> listAll(String status, String serviceType, Integer auditStatus) {
        return serviceRequestMapper.selectAll(status, serviceType, auditStatus);
    }

    /** 管理员待审核需求列表 */
    public List<ServiceRequest> listPendingAudit() {
        return serviceRequestMapper.selectAll(null, null, 0);
    }

    /** 管理员审核需求（auditStatus：1 通过 / 2 拒绝） */
    @Transactional
    public void audit(Long id, Long auditorId, Integer auditStatus, String remark) {
        ServiceRequest request = serviceRequestMapper.selectById(id);
        if (request == null) {
            throw new NotFoundException("服务需求不存在，ID: " + id);
        }
        if (request.getAuditStatus() == null || request.getAuditStatus() != 0) {
            throw new ForbiddenException("该需求当前不在待审核状态，无法审核");
        }
        if (auditStatus == null || (auditStatus != 1 && auditStatus != 2)) {
            throw new ForbiddenException("审核状态只能为 1（通过）或 2（拒绝）");
        }
        serviceRequestMapper.updateAuditStatus(id, auditStatus, remark);
        if (auditStatus == 2) {
            serviceRequestMapper.updateStatus(id, "CLOSED");
        }

        AuditRecord record = new AuditRecord();
        record.setBusinessType("service_request");
        record.setBusinessId(id);
        record.setAuditorId(auditorId);
        record.setAuditStatus(auditStatus);
        record.setRemark(remark);
        auditRecordMapper.insert(record);

        // 通道B：指定下单需求审核通过后，自动为客户选定的陪诊师生成待接单订单
        if (auditStatus == 1 && request.getPreferredCompanionId() != null) {
            orderService.createDirectedOrder(
                    request.getCustomerId(), id, request.getPreferredCompanionId(), request.getBudget());
        }

        if (auditStatus == 1) {
            String content = request.getPreferredCompanionId() == null
                    ? "您的陪诊需求已通过审核，现已进入需求广场。"
                    : "您的指定陪诊需求已通过审核，订单已发送给指定陪诊师。";
            notificationService.send(request.getCustomerId(), "REQUEST_AUDIT_APPROVED", "需求审核已通过", content, id);
        } else {
            String suffix = remark == null || remark.isBlank() ? "" : " 原因：" + remark.trim();
            notificationService.send(request.getCustomerId(), "REQUEST_AUDIT_REJECTED", "需求审核未通过",
                    "您的陪诊需求未通过审核。" + suffix, id);
        }
    }

    /** 陪诊师需求广场：已通过审核、仍待匹配的需求 */
    public List<RequestPoolVO> listPool(Long companionProfileId, String serviceType, String keyword) {
        return serviceRequestMapper.selectPool(companionProfileId, serviceType, keyword);
    }

    /** 查看需求详情 */
    public ServiceRequest getById(Long id) {
        ServiceRequest request = serviceRequestMapper.selectById(id);
        if (request == null) {
            throw new NotFoundException("服务需求不存在，ID: " + id);
        }
        return request;
    }

    /** 取消需求 */
    public void cancel(Long id, Long userId) {
        ServiceRequest request = serviceRequestMapper.selectById(id);
        if (request == null) {
            throw new NotFoundException("服务需求不存在，ID: " + id);
        }
        if (!request.getCustomerId().equals(userId)) {
            throw new ForbiddenException("只能取消自己的服务需求");
        }
        int orderCount = serviceRequestMapper.countOrdersByRequestId(id);
        if (orderCount > 0) {
            throw new ForbiddenException("该需求已生成订单，不能直接取消，请先取消订单");
        }
        if ("CANCELLED".equals(request.getStatus()) || "CLOSED".equals(request.getStatus())) {
            throw new ForbiddenException("当前状态（" + request.getStatus() + "）不允许取消");
        }
        serviceRequestMapper.updateStatus(id, "CANCELLED");
    }
}
