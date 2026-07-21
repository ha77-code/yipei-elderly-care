package com.yipei.service;

import com.yipei.constant.RoleConstants;
import com.yipei.entity.AuditRecord;
import com.yipei.entity.LoginVO;
import com.yipei.entity.SysUser;
import com.yipei.entity.UserLoginRequest;
import com.yipei.entity.UserRegisterRequest;
import com.yipei.entity.UserVO;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.AuditRecordMapper;
import com.yipei.mapper.SysUserMapper;
import com.yipei.security.JwtTokenProvider;
import com.yipei.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final SysUserMapper sysUserMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileStorageService fileStorageService;
    private final AuditRecordMapper auditRecordMapper;
    private final UserNotificationService notificationService;

    public UserService(SysUserMapper sysUserMapper,
                       JwtTokenProvider jwtTokenProvider,
                       FileStorageService fileStorageService,
                       AuditRecordMapper auditRecordMapper,
                       UserNotificationService notificationService) {
        this.sysUserMapper = sysUserMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.fileStorageService = fileStorageService;
        this.auditRecordMapper = auditRecordMapper;
        this.notificationService = notificationService;
    }

    /* ===== 登录 ===== */

    public LoginVO login(UserLoginRequest request) {
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null || !PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new ForbiddenException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new ForbiddenException("账号已被禁用");
        }
        LoginVO vo = toLoginVO(user);
        vo.setToken(jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole()));
        return vo;
    }

    /* ===== 注册 ===== */

    public UserVO register(UserRegisterRequest request) {
        if (!RoleConstants.isValid(request.getRole())) {
            throw new ForbiddenException("角色只能是 CUSTOMER、COMPANION 或 ADMIN");
        }
        SysUser exist = sysUserMapper.selectByUsername(request.getUsername());
        if (exist != null) {
            throw new ForbiddenException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(1);
        sysUserMapper.insert(user);
        return toUserVO(user);
    }

    /* ===== 用户查询 ===== */

    public UserVO getCurrentUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + userId);
        }
        return toUserVO(user);
    }

    public List<UserVO> getUserList() {
        List<SysUser> users = sysUserMapper.selectAll();
        return users.stream().map(this::toUserVO).collect(Collectors.toList());
    }

    public UserVO getUserById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + id);
        }
        return toUserVO(user);
    }

    /* ===== 管理员操作 ===== */

    public void requireLogin(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new ForbiddenException("请先登录");
        }
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new ForbiddenException("账号已被禁用");
        }
    }

    public void requireAdmin(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || !RoleConstants.ADMIN.equals(user.getRole())) {
            throw new ForbiddenException("仅管理员可操作");
        }
    }

    public UserVO updateUserStatus(Long id, int status, Long operatorId) {
        requireAdmin(operatorId);
        if (status != 0 && status != 1) {
            throw new ForbiddenException("用户状态只能为 0（禁用）或 1（正常）");
        }
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + id);
        }
        sysUserMapper.updateStatus(id, status);
        user.setStatus(status);
        return toUserVO(user);
    }

    /* ===== 个人信息修改 ===== */

    public UserVO updateUserInfo(Long id, String nickname, String phone, String avatar) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + id);
        }
        sysUserMapper.updateUserInfo(id, nickname, phone, avatar);
        SysUser updated = sysUserMapper.selectById(id);
        return toUserVO(updated);
    }

    /* ===== 头像上传 ===== */

    /** 上传新头像，进入待审核状态（不立即替换已展示头像），返回更新后的用户信息 */
    @Transactional
    public UserVO uploadAvatar(Long id, MultipartFile file) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + id);
        }
        String url = fileStorageService.storeImage(file);
        sysUserMapper.updatePendingAvatar(id, url);
        notificationService.sendToRole(RoleConstants.ADMIN, "AVATAR_AUDIT_PENDING", "有新的头像待审核",
                (user.getNickname() == null ? user.getUsername() : user.getNickname()) + "提交了新头像，请及时审核。", id);
        return toUserVO(sysUserMapper.selectById(id));
    }

    /* ===== 头像审核（管理员） ===== */

    /** 待审核头像列表 */
    public List<UserVO> listPendingAvatars(Long operatorId) {
        requireAdmin(operatorId);
        return sysUserMapper.selectPendingAvatars().stream()
                .map(this::toUserVO).collect(Collectors.toList());
    }

    /** 审核头像：auditStatus 1 通过 / 2 拒绝 */
    @Transactional
    public void auditAvatar(Long userId, Long auditorId, Integer auditStatus, String remark) {
        requireAdmin(auditorId);
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + userId);
        }
        if (user.getAvatarAuditStatus() == null || user.getAvatarAuditStatus() != 0) {
            throw new ForbiddenException("该用户当前没有待审核的头像");
        }
        if (auditStatus == null || (auditStatus != 1 && auditStatus != 2)) {
            throw new ForbiddenException("审核状态只能为 1（通过）或 2（拒绝）");
        }
        if (auditStatus == 1) {
            sysUserMapper.approveAvatar(userId);
        } else {
            sysUserMapper.rejectAvatar(userId);
        }
        AuditRecord record = new AuditRecord();
        record.setBusinessType("USER_AVATAR");
        record.setBusinessId(userId);
        record.setAuditorId(auditorId);
        record.setAuditStatus(auditStatus);
        record.setRemark(remark);
        auditRecordMapper.insert(record);
        String suffix = remark == null || remark.isBlank() ? "" : " 备注：" + remark.trim();
        notificationService.send(userId,
                auditStatus == 1 ? "AVATAR_AUDIT_APPROVED" : "AVATAR_AUDIT_REJECTED",
                auditStatus == 1 ? "头像审核已通过" : "头像审核未通过",
                (auditStatus == 1 ? "您的新头像已通过审核并生效。" : "您的新头像未通过审核，当前头像保持不变。") + suffix,
                userId);
    }

    /* ===== 修改密码 ===== */

    public void updatePassword(Long id, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + id);
        }
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new ForbiddenException("旧密码错误");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new ForbiddenException("新密码不能为空");
        }
        sysUserMapper.updatePassword(id, PasswordUtil.encode(newPassword));
    }

    /* ===== VO 转换 ===== */

    private LoginVO toLoginVO(SysUser user) {
        LoginVO vo = new LoginVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setPendingAvatar(user.getPendingAvatar());
        vo.setAvatarAuditStatus(user.getAvatarAuditStatus());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateAt(user.getCreateAt());
        vo.setUpdateAt(user.getUpdateAt());
        return vo;
    }

    private UserVO toUserVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setPendingAvatar(user.getPendingAvatar());
        vo.setAvatarAuditStatus(user.getAvatarAuditStatus());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateAt(user.getCreateAt());
        vo.setUpdateAt(user.getUpdateAt());
        return vo;
    }
}
