package controller;

public class CLeaderboard {
    @javafx.fxml.FXML
    private javafx.scene.control.Label nameLabel;
    @javafx.fxml.FXML
    private javafx.scene.control.Label scoreLabel;
    @javafx.fxml.FXML
    private javafx.scene.control.Label rankLabel;
    @javafx.fxml.FXML
    private javafx.scene.control.TableView<UserRow> leaderboardTable;

    // Struktur data stack untuk leaderboard
    private java.util.Stack<UserRow> userStack = new java.util.Stack<>();

    public static class UserRow {
        public String username;
        public int poin;
        public int rank;
        public UserRow(String username, int poin, int rank) {
            this.username = username;
            this.poin = poin;
            this.rank = rank;
        }
        public String getUsername() { return username; }
        public int getPoin() { return poin; }
        public int getRank() { return rank; }
    }

    @javafx.fxml.FXML
    public void initialize() {
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        // Load semua user dari XML
        java.util.List<model.UserDataStore.User> users = model.UserDataStore.loadUsers();
        // Urutkan berdasarkan poin (descending)
        users.sort((a, b) -> Integer.compare(b.poin, a.poin));
        userStack.clear();
        int rank = 1;
        for (model.UserDataStore.User u : users) {
            userStack.push(new UserRow(u.username, u.poin, rank));
            rank++;
        }
        // Tampilkan user ranking 1 di label
        if (!userStack.isEmpty()) {
            UserRow top = userStack.get(0);
            nameLabel.setText(top.username);
            scoreLabel.setText(top.poin + " Points");
            rankLabel.setText(top.rank + getRankSuffix(top.rank));
        }
        // Sisanya ke TableView
        javafx.collections.ObservableList<UserRow> tableRows = javafx.collections.FXCollections.observableArrayList();
        for (int i = 1; i < userStack.size(); i++) {
            tableRows.add(userStack.get(i));
        }
        leaderboardTable.setItems(tableRows);
        // Set cell value factory jika belum
        if (leaderboardTable.getColumns().size() >= 3) {
            javafx.scene.control.TableColumn<UserRow, String> rankCol = (javafx.scene.control.TableColumn<UserRow, String>) leaderboardTable.getColumns().get(0);
            javafx.scene.control.TableColumn<UserRow, String> nameCol = (javafx.scene.control.TableColumn<UserRow, String>) leaderboardTable.getColumns().get(1);
            javafx.scene.control.TableColumn<UserRow, String> scoreCol = (javafx.scene.control.TableColumn<UserRow, String>) leaderboardTable.getColumns().get(2);
            rankCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().rank)));
            nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().username));
            scoreCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().poin)));
        }
    }

    private String getRankSuffix(int rank) {
        if (rank == 1) return "st";
        if (rank == 2) return "nd";
        if (rank == 3) return "rd";
        return "th";
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
    private String username = null;
    private String password = null;

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
        // Update leaderboard sesuai user
        loadLeaderboard();
    }
}
