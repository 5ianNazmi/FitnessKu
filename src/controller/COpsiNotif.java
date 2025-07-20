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

    private String username = null;
    private String password = null;

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
        refreshUserData();
    }

    private void refreshUserData() {
        // Load data notif user jika username/password sudah di-set
        if (username != null && password != null) {
            model.UserDataStore.User user = model.UserDataStore.loginUser(username, password);
            if (user != null) {
                notifCheckBox.setSelected(user.notifEnabled);
                if (user.notifEnabled) {
                    timeBox.setVisible(true);
                    timeBox.setManaged(true);
                    hourCombo.setValue(user.notifHour);
                    minuteCombo.setValue(user.notifMinute);
                } else {
                    timeBox.setVisible(false);
                    timeBox.setManaged(false);
                }
            }
        }
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
            if (newVal) {
                // Jika jam/menit belum dipilih, isi default
                if (hourCombo.getValue() == null) hourCombo.setValue("08");
                if (minuteCombo.getValue() == null) minuteCombo.setValue("00");
            }
            // Simpan perubahan notif langsung saat di-uncheck
            if (!newVal && username != null && password != null) {
                model.UserDataStore.updateUserNotif(username, false, "", "");
            }
        });
        // Default: sembunyikan pengaturan waktu
        timeBox.setVisible(false);
        timeBox.setManaged(false);

        // Hide saved label by default
        if (savedLabel != null) {
            savedLabel.setVisible(false);
            savedLabel.setManaged(false);
        }

        // Data user akan di-refresh setelah setUser dipanggil dari controller utama
    }

    @FXML
    private void handleSave() {
        boolean enabled = notifCheckBox.isSelected();
        String jam = hourCombo.getValue();
        String menit = minuteCombo.getValue();
        // Jika notif aktif dan jam/menit belum dipilih, isi default
        if (enabled) {
            if (jam == null || jam.isEmpty()) jam = "08";
            if (menit == null || menit.isEmpty()) menit = "00";
        } else {
            jam = "";
            menit = "";
        }
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
