package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CTantangan {
    private String username = null;
    private String password = null;

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @FXML private Label workoutLabel;
    @FXML private Button btnSpin;
    @FXML private Button btnAmbil;

    private final List<String> workoutOptions = Arrays.asList(
        "Full Body", "Chest", "Abs", "Arms", "Legs"
    );
    private String selectedWorkout = null;

    @FXML
    public void initialize() {
        btnSpin.setOnAction(this::handleSpinWorkout);
        btnAmbil.setOnAction(this::handleAmbilTantangan);
        btnAmbil.setDisable(true);
    }

    private void handleSpinWorkout(ActionEvent event) {
        selectedWorkout = workoutOptions.get(new Random().nextInt(workoutOptions.size()));
        workoutLabel.setText(selectedWorkout);
        btnAmbil.setDisable(false);
    }

    private void handleAmbilTantangan(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VDeskripsi.fxml"));
            javafx.scene.Parent deskripsiPage = loader.load();
            controller.CDeskripsi ctrl = loader.getController();
            if (username != null && password != null) {
                ctrl.setUser(username, password);
            }
            if (selectedWorkout != null) {
                ctrl.setWorkoutOption(selectedWorkout);
            }
            javafx.scene.Scene scene = btnAmbil.getScene();
            scene.setRoot(deskripsiPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @javafx.fxml.FXML
    private void handleBackButton(javafx.scene.input.MouseEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/HUtama.fxml"));
            javafx.scene.Parent utamaPage = loader.load();
            controller.CUtama ctrl = loader.getController();
            ctrl.setUser(username, password);
            javafx.scene.Scene scene = ((javafx.scene.Node)event.getSource()).getScene();
            scene.setRoot(utamaPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
