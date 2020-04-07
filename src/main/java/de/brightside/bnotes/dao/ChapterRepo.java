package de.brightside.bnotes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import de.brightside.bnotes.model.Chapter;

public interface ChapterRepo extends JpaRepository<Chapter,Long>{
    @Query("FROM Chapter WHERE documentId = ?1 AND active = 1 ORDER BY orderSequence, chapterId")
	public List<Chapter> getDocumentChapters(long documentId);
    
    @Query(value = "SELECT max(orderSequence) FROM Chapter WHERE documentId = ?1")
	public Long maxOrderSequence(long documentId);

    
    /**
     * increment all order sequence values which have at least the value minimumOrderSequenceValue
     * @param documentId
     * @param minimumOrderSequenceValue
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE Chapter set orderSequence = orderSequence + 1 WHERE documentId = ?1 and orderSequence >= ?2")
	public int incrementOrderSequence(long documentId, long minimumOrderSequenceValue);

    @Query(value = "FROM Chapter WHERE active = 1 and documentId = ?1 ORDER BY orderSequence, chapterId")
	public List<Chapter> findAllActiveOfDocument(long documentId);
}