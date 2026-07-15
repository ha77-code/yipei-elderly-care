package com.yipei.service;

import com.yipei.entity.SysUser;
import com.yipei.entity.UserVO;
import com.yipei.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final SysUserMapper sysUserMapper;

    public UserService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public List<UserVO> getUserList() {
        List<SysUser> users = sysUserMapper.selectAll();
        return users.stream().map(this::toUserVO).collect(Collectors.toList());
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
