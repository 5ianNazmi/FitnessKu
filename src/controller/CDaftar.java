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

public class CDaftar {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button daftarButton;
    @FXML private Text statusText;

    @FXML
    private void initialize() {
        // Optional: inisialisasi
    }

    @FXML
    private void handleDaftar(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        if (!password.equals(confirm)) {
            statusText.setText("Password tidak cocok!");
            return;
        }
        boolean success = UserDataStore.registerUser(username, email, password);
        if (success) {
            statusText.setText("Registrasi berhasil!");
            try {
                changeScene(event, "/view/HLogin.fxml"); // pindah ke halaman login
            } catch (Exception e) {
                statusText.setText("Gagal pindah ke login!");
            }
        } else {
            statusText.setText("Username/email sudah terdaftar!");
        }
    }

    @FXML
    private void handleGoToLogin(ActionEvent event) {
        try {
            changeScene(event, "/view/HLogin.fxml");
        } catch (Exception e) {
            statusText.setText("Gagal pindah ke halaman login!");
        }
    }

    private void changeScene(ActionEvent event, String fxmlPath) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
