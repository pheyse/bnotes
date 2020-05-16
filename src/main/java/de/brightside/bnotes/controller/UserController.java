package de.brightside.bnotes.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import de.brightside.bnotes.dto.LoginDto;
import de.brightside.bnotes.model.User;
import de.brightside.bnotes.security.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
    	super();
		this.userService = userService;
	}

    @PostMapping("/signin")
	public String login(@RequestBody LoginDto loginDto) {
        return userService.signin(loginDto.getUsername(), loginDto.getPassword()).orElseThrow(()->
        new HttpServerErrorException(HttpStatus.FORBIDDEN, "Login Failed"));
    }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public User signupUserThatCanEditDocuments(@RequestBody LoginDto loginDto){
    	HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.BAD_REQUEST,"User already exists");
        return userService.signupUserThatCanEditDocuments(loginDto.getUsername(), loginDto.getPassword()).orElseThrow(() -> { return exception;});
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAll();
    }
}