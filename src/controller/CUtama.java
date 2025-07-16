package controller;

public class CUtama {
    @javafx.fxml.FXML
    private void handleWorkoutCard(javafx.scene.input.MouseEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VWorkout.fxml"));
            javafx.scene.Parent workoutPage = loader.load();
            controller.CWorkout ctrl = loader.getController();
            ctrl.setUser(currentUsername, currentPassword);
            javafx.scene.Scene scene = ((javafx.scene.Node)event.getSource()).getScene();
            scene.setRoot(workoutPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @javafx.fxml.FXML
    private void handleTantanganCard(javafx.scene.input.MouseEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VTantangan.fxml"));
            javafx.scene.Parent tantanganPage = loader.load();
            // Jika ingin passing user, bisa tambahkan setter di CTantangan
            javafx.scene.Scene scene = ((javafx.scene.Node)event.getSource()).getScene();
            scene.setRoot(tantanganPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @javafx.fxml.FXML
    private javafx.scene.layout.AnchorPane cardPoin;
    @javafx.fxml.FXML
    private javafx.scene.layout.AnchorPane cardLeaderboard;
    @javafx.fxml.FXML
    private javafx.scene.layout.AnchorPane cardNotif;
    @javafx.fxml.FXML
    private javafx.scene.layout.AnchorPane cardWorkout;
    @javafx.fxml.FXML
    private javafx.scene.layout.AnchorPane cardTantangan;
    @javafx.fxml.FXML
    private javafx.scene.layout.AnchorPane cardProgress;

    private String currentUsername = "favian";
    private String currentPassword = "vian";

    public void setUser(String username, String password) {
        this.currentUsername = username;
        this.currentPassword = password;
    }

    @javafx.fxml.FXML
    private void handleNotifCard(javafx.scene.input.MouseEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VOpsiNotif.fxml"));
            javafx.scene.Parent notifPage = loader.load();
            controller.COpsiNotif ctrl = loader.getController();
            ctrl.setUser(currentUsername, currentPassword);
            javafx.scene.Scene scene = ((javafx.scene.Node)event.getSource()).getScene();
            scene.setRoot(notifPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
