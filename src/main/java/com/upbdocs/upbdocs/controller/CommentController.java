package com.upbdocs.upbdocs.controller;

import com.upbdocs.upbdocs.model.Comment;
import com.upbdocs.upbdocs.model.User;
import com.upbdocs.upbdocs.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes/{noteId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(@PathVariable Long noteId, @RequestBody String content, @AuthenticationPrincipal User user) {
        Comment comment = commentService.addComment(noteId, content, user);
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long noteId) {
        List<Comment> comments = commentService.getCommentsByNoteId(noteId);
        return ResponseEntity.ok(comments);
    }
}