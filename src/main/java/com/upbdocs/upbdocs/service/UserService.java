package com.upbdocs.upbdocs.service;

import com.upbdocs.upbdocs.dto.request.UpdateUserRequest;
import com.upbdocs.upbdocs.model.User;
import com.upbdocs.upbdocs.model.University;
import com.upbdocs.upbdocs.dto.response.UserDTO;
import com.upbdocs.upbdocs.repository.UserRepository;
import com.upbdocs.upbdocs.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;

    public UserDTO convertToUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        if (user.getUniversity() != null) {
            userDTO.setUniversity(user.getUniversity());
        } else {
            userDTO.setUniversity(null);
        }
        return userDTO;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToUserDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateUserRequest.getEmail() != null) {
            user.setEmail(updateUserRequest.getEmail());
        }

        if (updateUserRequest.getUniversityId() != null) {
            University university = universityRepository.findById(updateUserRequest.getUniversityId())
                    .orElseThrow(() -> new RuntimeException("University not found"));
            user.setUniversity(university);
        }

        User updatedUser = userRepository.save(user);
        return convertToUserDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}