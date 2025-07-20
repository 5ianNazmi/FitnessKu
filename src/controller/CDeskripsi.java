package controller;
import java.util.List;
import model.WorkoutDataStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

public class CDeskripsi {
    @FXML private javafx.scene.layout.Pane overlayPane;
    @FXML private Label poinLabel;
    @FXML private javafx.scene.layout.VBox rootVBox;
    // Konfigurasi jumlah poin yang didapat per selesai exercise
    private static final int POINTS_PER_EXERCISE = 100;
    // Timer untuk durasi workout
    private long workoutStartTime = 0;

    @FXML
    public void initialize() {
        isInitialized = true;
        workoutStartTime = System.currentTimeMillis();
        if (workoutOption != null && !workoutOption.isEmpty()) {
            exercises = WorkoutDataStore.getExercisesByOption(workoutOption);
            currentIndex = 0;
            showExercise(currentIndex);
        }
    }

    @FXML
    private void handleEndExercise() {
        // Hitung durasi workout
        long workoutEndTime = System.currentTimeMillis();
        int durationMinutes = (int) Math.max(1, (workoutEndTime - workoutStartTime) / 60000);
        int totalCalories = model.WorkoutDataStore.getOptionTotalCalories(workoutOption);

        // Tampilkan overlay blur dan info
        if (overlayPane != null && poinLabel != null && rootVBox != null) {
            overlayPane.setVisible(true);
            poinLabel.setText(
                "Selamat! Anda mendapat " + POINTS_PER_EXERCISE + " poin!\n" +
                "Kalori terbakar: " + totalCalories + " kkal\n" +
                "Durasi: " + durationMinutes + " menit"
            );
            rootVBox.setEffect(new javafx.scene.effect.BoxBlur(8, 8, 3));
        }

        // Tambahkan poin ke user dan simpan ke UserDataStore
        if (username != null && password != null) {
            model.UserDataStore.User user = model.UserDataStore.loginUser(username, password);
            if (user != null) {
                user.poin += POINTS_PER_EXERCISE;
                model.UserDataStore.updateUser(user);
            }
        }

        // Simpan riwayat workout ke Progress.xml
        if (username != null && !username.isEmpty()) {
            model.ProgressDataStore.addWorkoutHistory(username, workoutOption, totalCalories, durationMinutes);
            System.out.println("Saving workout: " + username + " - " + workoutOption + " - " + totalCalories + " cal - " + durationMinutes + " min");
        } else {
            System.out.println("Username is null or empty, cannot save workout history");
        }

        // Setelah beberapa detik, hilangkan overlay dan kembali ke halaman workout
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> {
            if (overlayPane != null && rootVBox != null) {
                overlayPane.setVisible(false);
                rootVBox.setEffect(null);
            }
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VWorkout.fxml"));
                javafx.scene.Parent workoutPage = loader.load();
                controller.CWorkout ctrl = loader.getController();
                if (username != null && password != null) {
                    ctrl.setUser(username, password);
                }
                javafx.scene.Scene scene = webView.getScene();
                scene.setRoot(workoutPage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }
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

    private void showExercise(int idx) {
        if (exercises == null || exercises.isEmpty()) {
            System.out.println("[DEBUG] Tidak ada data exercise untuk opsi: " + workoutOption);
            if (instruksiLabel != null) instruksiLabel.setText("Tidak ditemukan gerakan untuk opsi: " + workoutOption + ". Pastikan nama opsi sesuai dengan Workout.xml.");
            if (titleLabel != null) titleLabel.setText("");
            if (repeatLabel != null) repeatLabel.setText("");
            if (webView != null) webView.getEngine().loadContent("");
            if (closeButton != null) closeButton.setDisable(true);
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
        // Tampilkan tombol selesai hanya jika sudah di exercise terakhir
        if (closeButton != null) {
            boolean lastExercise = (currentIndex == exercises.size() - 1);
            closeButton.setVisible(lastExercise);
            closeButton.setDisable(!lastExercise);
        }
    }

    @FXML
    private void handleNextExercise() {
        if (exercises == null || exercises.isEmpty()) return;
        if (currentIndex < exercises.size() - 1) {
            currentIndex++;
            showExercise(currentIndex);
        }
        // Jika sudah di exercise terakhir, tidak melakukan apa-apa
    }

    @FXML
    private void handlePreviousExercise() {
        if (exercises == null || exercises.isEmpty()) return;
        if (currentIndex > 0) {
            currentIndex--;
            showExercise(currentIndex);
        }
        // Jika sudah di exercise pertama, tidak melakukan apa-apa
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
