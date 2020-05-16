package de.brightside.bnotes.dao;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.brightside.bnotes.model.User;

public interface UserRepo extends JpaRepository<User, String>{

	@Query("SELECT u.username FROM User u")
	public Set<String> getAllUserNames();
	
	Optional<User> findByUsername(String userName);
	
}