package controller;

public class CIsiAlamat {
    
    @javafx.fxml.FXML private javafx.scene.control.TextField namaField;
    @javafx.fxml.FXML private javafx.scene.control.TextField emailField;
    @javafx.fxml.FXML private javafx.scene.control.TextField teleponField;
    @javafx.fxml.FXML private javafx.scene.control.TextField provinsiField;
    @javafx.fxml.FXML private javafx.scene.control.TextField kotaField;
    @javafx.fxml.FXML private javafx.scene.control.TextField kecamatanField;
    @javafx.fxml.FXML private javafx.scene.control.TextField kodeposField;
    @javafx.fxml.FXML private javafx.scene.control.TextField alamatField;
    @javafx.fxml.FXML private javafx.scene.layout.Pane overlayPane;
    @javafx.fxml.FXML private javafx.scene.control.Button tutupButton;

    @javafx.fxml.FXML
    private void handleKonfirmasi() {
        // Validasi semua field wajib diisi
        if (isEmpty(namaField) || isEmpty(emailField) || isEmpty(teleponField) || isEmpty(provinsiField) || isEmpty(kotaField) || isEmpty(kecamatanField) || isEmpty(kodeposField) || isEmpty(alamatField)) {
            // Bisa tambahkan alert atau shake, tapi minimal tidak lanjut
            if (namaField != null) namaField.requestFocus();
            return;
        }
        // Tampilkan overlay
        if (overlayPane != null) overlayPane.setVisible(true);
    }

    private boolean isEmpty(javafx.scene.control.TextField field) {
        return field == null || field.getText() == null || field.getText().trim().isEmpty();
    }

    @javafx.fxml.FXML
    private String username;
    private String password;

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @javafx.fxml.FXML
    private void handleTutupOverlay() {
        if (overlayPane != null) overlayPane.setVisible(false);
        // Kembali ke halaman VPoin, passing data user
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/VPoin.fxml"));
            javafx.scene.Parent poinPage = loader.load();
            // Passing data user ke controller CPoin
            controller.CPoin ctrl = loader.getController();
            if (ctrl != null && username != null && password != null) {
                ctrl.setUser(username, password);
            }
            javafx.scene.Scene scene = tutupButton.getScene();
            scene.setRoot(poinPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
