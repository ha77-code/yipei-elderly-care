package com.yipei.service;

import com.yipei.entity.SysUser;
import com.yipei.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    private final SysUserMapper sysUserMapper;

    public UserService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public List<SysUser> getUserList(){
        return sysUserMapper.selectAll();
    }
}
