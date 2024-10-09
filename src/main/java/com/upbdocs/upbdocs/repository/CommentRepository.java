package com.upbdocs.upbdocs.repository;


import com.upbdocs.upbdocs.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNoteId(Long noteId);
}
