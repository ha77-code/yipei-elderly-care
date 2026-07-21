package com.yipei.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceApplication {
    private Long id;
    private Long requestId;
    /** companion_profile.id */
    private Long companionId;
    private String message;
    /** PENDING, ACCEPTED, REJECTED, WITHDRAWN */
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
