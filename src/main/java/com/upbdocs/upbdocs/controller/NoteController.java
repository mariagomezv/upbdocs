package com.upbdocs.upbdocs.controller;

import com.upbdocs.upbdocs.model.Note;
import com.upbdocs.upbdocs.model.User;
import com.upbdocs.upbdocs.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Note> createNote(
            @RequestPart("note") Note note,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal User user) throws IOException {
        Note createdNote = noteService.createNoteWithFile(note, file, user);
        return ResponseEntity.ok(createdNote);
    }

    @GetMapping
    public ResponseEntity<List<Note>> getNotes(@AuthenticationPrincipal User user) {
        List<Note> notes = noteService.getNotesByUser(user);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note note, @AuthenticationPrincipal User user) {
        Note updatedNote = noteService.updateNote(id, note, user);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, @AuthenticationPrincipal User user) {
        noteService.deleteNote(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadDocument(@PathVariable Long id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal User user) throws IOException {
        String fileUrl = noteService.uploadDocument(id, file.getBytes(), file.getOriginalFilename(), user);
        return ResponseEntity.ok(fileUrl);
    }
}