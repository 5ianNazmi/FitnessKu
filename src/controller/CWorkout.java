package controller;


public class CWorkout {
    @javafx.fxml.FXML
    private javafx.scene.control.Button btnGantiOpsi;

    @javafx.fxml.FXML
    private void handleGantiOpsi(javafx.event.ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VOpsiWorkout.fxml"));
            javafx.scene.Parent opsiPage = loader.load();
            controller.COpsiWorkout ctrl = loader.getController();
            ctrl.setCurrentUsername(username);
            // Tambahkan callback agar setelah opsi diganti, workoutListBox di-refresh
            if (ctrl instanceof controller.COpsiWorkout) {
                ((controller.COpsiWorkout)ctrl).setOnOpsiChanged(() -> {
                    // Setelah opsi workout diganti, refresh workoutListBox
                    loadWorkouts();
                });
            }
            javafx.scene.Scene scene = ((javafx.scene.Node)event.getSource()).getScene();
            scene.setRoot(opsiPage);
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
    @javafx.fxml.FXML
    private javafx.scene.layout.VBox workoutListBox;

    private String username = null;
    private String password = null;

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
        // Jika sudah di-inject user, refresh tampilan workout
        loadWorkouts();
    }

    @javafx.fxml.FXML
    public void initialize() {
        // Jangan load workout di sini, tunggu sampai setUser dipanggil
    }

    private void loadWorkouts() {
        workoutListBox.getChildren().clear();
        if (username == null) {
            javafx.scene.control.Label label = new javafx.scene.control.Label("User belum dipassing");
            label.setStyle("-fx-text-fill: #fff; -fx-font-size: 16px;");
            workoutListBox.getChildren().add(label);
            return;
        }
        model.UserDataStore.User user = model.UserDataStore.loginUser(username, password);
        if (user != null && user.workouts != null && !user.workouts.isEmpty()) {
            for (String workout : user.workouts) {
                javafx.scene.control.Button btn = new javafx.scene.control.Button(workout);
                btn.getStyleClass().add("workout-btn");
                btn.setOnAction(e -> handleWorkoutSelected(workout));
                workoutListBox.getChildren().add(btn);
            }
        } else {
            javafx.scene.control.Label label = new javafx.scene.control.Label("Belum ada workout yang dipilih");
            label.setStyle("-fx-text-fill: #fff; -fx-font-size: 16px;");
            workoutListBox.getChildren().add(label);
        }
    }

    private void handleWorkoutSelected(String workout) {
        // Navigasi ke halaman deskripsi dan passing opsi workout serta user
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VDeskripsi.fxml"));
            javafx.scene.Parent deskripsiPage = loader.load();
            controller.CDeskripsi ctrl = loader.getController();
            ctrl.setWorkoutOption(workout); // passing opsi workout
            if (username != null && password != null) {
                ctrl.setUser(username, password);
            }
            workoutListBox.getScene().setRoot(deskripsiPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
