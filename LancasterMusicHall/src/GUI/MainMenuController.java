package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainMenuController {

    @FXML private TextField staffIdField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String staffId = staffIdField.getText();
        String password = passwordField.getText();

        if (staffId.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter Staff ID and Password.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful.");
            // TODO: Proceed to next screen
        }
    }

    @FXML
    private void handleForgotPassword() {
        showAlert(Alert.AlertType.INFORMATION, "Forgotten Password", "Please contact admin for password assistance.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
