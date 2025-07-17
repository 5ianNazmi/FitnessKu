package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CPoin {
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

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
        showUserPoin();
    }

    private void showUserPoin() {
        if (username == null) return;
        model.UserDataStore.User user = model.UserDataStore.loginUser(username, password);
        if (user != null && poinLabel != null) {
            poinLabel.setText(String.valueOf(user.poin));
        }
    }
}
