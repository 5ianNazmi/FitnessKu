package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import model.UserDataStore;
import model.WorkoutDataStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

public class COpsiWorkout {
    @FXML private Button btnFullBody;
    @FXML private Button btnChest;
    @FXML private Button btnAbs;
    @FXML private Button btnArms;
    @FXML private Button btnLegs;
    @FXML private Button btnNext;

    // List untuk menyimpan opsi yang dipilih (boleh double)
    private List<String> selectedWorkouts = new ArrayList<>();
    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    @FXML
    private void handleWorkoutOption(ActionEvent event) {
        Button source = (Button) event.getSource();
        String workout = source.getText();
        if (source.getStyleClass().contains("selected")) {
            // Jika sudah dipilih, klik lagi untuk unselect
            source.getStyleClass().remove("selected");
            selectedWorkouts.remove(workout); // hanya hapus satu kemunculan
        } else {
            // Jika belum dipilih, tambahkan ke list dan beri warna
            source.getStyleClass().add("selected");
            selectedWorkouts.add(workout);
        }
    }

    @FXML
    private void handleNext(ActionEvent event) {
        // Simpan workout ke Workout.xml
        if (currentUsername != null && !selectedWorkouts.isEmpty()) {
            WorkoutDataStore.setUserWorkouts(currentUsername, selectedWorkouts);
        }
        // Pindah ke halaman utama
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/HUtama.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}