package com.upbdocs.upbdocs.model;

import com.upbdocs.upbdocs.dto.response.UserDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Transient
    private UserDTO creatorDTO;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "note_users",
        joinColumns = @JoinColumn(name = "note_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> sharedUsers = new ArrayList<>();

    @Transient
    private List<UserDTO> sharedUsersDTO = new ArrayList<>();

    @Column(name = "document_url")
    private String documentUrl;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
