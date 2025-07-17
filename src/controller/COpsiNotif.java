package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class COpsiNotif {

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
    private javafx.scene.control.Label savedLabel;

    private String username = "favian";
    private String password = "vian";

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

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

        // Hide saved label by default
        if (savedLabel != null) {
            savedLabel.setVisible(false);
            savedLabel.setManaged(false);
        }

        // Load data notif user jika ada
        model.UserDataStore.User user = model.UserDataStore.loginUser(username, password);
        if (user != null) {
            notifCheckBox.setSelected(user.notifEnabled);
            if (user.notifEnabled) {
                timeBox.setVisible(true);
                timeBox.setManaged(true);
                hourCombo.setValue(user.notifHour);
                minuteCombo.setValue(user.notifMinute);
            }
        }
    }

    @FXML
    private void handleSave() {
        boolean enabled = notifCheckBox.isSelected();
        String jam = hourCombo.getValue() != null ? hourCombo.getValue() : "";
        String menit = minuteCombo.getValue() != null ? minuteCombo.getValue() : "";
        model.UserDataStore.updateUserNotif(username, enabled, jam, menit);
        // Tampilkan pesan sukses
        if (savedLabel != null) {
            savedLabel.setVisible(true);
            savedLabel.setManaged(true);
            // Sembunyikan setelah 2 detik
            new Thread(() -> {
                try { Thread.sleep(2000); } catch (InterruptedException e) {}
                javafx.application.Platform.runLater(() -> {
                    savedLabel.setVisible(false);
                    savedLabel.setManaged(false);
                });
            }).start();
        }
    }
}
