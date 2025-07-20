package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.util.List;
import model.ProgressDataStore;

public class CProgress {
    private String username = null;
    private String password = null;

    @FXML private BarChart<String, Number> caloriesBarChart;
    @FXML private BarChart<String, Number> durationBarChart;
    @FXML private VBox activityTodayBox;

    public void setUser(String username, String password) {
        System.out.println("=== CProgress: setUser() called ===");
        System.out.println("Username: '" + username + "'");
        System.out.println("Password: '" + password + "'");
        this.username = username;
        this.password = password;
        
        // Gunakan Timeline dengan delay singkat untuk load progress data real
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(200), e -> {
                System.out.println("=== Timeline delay executed ===");
                System.out.println("Charts ready check: caloriesBarChart=" + (caloriesBarChart != null) + 
                                 ", durationBarChart=" + (durationBarChart != null));
                // Load data progress yang sesungguhnya
                loadProgress();
                
                // Force apply ultra slim styling with additional delay
                javafx.animation.Timeline styleTimeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.millis(500), styleEvent -> {
                        applyUltraSlimStyling();
                    })
                );
                styleTimeline.play();
            })
        );
        timeline.play();
    }

    // Method untuk refresh data progress
    public void refreshProgress() {
        System.out.println("CProgress: Refreshing progress data");
        loadProgress();
    }
    
    // Method untuk test chart dengan data dummy
    private void loadTestData() {
        System.out.println("=== LOADING TEST DATA ===");
        
        if (caloriesBarChart != null) {
            System.out.println("Clearing and setting up calories chart...");
            caloriesBarChart.getData().clear();
            
            // Buat series baru
            XYChart.Series<String, Number> testSeries = new XYChart.Series<>();
            testSeries.setName("Kalori Hari Ini");
            
            // Tambah data point total
            testSeries.getData().add(new XYChart.Data<>("Total Hari Ini", 482));
            
            // Tambahkan series ke chart
            caloriesBarChart.getData().add(testSeries);
            caloriesBarChart.setTitle("Kalori Terbakar Hari Ini");
            caloriesBarChart.setLegendVisible(false);
            
            System.out.println("Calories chart loaded with " + testSeries.getData().size() + " items");
        } else {
            System.out.println("ERROR: caloriesBarChart is NULL!");
        }
        
        if (durationBarChart != null) {
            System.out.println("Clearing and setting up duration chart...");
            durationBarChart.getData().clear();
            
            // Buat series baru
            XYChart.Series<String, Number> testSeries = new XYChart.Series<>();
            testSeries.setName("Durasi Hari Ini");
            
            // Tambah data point total
            testSeries.getData().add(new XYChart.Data<>("Total Hari Ini", 6));
            
            // Tambahkan series ke chart
            durationBarChart.getData().add(testSeries);
            durationBarChart.setTitle("Durasi Workout Hari Ini");
            durationBarChart.setLegendVisible(false);
            
            System.out.println("Duration chart loaded with " + testSeries.getData().size() + " items");
        } else {
            System.out.println("ERROR: durationBarChart is NULL!");
        }
        
        System.out.println("=== TEST DATA LOADING COMPLETE ===");
    }

    @FXML
    public void initialize() {
        System.out.println("=== CProgress: initialize() called ===");
        System.out.println("Charts initial state: caloriesBarChart=" + (caloriesBarChart != null) + 
                          ", durationBarChart=" + (durationBarChart != null) + 
                          ", activityTodayBox=" + (activityTodayBox != null));
        // Initialize akan dipanggil setelah setUser, tapi chart belum ready
        // Jadi kita akan load progress di setUser()
    }

    private void loadProgress() {
        if (username == null) {
            System.out.println("Username is null, cannot load progress");
            return;
        }
        String today = LocalDate.now().toString();
        System.out.println("Loading progress for user: '" + username + "' on date: '" + today + "'");
        List<ProgressDataStore.WorkoutHistory> todayHistory = ProgressDataStore.getDailyWorkoutHistory(username, today);
        
        System.out.println("Found " + todayHistory.size() + " workout history entries for today");

        // Hitung total kalori dan durasi dari semua workout hari ini
        int totalCalories = 0;
        int totalDuration = 0;
        
        for (ProgressDataStore.WorkoutHistory wh : todayHistory) {
            totalCalories += wh.calories;
            totalDuration += wh.duration;
            System.out.println("History entry: " + wh.option + " - " + wh.calories + " cal - " + wh.duration + " min");
        }
        
        System.out.println("Total calories today: " + totalCalories);
        System.out.println("Total duration today: " + totalDuration + " minutes");

        // Chart kalori - tampilkan hanya total
        System.out.println("Chart objects: caloriesBarChart=" + (caloriesBarChart != null) + 
                          ", durationBarChart=" + (durationBarChart != null));
        
        if (caloriesBarChart != null) {
            caloriesBarChart.getData().clear();
            XYChart.Series<String, Number> caloriesSeries = new XYChart.Series<>();
            caloriesSeries.setName("Kalori Hari Ini");
            
            if (totalCalories == 0) {
                // Jika tidak ada data workout, tampilkan pesan kosong
                caloriesSeries.getData().add(new XYChart.Data<>("Belum ada workout", 1));
                System.out.println("No workout data found, showing empty placeholder");
            } else {
                // Tampilkan hanya total kalori hari ini
                caloriesSeries.getData().add(new XYChart.Data<>("Total Hari Ini", totalCalories));
                System.out.println("Adding total calories to chart: " + totalCalories);
            }
            
            caloriesBarChart.getData().add(caloriesSeries);
            caloriesBarChart.setTitle("Kalori Terbakar Hari Ini");
            caloriesBarChart.setLegendVisible(false);
            caloriesBarChart.setAnimated(true);
            
            // Set axis properties untuk menghindari overlapping - sembunyikan label X-axis
            if (caloriesBarChart.getXAxis() instanceof javafx.scene.chart.CategoryAxis) {
                javafx.scene.chart.CategoryAxis xAxis = (javafx.scene.chart.CategoryAxis) caloriesBarChart.getXAxis();
                xAxis.setGapStartAndEnd(false);
                xAxis.setTickLabelsVisible(false); // Sembunyikan label untuk menghindari overlapping
                xAxis.setTickMarkVisible(false);
            }
            
            // Apply styling langsung ke chart
            caloriesBarChart.setStyle("-fx-background-color: #181A20; -fx-background-radius: 12;");
            
            // Set bar styling dengan delay untuk memastikan nodes sudah terbuat
            javafx.animation.Timeline barStyleTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(300), e -> {
                    caloriesBarChart.lookupAll(".chart-bar").forEach(node -> {
                        node.setStyle("-fx-bar-fill: #26b062;");
                    });
                })
            );
            barStyleTimeline.play();
            
            System.out.println("Calories chart updated with " + caloriesSeries.getData().size() + " data points");
        } else {
            System.out.println("ERROR: caloriesBarChart is null!");
        }

        // Chart durasi
        if (durationBarChart != null) {
            durationBarChart.getData().clear();
            XYChart.Series<String, Number> durationSeries = new XYChart.Series<>();
            durationSeries.setName("Durasi Hari Ini");
            
            if (totalDuration == 0) {
                // Jika tidak ada data workout, tampilkan pesan kosong
                durationSeries.getData().add(new XYChart.Data<>("Belum ada workout", 1));
                System.out.println("No workout data found, showing empty placeholder");
            } else {
                // Tampilkan hanya total durasi hari ini
                durationSeries.getData().add(new XYChart.Data<>("Total Hari Ini", totalDuration));
                System.out.println("Adding total duration to chart: " + totalDuration + " minutes");
            }
            
            durationBarChart.getData().add(durationSeries);
            durationBarChart.setTitle("Durasi Workout Hari Ini");
            durationBarChart.setLegendVisible(false);
            durationBarChart.setAnimated(true);
            
            // Set axis properties untuk menghindari overlapping - sembunyikan label X-axis
            if (durationBarChart.getXAxis() instanceof javafx.scene.chart.CategoryAxis) {
                javafx.scene.chart.CategoryAxis xAxis = (javafx.scene.chart.CategoryAxis) durationBarChart.getXAxis();
                xAxis.setGapStartAndEnd(false);
                xAxis.setTickLabelsVisible(false); // Sembunyikan label untuk menghindari overlapping
                xAxis.setTickMarkVisible(false);
            }
            
            // Apply styling langsung ke chart
            durationBarChart.setStyle("-fx-background-color: #181A20; -fx-background-radius: 12;");
            
            // Set bar styling dengan delay untuk memastikan nodes sudah terbuat
            javafx.animation.Timeline barStyleTimeline2 = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(300), e -> {
                    durationBarChart.lookupAll(".chart-bar").forEach(node -> {
                        node.setStyle("-fx-bar-fill: #26b062;");
                    });
                })
            );
            barStyleTimeline2.play();
            
            System.out.println("Duration chart updated with " + durationSeries.getData().size() + " data points");
        } else {
            System.out.println("ERROR: durationBarChart is null!");
        }

        // Aktivitas hari ini
        loadActivityList(todayHistory);
    }

    private void loadActivityList(List<ProgressDataStore.WorkoutHistory> todayHistory) {
        if (activityTodayBox != null) {
            activityTodayBox.getChildren().clear();
            
            // Tambahkan header dengan styling yang lebih baik
            javafx.scene.layout.HBox headerBox = new javafx.scene.layout.HBox();
            headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            headerBox.setStyle("-fx-padding: 0 0 10 0;");
            
            Label titleLabel = new Label("Aktivitas Hari Ini");
            titleLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");
            
            headerBox.getChildren().add(titleLabel);
            activityTodayBox.getChildren().add(headerBox);
            
            if (todayHistory.isEmpty()) {
                // Tampilan untuk belum ada aktivitas
                javafx.scene.layout.VBox emptyBox = new javafx.scene.layout.VBox();
                emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
                emptyBox.setStyle("-fx-padding: 20 0; -fx-spacing: 10;");
                
                Label emptyIcon = new Label("🏋️");
                emptyIcon.setStyle("-fx-font-size: 32px; -fx-opacity: 0.3;");
                
                Label emptyLabel = new Label("Belum ada aktivitas hari ini");
                emptyLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 14px;");
                
                Label encouragementLabel = new Label("Ayo mulai workout pertama Anda!");
                encouragementLabel.setStyle("-fx-text-fill: #26b062; -fx-font-size: 12px;");
                
                emptyBox.getChildren().addAll(emptyIcon, emptyLabel, encouragementLabel);
                activityTodayBox.getChildren().add(emptyBox);
                
            } else {
                // TOTAL SUMMARY DI ATAS - PINDAHKAN KE SINI
                javafx.scene.layout.HBox totalBox = new javafx.scene.layout.HBox();
                totalBox.setAlignment(javafx.geometry.Pos.CENTER);
                totalBox.setStyle("-fx-padding: 15; -fx-background-color: rgba(38, 176, 98, 0.1); " +
                               "-fx-background-radius: 12; -fx-spacing: 30; -fx-margin: 0 0 15 0;");
                
                int totalCalories = todayHistory.stream().mapToInt(wh -> wh.calories).sum();
                int totalDuration = todayHistory.stream().mapToInt(wh -> wh.duration).sum();
                
                javafx.scene.layout.VBox caloriesBox = new javafx.scene.layout.VBox();
                caloriesBox.setAlignment(javafx.geometry.Pos.CENTER);
                Label caloriesNumLabel = new Label(String.valueOf(totalCalories));
                caloriesNumLabel.setStyle("-fx-text-fill: #26b062; -fx-font-size: 24px; -fx-font-weight: bold;");
                Label caloriesTextLabel = new Label("Total Kalori");
                caloriesTextLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12px;");
                caloriesBox.getChildren().addAll(caloriesNumLabel, caloriesTextLabel);
                
                javafx.scene.layout.VBox durationBox = new javafx.scene.layout.VBox();
                durationBox.setAlignment(javafx.geometry.Pos.CENTER);
                Label durationNumLabel = new Label(totalDuration + " min");
                durationNumLabel.setStyle("-fx-text-fill: #26b062; -fx-font-size: 24px; -fx-font-weight: bold;");
                Label durationTextLabel = new Label("Total Durasi");
                durationTextLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12px;");
                durationBox.getChildren().addAll(durationNumLabel, durationTextLabel);
                
                javafx.scene.layout.VBox workoutBox = new javafx.scene.layout.VBox();
                workoutBox.setAlignment(javafx.geometry.Pos.CENTER);
                Label workoutNumLabel = new Label(String.valueOf(todayHistory.size()));
                workoutNumLabel.setStyle("-fx-text-fill: #26b062; -fx-font-size: 24px; -fx-font-weight: bold;");
                Label workoutTextLabel = new Label("Total Workout");
                workoutTextLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12px;");
                workoutBox.getChildren().addAll(workoutNumLabel, workoutTextLabel);
                
                totalBox.getChildren().addAll(caloriesBox, durationBox, workoutBox);
                activityTodayBox.getChildren().add(totalBox);
                
                // Spacer antara total dan list
                javafx.scene.layout.Pane spacingPane = new javafx.scene.layout.Pane();
                spacingPane.setPrefHeight(10);
                activityTodayBox.getChildren().add(spacingPane);
                
                // Tampilkan setiap workout TANPA ICON
                for (int i = 0; i < todayHistory.size(); i++) {
                    ProgressDataStore.WorkoutHistory wh = todayHistory.get(i);
                    
                    javafx.scene.layout.HBox itemBox = new javafx.scene.layout.HBox();
                    itemBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    itemBox.setStyle("-fx-padding: 12 16; -fx-background-color: #222; " +
                                   "-fx-background-radius: 8; -fx-spacing: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");
                    
                    // Info workout (TANPA ICON)
                    javafx.scene.layout.VBox infoBox = new javafx.scene.layout.VBox();
                    infoBox.setStyle("-fx-spacing: 2;");
                    
                    Label workoutLabel = new Label(wh.option);
                    workoutLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-font-weight: bold;");
                    
                    Label detailLabel = new Label(wh.duration + " menit • " + wh.calories + " kkal");
                    detailLabel.setStyle("-fx-text-fill: #26b062; -fx-font-size: 14px;");
                    
                    // Time info jika ada
                    String timeStr = getTimeFromHistory(wh);
                    if (timeStr != null && !timeStr.isEmpty()) {
                        Label timeLabel = new Label(timeStr);
                        timeLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
                        infoBox.getChildren().addAll(workoutLabel, detailLabel, timeLabel);
                    } else {
                        infoBox.getChildren().addAll(workoutLabel, detailLabel);
                    }
                    
                    // Spacer untuk push ke kanan
                    javafx.scene.layout.Pane spacer = new javafx.scene.layout.Pane();
                    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    
                    // Badge untuk nomor urut
                    Label badgeLabel = new Label("#" + (i + 1));
                    badgeLabel.setStyle("-fx-text-fill: #26b062; -fx-font-size: 12px; -fx-font-weight: bold; " +
                                      "-fx-background-color: rgba(38, 176, 98, 0.2); -fx-background-radius: 12; " +
                                      "-fx-padding: 4 8;");
                    
                    itemBox.getChildren().addAll(infoBox, spacer, badgeLabel);
                    activityTodayBox.getChildren().add(itemBox);
                    
                    // Add spacing between items
                    if (i < todayHistory.size() - 1) {
                        javafx.scene.layout.Pane spacingPaneItem = new javafx.scene.layout.Pane();
                        spacingPaneItem.setPrefHeight(8);
                        activityTodayBox.getChildren().add(spacingPaneItem);
                    }
                }
            }
        }
    }
    
    // Helper method untuk mempersingkat nama workout agar tidak overlapping di chart
    private String shortenWorkoutName(String workoutName) {
        switch (workoutName.toLowerCase()) {
            case "full body": return "FB";
            case "chest": return "C";
            case "arms": return "A";
            case "abs": return "Ab";
            case "legs": return "L";
            case "back": return "B";
            case "cardio": return "Cd";
            default: return workoutName.length() > 2 ? workoutName.substring(0, 2).toUpperCase() : workoutName.toUpperCase();
        }
    }
    
    // Helper method untuk mendapatkan icon berdasarkan workout
    private String getWorkoutIcon(String workoutOption) {
        switch (workoutOption.toLowerCase()) {
            case "chest": return "💪";
            case "arms": return "🦾";  
            case "abs": return "🔥";
            case "legs": return "🦵";
            case "full body": return "🏋️";
            case "back": return "💪";
            default: return "🏃";
        }
    }
    
    // Method untuk force apply styling ultra slim ke chart bars
    private void applyUltraSlimStyling() {
        javafx.application.Platform.runLater(() -> {
            System.out.println("=== Applying Progress Chart Styling ===");
            
            if (caloriesBarChart != null) {
                // Force refresh chart styling
                caloriesBarChart.applyCss();
                caloriesBarChart.autosize();
                
                // Set category gap to make bars thicker
                caloriesBarChart.setCategoryGap(10); // Reduce gap between categories
                caloriesBarChart.setBarGap(0); // Remove gap between bars in same category
                
                // Set category axis properties for better spacing
                if (caloriesBarChart.getXAxis() instanceof javafx.scene.chart.CategoryAxis) {
                    javafx.scene.chart.CategoryAxis xAxis = (javafx.scene.chart.CategoryAxis) caloriesBarChart.getXAxis();
                    xAxis.setGapStartAndEnd(false);
                    xAxis.setTickLabelsVisible(false); // Tetap sembunyikan X-axis labels untuk menghindari overlapping
                    xAxis.setTickMarkVisible(false);
                }
                
                // Set Y-axis properties untuk visibility yang lebih baik
                if (caloriesBarChart.getYAxis() instanceof javafx.scene.chart.NumberAxis) {
                    javafx.scene.chart.NumberAxis yAxis = (javafx.scene.chart.NumberAxis) caloriesBarChart.getYAxis();
                    yAxis.setTickLabelsVisible(true); // Pastikan Y-axis labels terlihat
                    yAxis.setTickMarkVisible(true);
                    yAxis.setMinorTickVisible(false);
                    yAxis.setAutoRanging(true);
                }
                
                // Apply styling directly to bars for thicker appearance
                caloriesBarChart.lookupAll(".chart-bar").forEach(node -> {
                    if (node instanceof javafx.scene.Node) {
                        ((javafx.scene.Node) node).setStyle("-fx-bar-fill: #26b062; -fx-background-color: #26b062; -fx-min-width: 120px; -fx-pref-width: 120px;");
                    }
                });
                
                // Apply styling to axis labels for better visibility
                caloriesBarChart.lookupAll(".axis").forEach(node -> {
                    if (node instanceof javafx.scene.Node) {
                        ((javafx.scene.Node) node).setStyle("-fx-tick-label-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: bold;");
                    }
                });
                
                System.out.println("Applied progress chart styling to calories bar chart");
            }
            
            if (durationBarChart != null) {
                // Force refresh chart styling
                durationBarChart.applyCss();
                durationBarChart.autosize();
                
                // Set category gap to make bars thicker
                durationBarChart.setCategoryGap(10); // Reduce gap between categories
                durationBarChart.setBarGap(0); // Remove gap between bars in same category
                
                // Set category axis properties for better spacing
                if (durationBarChart.getXAxis() instanceof javafx.scene.chart.CategoryAxis) {
                    javafx.scene.chart.CategoryAxis xAxis = (javafx.scene.chart.CategoryAxis) durationBarChart.getXAxis();
                    xAxis.setGapStartAndEnd(false);
                    xAxis.setTickLabelsVisible(false); // Tetap sembunyikan X-axis labels untuk menghindari overlapping
                    xAxis.setTickMarkVisible(false);
                }
                
                // Set Y-axis properties untuk visibility yang lebih baik
                if (durationBarChart.getYAxis() instanceof javafx.scene.chart.NumberAxis) {
                    javafx.scene.chart.NumberAxis yAxis = (javafx.scene.chart.NumberAxis) durationBarChart.getYAxis();
                    yAxis.setTickLabelsVisible(true); // Pastikan Y-axis labels terlihat
                    yAxis.setTickMarkVisible(true);
                    yAxis.setMinorTickVisible(false);
                    yAxis.setAutoRanging(true);
                }
                
                // Apply styling directly to bars for thicker appearance
                durationBarChart.lookupAll(".chart-bar").forEach(node -> {
                    if (node instanceof javafx.scene.Node) {
                        ((javafx.scene.Node) node).setStyle("-fx-bar-fill: #26b062; -fx-background-color: #26b062; -fx-min-width: 120px; -fx-pref-width: 120px;");
                    }
                });
                
                // Apply styling to axis labels for better visibility
                durationBarChart.lookupAll(".axis").forEach(node -> {
                    if (node instanceof javafx.scene.Node) {
                        ((javafx.scene.Node) node).setStyle("-fx-tick-label-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: bold;");
                    }
                });
                
                System.out.println("Applied progress chart styling to duration bar chart");
            }
        });
    }
    
    // Helper method untuk mendapatkan waktu dari history (jika ada)
    private String getTimeFromHistory(ProgressDataStore.WorkoutHistory wh) {
        // Implementasi sederhana, bisa diperbaiki untuk parse time dari XML
        return java.time.LocalTime.now().toString().substring(0, 5); // Format HH:mm
    }

    @FXML
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
}
