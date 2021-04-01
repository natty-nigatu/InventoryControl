package ui.authentication;

import data.Database;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.main.Message;

import javax.xml.crypto.Data;

public class ChangePassword {
    private PasswordField txtCurrentPassword, txtPassword1, txtPassword2;
    private Button btnSave, btnCancel;
    private Label lblInfo;
    private Database db;
    private int account;

    public ChangePassword(Database db, int account) {
        this.db = db;
        this.account = account;
    }

    public VBox createUI() {

        //create controls
        init_textFields();
        init_btnSave();
        init_btnCancel();

        return init_interface();
    }

    private VBox init_interface(){
        //create grid
        Label lblCurrentPassword = new Label("Current Password");
        Label lblPassword = new Label("New Password");
        Label lblConfirm = new Label("Confirm Password");

        GridPane grid = new GridPane();
        grid.addColumn(0, lblCurrentPassword, lblPassword, lblConfirm);
        grid.addColumn(1, txtCurrentPassword, txtPassword1, txtPassword2, lblInfo, btnSave, btnCancel);

        //create top
        Label lblChangePassword = new Label("Change Password");
        lblChangePassword.getStyleClass().add("label-large");
        lblChangePassword.setAlignment(Pos.CENTER);
        lblChangePassword.setMaxWidth(400);

        //format grid pane
        grid.setVgap(10);
        grid.setHgap(5);
        grid.setPadding(new Insets(30, 0, 30, 0));

        //add elements to v box and format
        VBox root = new VBox(lblChangePassword, grid);
        root.setPadding(new Insets(30));

        return root;
    }

    private void init_btnSave(){
        //create button
        btnSave = new Button("Save");
        btnSave.setPrefWidth(250);

        //inactive until validated
        btnSave.setDisable(true);

        //add event handler
        btnSave.setOnAction(e -> handle_btnSave());
    }

    private void handle_btnSave() {

        String password = db.getPassword(account);

        if (!password.contentEquals(txtCurrentPassword.getText())) {
            new Message("Wrong password", "Wrong current password",
                    "Incorrect password used, Please try again.");
            return;
        }

        db.setPassword(account, txtPassword1.getText());

        new Message("Success", "Password Changed Successfully", "Please click close to continue");
        handle_btnCancel();
    }

    private void init_btnCancel() {
        //create button
        btnCancel = new Button("Cancel");
        btnCancel.setPrefWidth(250);

        //add event handler
        btnCancel.setOnAction(e -> handle_btnCancel());
    }

    private void handle_btnCancel() {
        //get stage and close it
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    private void init_textFields() {
        //label
        lblInfo = new Label();
        lblInfo.getStyleClass().add("label-red");

        //create fields
        txtCurrentPassword = new PasswordField();
        txtPassword1 = new PasswordField();
        txtPassword2 = new PasswordField();

        //input validation handler
        txtPassword1.setOnKeyTyped(e -> handle_password());
        txtPassword2.setOnKeyTyped(e -> handle_password());
        txtCurrentPassword.setOnKeyTyped(e -> handle_currentPassword());
    }

    private void handle_currentPassword() {
        //check current
        String text = txtCurrentPassword.getText();

        if (text.length() < 8) {
            lblInfo.setText("Current Password is too short.");
            btnSave.setDisable(true);
            return;
        }

        handle_password();
    }

    private void handle_password() {

        String text1 = txtPassword1.getText();
        String text2 = txtPassword2.getText();

        if (text2.length() < 8 && text1.length() < 8) {
            lblInfo.setText("Password is too short.");
            btnSave.setDisable(true);
            return;
        }

        if(!text1.matches(".*\\d.*")) {
            lblInfo.setText("Password must have at least 1 Number.");
            btnSave.setDisable(true);
            return;
        }

        if(!text1.matches(".*[a-z|A-Z].*")) {
            lblInfo.setText("Password must have at least 1 Letter.");
            btnSave.setDisable(true);
            return;
        }

        if (!text1.contentEquals(text2)) {
            lblInfo.setText("Passwords do not Match.");
            btnSave.setDisable(true);
            return;
        }

        if (text2.length() < 8 || text1.length() < 8) {
            lblInfo.setText("Password is too short.");
            btnSave.setDisable(true);
            return;
        }

        //check current
        text1 = txtCurrentPassword.getText();

        if (text1.length() < 8) {
            lblInfo.setText("Current Password is too short.");
            btnSave.setDisable(true);
            return;
        }

        lblInfo.setText("");
        btnSave.setDisable(false);
    }
}

