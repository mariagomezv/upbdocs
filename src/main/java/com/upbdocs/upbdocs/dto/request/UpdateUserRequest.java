package com.upbdocs.upbdocs.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private Long universityId;
}
