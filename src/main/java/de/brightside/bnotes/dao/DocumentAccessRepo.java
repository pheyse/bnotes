package de.brightside.bnotes.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.brightside.bnotes.model.DocumentAccess;

public interface DocumentAccessRepo extends JpaRepository<DocumentAccess, Long>{

	@Query("SELECT COUNT(u) FROM DocumentAccess u WHERE u.userName=?1 and u.documentId=?2")
	public Long countAccess(String userName, long documentId);
	

	@Query("SELECT u.documentId FROM DocumentAccess u WHERE u.userName=?1")
    public Set<Long> getUserDocuments(String userName);

	@Query("SELECT u.userName FROM DocumentAccess u WHERE u.documentId=?1")
	public Set<String> getDocumentUsers(long documentId);
	

}