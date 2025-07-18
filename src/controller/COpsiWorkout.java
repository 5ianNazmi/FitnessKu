package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import model.UserDataStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

public class COpsiWorkout {
    // Callback untuk notifikasi opsi workout berubah
    private Runnable onOpsiChanged;

    public void setOnOpsiChanged(Runnable callback) {
        this.onOpsiChanged = callback;
    }
    @FXML private Button btnFullBody;
    @FXML private Button btnChest;
    @FXML private Button btnAbs;
    @FXML private Button btnArms;
    @FXML private Button btnLegs;
    @FXML private Button btnNext;

    // List untuk menyimpan opsi yang dipilih (boleh double)
    private List<String> selectedWorkouts = new ArrayList<>();
    private String currentUsername;

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    @FXML
    private void handleWorkoutOption(ActionEvent event) {
        Button source = (Button) event.getSource();
        String workout = source.getText();
        if (source.getStyleClass().contains("selected")) {
            // Jika sudah dipilih, klik lagi untuk unselect
            source.getStyleClass().remove("selected");
            selectedWorkouts.remove(workout); // hanya hapus satu kemunculan
        } else {
            // Jika belum dipilih, tambahkan ke list dan beri warna
            source.getStyleClass().add("selected");
            selectedWorkouts.add(workout);
        }
    }

    @FXML
    private void handleNext(ActionEvent event) {
        // Simpan workout ke User.xml (meskipun kosong, tetap update)
        if (currentUsername != null) {
            UserDataStore.updateUserWorkouts(currentUsername, selectedWorkouts);
            // Panggil callback jika ada
            if (onOpsiChanged != null) {
                onOpsiChanged.run();
            }
        }
        // Pindah ke halaman utama dan passing user ke CUtama
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HUtama.fxml"));
            Parent root = loader.load();
            controller.CUtama ctrl = loader.getController();
            // Passing username dan password kosong (atau bisa ambil dari login jika ingin)
            ctrl.setUser(currentUsername, "");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}