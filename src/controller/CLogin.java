package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import model.UserDataStore;
import model.WorkoutDataStore;

public class CLogin {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Text statusText;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        UserDataStore.User user = UserDataStore.loginUser(username, password);
        if (user != null) {
            statusText.setText("Login berhasil!");
            try {
                // Cek workout di Workout.xml
                java.util.List<String> workouts = WorkoutDataStore.getUserWorkouts(username);
                if (workouts != null && !workouts.isEmpty()) {
                    changeScene(event, "/view/HUtama.fxml", username); // ke halaman utama
                } else {
                    changeScene(event, "/view/VOpsiWorkout.fxml", username); // ke opsi workout
                }
            } catch (Exception e) {
                statusText.setText("Gagal pindah halaman!");
            }
        } else {
            statusText.setText("Username/password salah!");
        }
    }

    @FXML
    private void handleGoToRegister(ActionEvent event) {
        try {
            changeScene(event, "/view/HDaftar.fxml");
        } catch (Exception e) {
            statusText.setText("Gagal pindah ke halaman daftar!");
        }
    }

    private void changeScene(ActionEvent event, String fxmlPath) throws Exception {
        changeScene(event, fxmlPath, null);
    }

    // Overload untuk passing username
    private void changeScene(ActionEvent event, String fxmlPath, String username) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        if (fxmlPath.contains("VOpsiWorkout") && username != null) {
            controller.COpsiWorkout ctrl = loader.getController();
            ctrl.setCurrentUsername(username);
        }
        // Jika ke HUtama, set username & password
        if (fxmlPath.contains("HUtama") && username != null) {
            controller.CUtama ctrl = loader.getController();
            ctrl.setUser(username, passwordField.getText());
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
