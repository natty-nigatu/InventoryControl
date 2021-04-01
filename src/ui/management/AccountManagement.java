package ui.management;

import data.Database;
import data.ImageFile;
import data.Staff;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import program.ImageClient;
import program.LANClient;
import program.LANClientSender;
import ui.CreateStage;
import ui.authentication.ChangePassword;
import ui.authentication.ChangeUsername;
import ui.authentication.LogIn;
import ui.image.ImageProfile;
import ui.main.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountManagement {
    public LANClient lanClient;
    private Button btnEdit, btnCancel, btnChangeUsername, btnChangePassword, btnLogout;
    private TextField txtName, txtEmail, txtPhone;
    private ImageView imgProfile;
    private Database db;
    private int account;
    private Staff staff;


    public AccountManagement(Database db, int account) {
        //set account db instance
        this.db = db;
        this.account = account;

        this.staff = new Staff(account);
    }


    public VBox createUI(){

        //load staff
        staff.load(db);

        //create controls
        init_txtFields();
        init_imageView();
        init_btnLogout();
        init_btnEdit();
        init_btnCancel();
        init_btnChangeUsername();
        init_btnChangePassword();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((Stage)btnCancel.getScene().getWindow()).setOnCloseRequest(e -> {
                    open = false;
                    db.close();
                    ImageClient.close();
                });
            }
        });

        return init_interface();

    }

    private void setValues(){
        HashMap data = db.getAccountData(account);

        txtName.setText(data.get("name").toString());
        txtEmail.setText(data.get("email").toString());
        txtPhone.setText(data.get("phone").toString());


        String image;
        try {
            image = data.get("image").toString();
        } catch (Exception e) {
            image = "";
        }

        updateImage();
    }



    private VBox init_interface() {
        //top right
        //create labels
        Label lblName = new Label("Name");
        Label lblEmail = new Label("Email");
        Label lblPhone = new Label("Phone");

        //add elements to grid pane and format
        GridPane right = new GridPane();
        right.addColumn(0, lblName, lblEmail, lblPhone);
        right.addColumn(1, txtName, txtEmail, txtPhone, btnEdit, btnCancel);
        right.setHgap(10);
        right.setVgap(15);

        //top
        HBox top = new HBox(imgProfile, right);
        top.setSpacing(20);

        //bottom
        Label lblAccount = new Label("Account Management");
        lblAccount.getStyleClass().add("label-large");
        Label lblUsername = new Label("Username");
        Label lblPassword = new Label("Password");

        //create and format grid pane
        GridPane bottom = new GridPane();
        bottom.add(lblAccount, 0, 0, 3, 1);
        bottom.addRow(1, lblUsername, btnChangeUsername);
        bottom.addRow(2, lblPassword, btnChangePassword);
        bottom.add(btnLogout, 2, 3);
        bottom.setVgap(15);
        bottom.setHgap(10);
        GridPane.setHalignment(lblAccount, HPos.CENTER);
        bottom.setAlignment(Pos.CENTER);

        //root
        VBox root = new VBox(top, bottom);
        root.setSpacing(30);
        root.setPadding(new Insets(30));

        return root;
    }

    private void init_btnChangePassword() {
        btnChangePassword = new Button("Change Password");
        btnChangePassword.setPrefWidth(150);

        //click
        btnChangePassword.setOnAction(e -> handle_btnChangePassword());
    }

    private void handle_btnChangePassword() {
        //create new window and show
        ChangePassword change = new ChangePassword(db, account);
        new CreateStage(change.createUI(), "Change Password", true);
    }

    private void init_btnChangeUsername(){
        btnChangeUsername = new Button("Change Username");
        btnChangeUsername.setPrefWidth(150);

        //on click
        btnChangeUsername.setOnAction(e -> handle_btnChangeUsername());
    }

    private void handle_btnChangeUsername() {
        //create new window and show
        ChangeUsername change = new ChangeUsername(db, account);
        new CreateStage(change.createUI(), "Change Username", true);

    }

    private void init_btnCancel() {
        btnCancel = new Button("Cancel");
        btnCancel.setPrefWidth(200);

        //hidden until needed
        btnCancel.setVisible(false);

        //click
        btnCancel.setOnAction(e -> handle_btnCancel());
    }

    private void handle_btnCancel(){

        btnEdit.setText("Edit");
        txtName.setEditable(false);
        txtEmail.setEditable(false);
        txtPhone.setEditable(false);

        btnCancel.setVisible(false);

        setValues();
    }

    private void init_btnEdit() {

        btnEdit = new Button("Edit");
        btnEdit.setPrefWidth(200);

        btnEdit.setOnAction(e -> handle_btnEdit());
    }

    private void handle_btnEdit() {
        if(btnEdit.getText().contentEquals("Edit")) {//edit
            btnEdit.setText("Save");
            txtName.setEditable(true);
            txtEmail.setEditable(true);
            txtPhone.setEditable(true);

            btnCancel.setVisible(true);
        }
        else {//save

            int phone;
            try{
                phone = Integer.parseInt(txtPhone.getText());}
            catch (Exception e)
            {phone = 0;}

            int result;

            result = db.setAccountData(account, txtName.getText(),
                                        txtEmail.getText(), phone);

            handle_btnCancel();
            lanClient.sender.send("update");
        }
    }

    private void init_txtFields() {
        //create fields
        txtName = new TextField();
        txtEmail = new TextField();
        txtPhone = new TextField();

        //disabled until edit mode
        txtName.setEditable(false);
        txtEmail.setEditable(false);
        txtPhone.setEditable(false);

        //number input only
        try {
            txtPhone.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (!t1.matches("\\d*") || t1.length() > 9)
                        txtPhone.setText(s);
                }
            });
        }
        catch (Exception ex){}

        try {
            txtName.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (t1.length() > 25)
                        txtName.setText(s);
                }
            });
        }
        catch (Exception ex){}

        try {
            txtEmail.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (t1.length() > 25)
                        txtEmail.setText(s);
                }
            });
        }
        catch (Exception ex){}

    }


    private void init_btnLogout() {
        btnLogout = new Button("Logout");
        btnLogout.setPrefWidth(250);
        btnLogout.setPrefHeight(40);

        //click
        btnLogout.setOnAction(e -> handle_btnLogout());
    }

    private void handle_btnLogout() {
        //create login window
        LogIn logIn = new LogIn(db);
        CreateStage stagePrimary = new CreateStage(logIn.createUI(), "Login");

        stagePrimary.stage.setOnCloseRequest(e -> {
            db.close();
            ImageClient.close();
        });

        //close this window
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();
    }

    private void init_imageView() {

        //create image view and format
        imgProfile = new ImageView();
        imgProfile.setFitWidth(200);
        imgProfile.setFitHeight(200);
        imgProfile.setPreserveRatio(true);

        Circle clip = new Circle(100, 100, 100);
        imgProfile.setClip(clip);

        //profile picture handler
        imgProfile.setOnMouseClicked(e -> handle_imgProfile());
    }

    private void updateImage() {

        //get image and if not found set no image

        ImageFile img = new ImageFile(staff.getImage());
        if (img.getException() != null)
            img = new ImageFile(new Image("file:src/assets/error/noImage.png"));


        imgProfile.setImage(img.getImage());
    }

    private void  handle_imgProfile() {
        ImageProfile upload = new ImageProfile(btnCancel, db, staff);
        new CreateStage(upload.createUI(), "Profile Image", true);
        lanClient.sender.send("update");
    }

    boolean open = true;

    public boolean hasParent() {
        //check if it has a parent to close other threads
        return open;
    }


    public void updateObject() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                ArrayList<String> allUsername = db.getAllUsername();

                if (!allUsername.contains(staff.getUsername())) {
                    new Message("Warning", "Your Account Has Been Changed.",
                            "Please contact an admin to learn more about why it has been changed.");
                    handle_btnLogout();
                    return;
                }

                staff.load(db);
                handle_btnCancel();
            }
        });

    }
}
