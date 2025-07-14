package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class COpsiNotif {
    @FXML
    private CheckBox notifCheckBox;
    @FXML
    private VBox timeBox;
    @FXML
    private ComboBox<String> hourCombo;
    @FXML
    private ComboBox<String> minuteCombo;
    @FXML
    private Button saveBtn;

    @FXML
    public void initialize() {
        // Isi ComboBox jam dan menit
        for (int i = 0; i < 24; i++) hourCombo.getItems().add(String.format("%02d", i));
        for (int i = 0; i < 60; i += 5) minuteCombo.getItems().add(String.format("%02d", i));
        // Event checkbox
        notifCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            timeBox.setVisible(newVal);
            timeBox.setManaged(newVal);
        });
        // Default: sembunyikan pengaturan waktu
        timeBox.setVisible(false);
        timeBox.setManaged(false);
    }

    @FXML
    private void handleSave() {
        if (notifCheckBox.isSelected()) {
            String jam = hourCombo.getValue();
            String menit = minuteCombo.getValue();
            // Simpan waktu notif (implementasi sesuai kebutuhan)
        }
    }
}
