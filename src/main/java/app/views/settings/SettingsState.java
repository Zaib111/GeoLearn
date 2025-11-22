package app.views.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsState {
    private String username = "";
    private String successMessage = "";
    private String errorMessage = "";
    private boolean success;
}
