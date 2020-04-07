package de.brightside.bnotes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.brightside.bnotes.model.User;

public interface UserRepo extends JpaRepository<User, String>{

	@Query("SELECT COUNT(u) FROM User u WHERE u.userName=?1 and u.password=?2")
    public Long countUserNamePassword(String userName, String password);

	public User findByUserName(String userName);
}