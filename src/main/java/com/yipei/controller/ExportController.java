package com.yipei.controller;

import com.yipei.mapper.ServiceOrderMapper;
import com.yipei.mapper.SysUserMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {
    private final SysUserMapper sysUserMapper;
    private final ServiceOrderMapper serviceOrderMapper;

    public ExportController(SysUserMapper sysUserMapper, ServiceOrderMapper serviceOrderMapper) {
        this.sysUserMapper = sysUserMapper;
        this.serviceOrderMapper = serviceOrderMapper;
    }

    /** 导出用户 CSV */
    @GetMapping("/users")
    public void exportUsers(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=users.csv");
        // BOM for Excel UTF-8 compatibility
        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF);
        PrintWriter w = response.getWriter();
        w.println("ID,用户名,昵称,手机号,角色,状态,注册时间");
        sysUserMapper.selectAll().forEach(u ->
                w.printf("%d,%s,%s,%s,%s,%d,%s%n",
                        u.getId(), u.getUsername(), u.getNickname(), u.getPhone(),
                        u.getRole(), u.getStatus(), u.getCreateAt()));
        w.flush();
    }

    /** 导出订单 CSV */
    @GetMapping("/orders")
    public void exportOrders(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=orders.csv");
        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF);
        PrintWriter w = response.getWriter();
        w.println("ID,需求ID,客户ID,陪诊师ID,价格,平台费,陪诊师收入,状态,接单时间,开始时间,完成时间,取消原因,创建时间");
        serviceOrderMapper.selectForAdmin(null, null, null).forEach(o ->
                w.printf("%d,%d,%d,%d,%.2f,%.2f,%.2f,%s,%s,%s,%s,%s,%s%n",
                        o.getId(), o.getRequestId(), o.getCustomerId(), o.getCompanionId(),
                        o.getServicePrice(), o.getPlatformFee(), o.getCompanionIncome(),
                        o.getStatus(), o.getAcceptedAt(), o.getStartedAt(),
                        o.getCompletedAt(), o.getCancelReason(), o.getCreatedAt()));
        w.flush();
    }
}
