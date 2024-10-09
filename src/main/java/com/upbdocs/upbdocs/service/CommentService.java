package com.upbdocs.upbdocs.service;


import com.upbdocs.upbdocs.model.Comment;
import com.upbdocs.upbdocs.model.Note;
import com.upbdocs.upbdocs.model.User;
import com.upbdocs.upbdocs.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NoteService noteService;

    @Transactional
    public Comment addComment(Long noteId, String content, User user) {
        Note note = noteService.getNoteById(noteId);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setNote(note);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByNoteId(Long noteId) {
        return commentRepository.findByNoteId(noteId);
    }
}
