package de.brightside.bnotes.security;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.brightside.bnotes.dao.RoleRepo;
import de.brightside.bnotes.dao.UserRepo;
import de.brightside.bnotes.model.Role;
import de.brightside.bnotes.model.User;

@Service
public class UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	
	private UserRepo userRepo;
    private AuthenticationManager authenticationManager;
    private RoleRepo roleRepo;
    private PasswordEncoder passwordEncoder;
	private JwtProvider jwtProvider;

    @Autowired
    public UserService(UserRepo userRepository, AuthenticationManager authenticationManager, RoleRepo roleRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepo = userRepository;
        this.authenticationManager = authenticationManager;
        this.roleRepo = roleRepository;
        this.passwordEncoder = passwordEncoder;
		this.jwtProvider = jwtProvider;
    }
    
    /**
     * Sign in a user into the application, with JWT-enabled authentication
     *
     * @param username  username
     * @param password  password
     * @return Optional of the Java Web Token, empty otherwise
     */
    public Optional<String> signin(String username, String password) {
        LOGGER.debug("New user attempting to sign in");
        Optional<String> token = Optional.empty();
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                token = Optional.of(jwtProvider.createToken(username, user.get().getRoles()));
            } catch (AuthenticationException e){
                LOGGER.info("Log in failed for user {}", username);
            }
        }
        return token;
    }

    public Optional<User> signupUserThatCanEditDocuments(String username, String password) {
        LOGGER.info("New user attempting to sign in");
        Optional<User> user = Optional.empty();
        if (!userRepo.findByUsername(username).isPresent()) {
            Optional<Role> role = roleRepo.findByRoleName("ROLE_CSR");
            user = Optional.of(userRepo.save(new User(username,
                            passwordEncoder.encode(password),
                            role.get())));
        }
        return user;
    }

    public List<User> getAll() {
        return userRepo.findAll();
    }
}
