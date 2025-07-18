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
    @FXML private Label workoutLabel;
    @FXML private Button btnRandom;

    private final List<String> workoutOptions = Arrays.asList(
        "Full Body", "Chest", "Abs", "Arms", "Legs"
    );

    @FXML
    public void initialize() {
        btnRandom.setOnAction(this::handleRandomWorkout);
    }

    private void handleRandomWorkout(ActionEvent event) {
        String randomWorkout = workoutOptions.get(new Random().nextInt(workoutOptions.size()));
        workoutLabel.setText(randomWorkout);
    }
}
