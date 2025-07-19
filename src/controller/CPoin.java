package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CPoin {
    @FXML
    private void handleTukarHadiahLevel20() {
        hadiahLevel = 20;
        showOverlayTukarHadiah();
    }
    @FXML private javafx.scene.layout.Pane overlayPane;
    @FXML private Label overlayLabel;
    @FXML private javafx.scene.control.Button yakinButton;
    @FXML private javafx.scene.control.Button batalButton;
    private int hadiahLevel = 0;
    // Handler tombol tukar hadiah pada card reward level
    @FXML
    private void handleTukarHadiahLevel5() {
        hadiahLevel = 5;
        showOverlayTukarHadiah();
    }
    @FXML
    private void handleTukarHadiahLevel10() {
        hadiahLevel = 10;
        showOverlayTukarHadiah();
    }
    @FXML
    private void handleTukarHadiahLevel15() {
        hadiahLevel = 15;
        showOverlayTukarHadiah();
    }
    private void showOverlayTukarHadiah() {
        if (overlayPane != null && overlayLabel != null && yakinButton != null && batalButton != null) {
            overlayPane.setVisible(true);
            batalButton.setVisible(true);
            model.UserDataStore.User user = model.UserDataStore.loginUser(username, password);
            int userLevel = 0;
            if (user != null) {
                int totalPoin = user.poin;
                int batas = 100;
                int poinSekarang = totalPoin;
                while (poinSekarang >= batas) {
                    poinSekarang -= batas;
                    userLevel++;
                    batas += 100;
                }
            }
            if (userLevel >= hadiahLevel) {
                overlayLabel.setText("Apakah kamu yakin ingin menukar hadiah?");
                yakinButton.setVisible(true);
            } else {
                overlayLabel.setText("Level tidak cukup!");
                yakinButton.setVisible(false);
                // Timer hilangkan overlay setelah 2 detik
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
                pause.setOnFinished(e -> overlayPane.setVisible(false));
                pause.play();
            }
        }
    }
    @FXML
    private void handleYakinTukarHadiah() {
        // Pindah ke halaman alamat jika level cukup
        if (overlayPane != null) overlayPane.setVisible(false);
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VIsiAlamat.fxml"));
            javafx.scene.Parent alamatPage = loader.load();
            // Passing data user ke controller CIsiAlamat
            controller.CIsiAlamat ctrl = loader.getController();
            if (ctrl != null && username != null && password != null) {
                ctrl.setUser(username, password);
            }
            javafx.scene.Scene scene = poinLabel.getScene();
            scene.setRoot(alamatPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleBatalTukarHadiah() {
        if (overlayPane != null) overlayPane.setVisible(false);
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
    private String username = null;
    private String password = null;

    @FXML private Label poinLabel;
    @FXML private javafx.scene.control.ProgressBar progressBar;
    @FXML private Label levelLabel;
    @FXML private Label progressTextLabel;
    @FXML private Label namaLabel;

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
        showUserPoin();
    }

    private void showUserPoin() {
        if (username == null) return;
        model.UserDataStore.User user = model.UserDataStore.loginUser(username, password);
        if (user != null && poinLabel != null) {
            int totalPoin = user.poin;
            int level = 0;
            int batas = 100;
            int poinSekarang = totalPoin;
            while (poinSekarang >= batas) {
                poinSekarang -= batas;
                level++;
                batas += 100;
            }
            poinLabel.setText(String.valueOf(totalPoin));
            levelLabel.setText(String.valueOf(level));
            progressBar.setProgress((double)poinSekarang / (double)batas);
            if (progressTextLabel != null) {
                progressTextLabel.setText(poinSekarang + "/" + batas);
            }
            if (namaLabel != null) {
                namaLabel.setText(user.username);
            }
        }
    }
}
