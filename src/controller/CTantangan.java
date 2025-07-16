package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CTantangan {
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
