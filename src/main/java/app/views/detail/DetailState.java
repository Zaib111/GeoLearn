package app.views.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * View Model for the Detail View
 * Holds all required parameters needed to construct the view
 * Automatically updates the view upon a change in the state
 */
public class DetailState {
    private String countryCode;
    private String countryName;
    private Optional<String> capital;
    private String region;
    private Optional<String> subregion;
    private long population;
    private double areaKm2;
    private List<String> borders;
    private String flagUrl;
    private List<String> languages;
    private List<String> currencies;
    private List<String> timezones;
    private String errorMessage;
    private Boolean hasError;
}
