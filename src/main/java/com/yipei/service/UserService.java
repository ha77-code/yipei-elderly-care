package com.yipei.service;

import com.yipei.entity.LoginRequest;
import com.yipei.entity.RegisterRequest;
import com.yipei.entity.SysUser;
import com.yipei.entity.UserVO;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final SysUserMapper sysUserMapper;

    /** 简易内存 token 存储，生产环境应使用 Redis */
    private static final Map<String, Long> TOKEN_STORE = new ConcurrentHashMap<>();

    public UserService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    /* ===== 登录注册 ===== */

    public Map<String, Object> login(LoginRequest request) {
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new ForbiddenException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new ForbiddenException("账号已被禁用");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        TOKEN_STORE.put(token, user.getId());
        return Map.of("token", token, "user", toUserVO(user));
    }

    public void register(RegisterRequest request) {
        SysUser exist = sysUserMapper.selectByUsername(request.getUsername());
        if (exist != null) {
            throw new ForbiddenException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(1);
        sysUserMapper.insert(user);
    }

    /* ===== 用户查询 ===== */

    public UserVO getCurrentUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + userId);
        }
        return toUserVO(user);
    }

    /** 从 Authorization header 解析 token 获取当前用户 ID */
    public Long resolveUserId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return TOKEN_STORE.get(token);
        }
        return null;
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

    public UserVO updateUserStatus(Long id, int status, String operatorRole) {
        if (!"ADMIN".equals(operatorRole)) {
            throw new ForbiddenException("仅管理员可操作");
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

    /* ===== 工具方法 ===== */

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
