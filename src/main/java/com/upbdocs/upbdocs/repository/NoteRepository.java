package com.upbdocs.upbdocs.repository;

import com.upbdocs.upbdocs.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByCreatorId(Long creatorId);
}
