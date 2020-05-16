package de.brightside.bnotes.security;

import static org.springframework.security.core.userdetails.User.withUsername;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import de.brightside.bnotes.dao.UserRepo;
import de.brightside.bnotes.model.User;

@Component
public class UserDetailsImplService implements UserDetailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsImplService.class);
	
	private UserRepo userRepository;
	private JwtProvider jwtProvider;

	@Autowired
	public UserDetailsImplService(UserRepo userRepository, JwtProvider jwtProvider) {
		super();
		this.userRepository = userRepository;
		this.jwtProvider = jwtProvider;
	}

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		LOGGER.debug("loadUserByUsername: " + s);
		User user = userRepository.findByUsername(s)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with name %s does not exist", s)));

		//: org.springframework.security.core.userdetails.User.withUsername() builder
		return withUsername(user.getUsername()).password(user.getPassword()).authorities(user.getRoles())
				.accountExpired(false).accountLocked(false).credentialsExpired(false).disabled(false).build();
	}

	/**
	 * Extract username and roles from a validated jwt string.
	 *
	 * @param jwtToken jwt string
	 * @return UserDetails if valid, Empty otherwise
	 */
	public Optional<UserDetails> loadUserByJwtToken(String jwtToken) {
		if (jwtProvider.isValidToken(jwtToken)) {
			return Optional.of(withUsername(jwtProvider.getUsername(jwtToken))
					.authorities(jwtProvider.getRoles(jwtToken)).password("") // token does not have password but field
																				// may not be empty
					.accountExpired(false).accountLocked(false).credentialsExpired(false).disabled(false).build());
		}
		return Optional.empty();
	}

	/**
	 * Extract the username from the JWT then lookup the user in the database.
	 *
	 * @param jwtToken
	 * @return
	 */
	public Optional<UserDetails> loadUserByJwtTokenAndDatabase(String jwtToken) {
		if (jwtProvider.isValidToken(jwtToken)) {
			return Optional.of(loadUserByUsername(jwtProvider.getUsername(jwtToken)));
		} else {
			return Optional.empty();
		}
	}
}