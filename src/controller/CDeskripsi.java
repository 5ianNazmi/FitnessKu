package controller;
import java.util.List;
import model.WorkoutDataStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

public class CDeskripsi {
    // Untuk passing user agar workout tetap tampil saat kembali
    private String username = null;
    private String password = null;

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
    // Opsi workout yang dipilih user, di-set dari halaman sebelumnya
    private String workoutOption = "Chest";
    private boolean isInitialized = false;
    private List<WorkoutDataStore.Exercise> exercises;
    private int currentIndex = 0;
    @FXML private WebView webView;
    @FXML private Label repeatLabel;
    @FXML private Label titleLabel;
    @FXML private Label instruksiLabel;
    @FXML private Button closeButton;

    // Setter untuk opsi workout, dipanggil dari halaman sebelumnya
    public void setWorkoutOption(String option) {
        this.workoutOption = option;
        // Jika sudah di-initialize, reload exercise sesuai opsi baru
        if (isInitialized) {
            exercises = WorkoutDataStore.getExercisesByOption(workoutOption);
            currentIndex = 0;
            showExercise(currentIndex);
        }
    }

    @FXML
    public void initialize() {
        isInitialized = true;
        // Hanya load jika workoutOption sudah di-set
        if (workoutOption != null && !workoutOption.isEmpty()) {
            exercises = WorkoutDataStore.getExercisesByOption(workoutOption);
            currentIndex = 0;
            showExercise(currentIndex);
        }
    }

    private void showExercise(int idx) {
        if (exercises == null || exercises.isEmpty()) {
            System.out.println("[DEBUG] Tidak ada data exercise untuk opsi: " + workoutOption);
            if (instruksiLabel != null) instruksiLabel.setText("Tidak ditemukan gerakan untuk opsi: " + workoutOption + ". Pastikan nama opsi sesuai dengan Workout.xml.");
            if (titleLabel != null) titleLabel.setText("");
            if (repeatLabel != null) repeatLabel.setText("");
            if (webView != null) webView.getEngine().loadContent("");
            return;
        }
        WorkoutDataStore.Exercise ex = exercises.get(idx);
        // Set judul
        if (titleLabel != null) titleLabel.setText(ex.name);
        // Ubah link YouTube ke format embed jika perlu
        String videoUrl = ex.video;
        if (videoUrl.contains("youtu.be/")) {
            // Ambil ID video
            String id = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
            if (id.contains("?")) id = id.substring(0, id.indexOf("?"));
            videoUrl = "https://www.youtube.com/embed/" + id;
        } else if (videoUrl.contains("youtube.com/watch?v=")) {
            String id = videoUrl.substring(videoUrl.indexOf("v=") + 2);
            if (id.contains("&")) id = id.substring(0, id.indexOf("&"));
            videoUrl = "https://www.youtube.com/embed/" + id;
        }
        String content = "<iframe width='320' height='180' src='" + videoUrl + "' frameborder='0' allowfullscreen></iframe>";
        webView.getEngine().loadContent(content, "text/html");
        // Set deskripsi
        if (instruksiLabel != null) instruksiLabel.setText(ex.description);
        // Set repeat
        repeatLabel.setText(String.valueOf(ex.repeat));
    }

    @FXML
    private void handleNextExercise() {
        if (exercises == null || exercises.isEmpty()) return;
        currentIndex = (currentIndex + 1) % exercises.size();
        showExercise(currentIndex);
    }

    @FXML
    private void handleBackButton(javafx.scene.input.MouseEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VWorkout.fxml"));
            javafx.scene.Parent workoutPage = loader.load();
            controller.CWorkout ctrl = loader.getController();
            if (username != null && password != null) {
                ctrl.setUser(username, password);
            }
            javafx.scene.Scene scene = ((javafx.scene.Node)event.getSource()).getScene();
            scene.setRoot(workoutPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
