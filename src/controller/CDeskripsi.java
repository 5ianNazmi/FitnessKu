package controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

public class CDeskripsi {
    @FXML
    private WebView webView;
    @FXML
    private Label repeatLabel;
    @FXML
    private Button closeButton;

    private int repeat = 10;
    private final int repeatMin = 1;
    private final int repeatMax = 50;

    @FXML
    public void initialize() {
        String embedUrl = "https://www.youtube.com/embed/RUNrHkbP4Pc";
        String content = "<iframe width='320' height='180' src='" + embedUrl + "' frameborder='0' allowfullscreen></iframe>";
        webView.getEngine().loadContent(content, "text/html");
        updateRepeatLabel();
    }

    @FXML
    private void handleRepeatMinus() {
        if (repeat > repeatMin) {
            repeat--;
            updateRepeatLabel();
        }
    }

    @FXML
    private void handleRepeatPlus() {
        if (repeat < repeatMax) {
            repeat++;
            updateRepeatLabel();
        }
    }

    private void updateRepeatLabel() {
        repeatLabel.setText(String.valueOf(repeat));
    }
}
