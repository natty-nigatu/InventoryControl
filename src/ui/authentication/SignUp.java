package ui.authentication;

import data.Database;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import program.ImageClient;
import ui.CreateStage;
import ui.main.Message;

public class SignUp {
    private TextField txtUsername;
    private PasswordField txtPassword1, txtPassword2;
    private Button btnLogin, btnSignup;
    private Label lblInfo;
    private Database db;

    public SignUp(Database db) {
        this.db = db;
    }

    public BorderPane createUI() {

        //create controls
        init_textFields();
        init_btnLogin();
        init_btnSignup();

        return init_interface();
    }

    private BorderPane init_interface(){
        //create center
        Label lblUsername = new Label("Username");
        Label lblPassword = new Label("Password");
        Label lblConfirm = new Label("Confirm");

        GridPane center = new GridPane();
        center.addColumn(0, lblUsername, lblPassword, lblConfirm);
        center.addColumn(1, txtUsername, txtPassword1, txtPassword2);
        center.add(lblInfo, 1, 3);
        center.add(btnSignup, 1, 4);

        //format grid pane
        center.setVgap(10);
        center.setHgap(20);
        center.setPadding(new Insets(30, 0, 30, 0));

        //create top
        Label top = new Label("SIGN UP");
        top.getStyleClass().add("label-large");

        //add elements to border pane
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(center);
        root.setBottom(btnLogin);

        //format border pane
        root.setPadding(new Insets(50, 80, 60, 80));
        BorderPane.setAlignment(btnLogin, Pos.BOTTOM_RIGHT);
        BorderPane.setAlignment(top, Pos.CENTER);

        return root;
    }

    private void init_textFields() {
        //info label
        lblInfo = new Label();
        lblInfo.getStyleClass().add("label-red");

        //input fields
        txtUsername = new TextField();
        txtPassword1 = new PasswordField();
        txtPassword2 = new PasswordField();

        //handle passwords inputs
        txtPassword1.setOnKeyTyped(e -> handle_password());
        txtPassword2.setOnKeyTyped(e -> handle_password());

        //handle username inputs
        txtUsername.setOnKeyTyped(e -> handle_username());
    }

    private void handle_username() {
        String text = txtUsername.getText();

        if (text.length() < 5) {
            lblInfo.setText("Username is too short.");
            btnSignup.setDisable(true);
            return;
        }

        handle_password();
    }

    private void handle_password() {

        String text1 = txtPassword1.getText();
        String text2 = txtPassword2.getText();

        if (text2.length() < 8 && text1.length() < 8) {
            lblInfo.setText("Password is too short.");
            btnSignup.setDisable(true);
            return;
        }

        if(!text1.matches(".*\\d.*")) {
            lblInfo.setText("Password must have at least 1 Number.");
            btnSignup.setDisable(true);
            return;
        }

        if(!text1.matches(".*[a-z|A-Z].*")) {
            lblInfo.setText("Password must have at least 1 Letter.");
            btnSignup.setDisable(true);
            return;
        }

        if (!text1.contentEquals(text2)) {
            lblInfo.setText("Passwords do not Match.");
            btnSignup.setDisable(true);
            return;
        }

        if (text2.length() < 8 || text1.length() < 8) {
            lblInfo.setText("Password is too short.");
            btnSignup.setDisable(true);
            return;
        }

        //check for username
        text1 = txtUsername.getText();

        if (text1.length() < 5) {
            lblInfo.setText("Username is too short.");
            btnSignup.setDisable(true);
            return;
        }

        lblInfo.setText("");
        btnSignup.setDisable(false);
    }

    private void init_btnSignup() {

        btnSignup = new Button("Sign Up");
        btnSignup.setPrefWidth(250);
        btnSignup.setPrefHeight(40);

        //disable button until verified
        btnSignup.setDisable(true);

        //click handler
        btnSignup.setOnAction(e -> handle_btnSignUp());

    }

    private void handle_btnSignUp(){

        int result = db.signup(txtUsername.getText());

        switch (result){

            case 0:
                lblInfo.setText("Wrong Username Used.");
                break;

            case -1:
                lblInfo.setText("Connection problem occurred.");
                break;

            default:
                //set new password
                db.setPassword(result, txtPassword1.getText());
                handle_btnLogin();
                new Message("Success", "Sign up succeeded", "Please login into to your account to continue.");
                break;
        }
    }

    private void init_btnLogin(){

        btnLogin = new Button("Log In");
        btnLogin.setPrefSize(60, 30);

        //click handler
        btnLogin.setOnAction(e -> handle_btnLogin());
    }

    private void handle_btnLogin() {

        LogIn logIn = new LogIn(db);
        CreateStage stagePrimary = new CreateStage(logIn.createUI(), "Login");

        stagePrimary.stage.setOnCloseRequest(e -> {
            db.close();
            ImageClient.close();
        });

        Stage stage = (Stage)btnLogin.getScene().getWindow();
        stage.close();

    }
}

