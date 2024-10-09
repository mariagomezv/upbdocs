package com.upbdocs.upbdocs.service;

import com.upbdocs.upbdocs.model.Note;
import com.upbdocs.upbdocs.model.User;
import com.upbdocs.upbdocs.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserService userService;

    @Transactional
    public Note createNoteWithFile(Note note, MultipartFile file, User creator) throws IOException {
        note.setCreator(creator);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            String fileUrl = s3Service.uploadFile(file.getBytes(), file.getOriginalFilename());
            note.setDocumentUrl(fileUrl);
        }

        Note savedNote = noteRepository.save(note);
        return convertToNoteWithDTO(savedNote);
    }


    public Note getNoteById(Long id) {
        return noteRepository.findById(id).orElseThrow(() -> new RuntimeException("Note not found"));
    }

    public List<Note> getNotesByUser(User user) {
        List<Note> notes = noteRepository.findByCreatorId(user.getId());
        return notes.stream().map(this::convertToNoteWithDTO).collect(Collectors.toList());
    }

    @Transactional
    public Note updateNote(Long id, Note updatedNote, User user) {
        Note note = getNoteById(id);
        if (!note.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("You don't have permission to edit this note");
        }
        note.setTitle(updatedNote.getTitle());
        note.setDescription(updatedNote.getDescription());
        note.setUpdatedAt(LocalDateTime.now());
        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long id, User user) {
        Note note = getNoteById(id);
        if (!note.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("You don't have permission to delete this note");
        }
        if (note.getDocumentUrl() != null) {
            s3Service.deleteFile(note.getDocumentUrl());
        }
        noteRepository.delete(note);
    }

    @Transactional
    public String uploadDocument(Long noteId, byte[] file, String fileName, User user) {
        Note note = getNoteById(noteId);
        if (!note.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("You don't have permission to upload documents to this note");
        }
        String fileUrl = s3Service.uploadFile(file, fileName);
        note.setDocumentUrl(fileUrl);
        noteRepository.save(note);
        return fileUrl;
    }

    private Note convertToNoteWithDTO(Note note) {
        note.setCreatorDTO(userService.convertToUserDTO(note.getCreator()));
        note.setSharedUsersDTO(note.getSharedUsers().stream()
                .map(userService::convertToUserDTO)
                .collect(Collectors.toList()));
        note.getComments().forEach(comment ->
            comment.setUserDTO(userService.convertToUserDTO(comment.getUser())));
        return note;
    }
}