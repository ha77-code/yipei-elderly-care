package com.yipei.service;

import com.yipei.constant.RoleConstants;
import com.yipei.entity.LoginVO;
import com.yipei.entity.SysUser;
import com.yipei.entity.UserLoginRequest;
import com.yipei.entity.UserRegisterRequest;
import com.yipei.entity.UserVO;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.SysUserMapper;
import com.yipei.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final SysUserMapper sysUserMapper;

    public UserService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
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
        return toLoginVO(user);
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

    public UserVO updateUserInfo(Long id, String nickname, String phone) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + id);
        }
        sysUserMapper.updateUserInfo(id, nickname, phone);
        SysUser updated = sysUserMapper.selectById(id);
        return toUserVO(updated);
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
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateAt(user.getCreateAt());
        vo.setUpdateAt(user.getUpdateAt());
        return vo;
    }
}
