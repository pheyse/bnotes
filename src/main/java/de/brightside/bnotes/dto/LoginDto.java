package de.brightside.bnotes.dto;

import javax.validation.constraints.NotNull;

public class LoginDto {
    @NotNull
    private String username;
    @NotNull
    private String password;

    protected LoginDto() {
    }

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoginDto [username=" + username + ", password=" + password + "]";
	}

    
}
