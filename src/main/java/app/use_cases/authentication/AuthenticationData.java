package app.use_cases.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationData {
    private String username;
    private String password;
    private String confirmPassword;

    public AuthenticationData(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
