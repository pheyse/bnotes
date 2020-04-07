package de.brightside.bnotes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.brightside.bnotes.model.Document;

public interface DocumentRepo extends JpaRepository<Document,Long>{
    @Query("FROM Document WHERE active = 1 ORDER BY title, documentId")
	public List<Document> getActiveDocumentsByName();
    
    public Document findByTitle(String name);
    
}