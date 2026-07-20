package com.yipei.service;

import com.yipei.constant.RoleConstants;
import com.yipei.entity.AuditRecord;
import com.yipei.entity.CompanionApplyRequest;
import com.yipei.entity.CompanionProfile;
import com.yipei.entity.CompanionVO;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.AuditRecordMapper;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanionService {
    private final CompanionProfileMapper companionProfileMapper;
    private final SysUserMapper sysUserMapper;
    private final AuditRecordMapper auditRecordMapper;
    private final UserNotificationService notificationService;

    public CompanionService(CompanionProfileMapper companionProfileMapper,
                            SysUserMapper sysUserMapper,
                            AuditRecordMapper auditRecordMapper,
                            UserNotificationService notificationService) {
        this.companionProfileMapper = companionProfileMapper;
        this.sysUserMapper = sysUserMapper;
        this.auditRecordMapper = auditRecordMapper;
        this.notificationService = notificationService;
    }

    /** 获取我的入驻资料 */
    public CompanionProfile getMyProfile(Long userId) {
        return companionProfileMapper.selectByUserId(userId);
    }

    /** 提交入驻申请 */
    @Transactional
    public CompanionProfile apply(Long userId, CompanionApplyRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || !RoleConstants.COMPANION.equals(user.getRole())) {
            throw new ForbiddenException("仅陪诊师角色可提交入驻申请");
        }
        CompanionProfile exist = companionProfileMapper.selectByUserId(userId);
        if (exist != null) {
            throw new ForbiddenException("您已提交过入驻资料，请前往修改");
        }
        CompanionProfile profile = new CompanionProfile();
        profile.setUserId(userId);
        profile.setRealName(request.getRealName());
        profile.setAvatar(request.getAvatar());
        profile.setIntroduction(request.getIntroduction());
        profile.setServiceArea(request.getServiceArea());
        profile.setServiceTypes(request.getServiceTypes());
        profile.setTraits(request.getTraits());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setAuditStatus(0);
        companionProfileMapper.insert(profile);
        notificationService.sendToRole(RoleConstants.ADMIN, "COMPANION_AUDIT_PENDING", "有新的陪诊师资料待审核",
                (profile.getRealName() == null ? "一位陪诊师" : profile.getRealName()) + "提交了入驻资料，请及时审核。", profile.getId());
        return profile;
    }

    /** 修改入驻资料 */
    @Transactional
    public void updateProfile(Long userId, CompanionApplyRequest request) {
        CompanionProfile profile = companionProfileMapper.selectByUserId(userId);
        if (profile == null) {
            throw new NotFoundException("未找到入驻资料，请先提交申请");
        }
        companionProfileMapper.update(profile.getId(),
                request.getRealName(), request.getAvatar(), request.getIntroduction(),
                request.getServiceArea(), request.getServiceTypes(), request.getTraits(),
                request.getExperienceYears());
        notificationService.sendToRole(RoleConstants.ADMIN, "COMPANION_AUDIT_PENDING", "有陪诊师资料待重新审核",
                (request.getRealName() == null ? "一位陪诊师" : request.getRealName()) + "更新了入驻资料，请及时审核。", profile.getId());
    }

    /** 审核通过的陪诊师列表 */
    public List<CompanionVO> listApproved(String serviceArea, String serviceType, String traits) {
        return companionProfileMapper.selectApproved(serviceArea, serviceType, traits);
    }

    /** 陪诊师详情 */
    public CompanionProfile getDetail(Long id) {
        CompanionProfile profile = companionProfileMapper.selectById(id);
        if (profile == null) {
            throw new NotFoundException("陪诊师不存在，ID: " + id);
        }
        if (profile.getAuditStatus() == null || profile.getAuditStatus() != 1) {
            throw new NotFoundException("该陪诊师暂未通过审核");
        }
        return profile;
    }

    /* ===== 管理员操作 ===== */

    /** 待审核列表 */
    public List<CompanionProfile> listPending() {
        return companionProfileMapper.selectPending();
    }

    /** 审核陪诊师 */
    @Transactional
    public void audit(Long id, Long auditorId, Integer auditStatus, String remark) {
        CompanionProfile profile = companionProfileMapper.selectById(id);
        if (profile == null) {
            throw new NotFoundException("陪诊师资料不存在，ID: " + id);
        }
        if (profile.getAuditStatus() == null || profile.getAuditStatus() != 0) {
            throw new ForbiddenException("该资料当前不在待审核状态，无法审核");
        }
        if (auditStatus == null || (auditStatus != 1 && auditStatus != 2)) {
            throw new ForbiddenException("审核状态只能为 1（通过）或 2（拒绝）");
        }
        companionProfileMapper.updateAuditStatus(id, auditStatus);

        AuditRecord record = new AuditRecord();
        record.setBusinessType("companion_profile");
        record.setBusinessId(id);
        record.setAuditorId(auditorId);
        record.setAuditStatus(auditStatus);
        record.setRemark(remark);
        auditRecordMapper.insert(record);
        String suffix = remark == null || remark.isBlank() ? "" : " 备注：" + remark.trim();
        notificationService.send(profile.getUserId(),
                auditStatus == 1 ? "COMPANION_AUDIT_APPROVED" : "COMPANION_AUDIT_REJECTED",
                auditStatus == 1 ? "陪诊师认证已通过" : "陪诊师认证未通过",
                (auditStatus == 1 ? "您的入驻资料已通过审核，可以开始申请接单。" : "您的入驻资料未通过审核，请修改后重新提交。") + suffix,
                profile.getId());
    }
}
