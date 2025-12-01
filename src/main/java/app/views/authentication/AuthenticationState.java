package app.views.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationState {
    private String username = "";
    private String errorMessage = "";
    private boolean success = false;
    private boolean isSignUpMode = false; // true for sign-up, false for sign-in
}
