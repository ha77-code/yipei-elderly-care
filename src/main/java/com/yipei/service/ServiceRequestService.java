package com.yipei.service;

import com.yipei.entity.ServiceRequest;
import com.yipei.entity.ServiceRequestCreateRequest;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.ServiceRequestMapper;
import com.yipei.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceRequestService {
    private final ServiceRequestMapper serviceRequestMapper;
    private final SysUserMapper sysUserMapper;

    public ServiceRequestService(ServiceRequestMapper serviceRequestMapper,
                                 SysUserMapper sysUserMapper) {
        this.serviceRequestMapper = serviceRequestMapper;
        this.sysUserMapper = sysUserMapper;
    }

    /** 发布服务需求 */
    public ServiceRequest create(Long customerId, ServiceRequestCreateRequest request) {
        SysUser user = sysUserMapper.selectById(customerId);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + customerId);
        }
        if (!"CUSTOMER".equals(user.getRole())) {
            throw new ForbiddenException("仅客户角色可发布服务需求");
        }
        ServiceRequest sr = new ServiceRequest();
        sr.setCustomerId(customerId);
        sr.setServiceType(request.getServiceType());
        sr.setServiceDate(request.getServiceDate());
        sr.setHospitalName(request.getHospitalName());
        sr.setDepartment(request.getDepartment());
        sr.setRequirement(request.getRequirement());
        sr.setSpecialNotes(request.getSpecialNotes());
        sr.setContactName(request.getContactName());
        sr.setContactPhone(request.getContactPhone());
        sr.setBudget(request.getBudget());
        sr.setStatus("PENDING");
        serviceRequestMapper.insert(sr);
        return sr;
    }

    /** 查看我发布的需求列表 */
    public List<ServiceRequest> listByCustomerId(Long customerId) {
        return serviceRequestMapper.selectByCustomerId(customerId);
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
