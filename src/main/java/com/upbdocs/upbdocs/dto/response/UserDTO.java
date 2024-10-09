package com.upbdocs.upbdocs.dto.response;

import com.upbdocs.upbdocs.model.Role;
import com.upbdocs.upbdocs.model.University;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private University university;
}
