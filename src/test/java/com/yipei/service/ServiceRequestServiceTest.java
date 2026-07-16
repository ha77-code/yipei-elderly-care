package com.yipei.service;

import com.yipei.constant.RoleConstants;
import com.yipei.entity.ServiceRequest;
import com.yipei.entity.ServiceRequestCreateRequest;
import com.yipei.entity.SysUser;
import com.yipei.mapper.ServiceRequestMapper;
import com.yipei.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceRequestServiceTest {

    @Mock
    private ServiceRequestMapper serviceRequestMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private AiSummaryService aiSummaryService;

    @InjectMocks
    private ServiceRequestService serviceRequestService;

    @Test
    void createPersistsGeneratedAiSummary() {
        SysUser customer = new SysUser();
        customer.setId(2L);
        customer.setRole(RoleConstants.CUSTOMER);
        when(sysUserMapper.selectById(2L)).thenReturn(customer);
        doAnswer(invocation -> {
            invocation.getArgument(0, ServiceRequest.class).setId(100L);
            return 1;
        }).when(serviceRequestMapper).insert(any(ServiceRequest.class));
        when(aiSummaryService.generate(any(ServiceRequest.class)))
                .thenReturn(Optional.of("按时到院完成检查陪同，注意行动不便。"));

        ServiceRequest created = serviceRequestService.create(2L, createRequest());

        assertEquals("按时到院完成检查陪同，注意行动不便。", created.getAiSummary());
        ArgumentCaptor<ServiceRequest> requestCaptor = ArgumentCaptor.forClass(ServiceRequest.class);
        verify(serviceRequestMapper).insert(requestCaptor.capture());
        assertEquals("门诊陪诊", requestCaptor.getValue().getServiceType());
        verify(serviceRequestMapper).updateAiSummary(
                eq(100L), eq("按时到院完成检查陪同，注意行动不便。"));
    }

    @Test
    void createUsesConfirmedAiSummaryWithoutGeneratingAgain() {
        SysUser customer = new SysUser();
        customer.setId(2L);
        customer.setRole(RoleConstants.CUSTOMER);
        when(sysUserMapper.selectById(2L)).thenReturn(customer);
        doAnswer(invocation -> {
            invocation.getArgument(0, ServiceRequest.class).setId(101L);
            return 1;
        }).when(serviceRequestMapper).insert(any(ServiceRequest.class));
        ServiceRequestCreateRequest request = createRequest();
        request.setAiSummary("已确认的陪诊摘要");

        ServiceRequest created = serviceRequestService.create(2L, request);

        assertEquals("已确认的陪诊摘要", created.getAiSummary());
        verify(aiSummaryService, never()).generate(any(ServiceRequest.class));
        verify(serviceRequestMapper, never()).updateAiSummary(any(), any());
    }
    private ServiceRequestCreateRequest createRequest() {
        ServiceRequestCreateRequest request = new ServiceRequestCreateRequest();
        request.setServiceType("门诊陪诊");
        request.setServiceDate(LocalDateTime.of(2026, 7, 20, 9, 0));
        request.setHospitalName("第一人民医院");
        request.setDepartment("心内科");
        request.setRequirement("陪同老人完成检查和取报告");
        request.setSpecialNotes("行动不便");
        request.setContactName("测试用户");
        request.setContactPhone("13800000000");
        return request;
    }
}
