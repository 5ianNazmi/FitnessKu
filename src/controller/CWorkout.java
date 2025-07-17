package controller;


public class CWorkout {
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
        java.util.List<String> workouts = model.WorkoutDataStore.getUserWorkouts(username);
        if (workouts != null && !workouts.isEmpty()) {
            for (String workout : workouts) {
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
        // Simpan workout yang dipilih ke User.xml jika belum ada
        model.UserDataStore.User user = model.UserDataStore.loginUser(username, password);
        if (user != null) {
            if (!user.workouts.contains(workout)) {
                user.workouts.add(workout);
                model.UserDataStore.updateUserWorkouts(username, user.workouts);
            }
        }
        System.out.println("Workout dipilih: " + workout);
    }
}
