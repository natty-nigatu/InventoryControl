package ui.image;

import data.Database;
import data.Staff;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import program.FileManagement;
import ui.CreateStage;

import java.io.File;

public class ImageProfile {
    private FileChooser fileChooser;
    private Button btnAdd, btnDelete, btnClose;
    private ImageProfileFragment profileFragment;
    private Button cancel;
    private Database db;
    private Staff staff;

    public ImageProfile(Button cancel, Database db, Staff staff) {
        this.cancel = cancel;
        this.db = db;
        this.staff = staff;

    }

    public VBox createUI() {

        //create display
        profileFragment = new ImageProfileFragment(staff);

        //create controls
        init_fileChooser();
        init_btnClose();
        init_btnAdd();
        init_btnDelete();

        return init_interface();

    }

    private VBox init_interface() {
        //add elements to v box
        VBox root = new VBox(profileFragment.createUI(), btnDelete, btnAdd, btnClose);

        //format
        root.setSpacing(20);
        root.setPadding(new Insets(30));

        return root;
    }

    private void handle_onClose(){
        Stage stage = (Stage)btnAdd.getScene().getWindow();
        stage.setOnCloseRequest(e -> cancel.fire());
    }

    private void init_btnDelete() {
        btnDelete = new Button("Delete Profile Image");
        btnDelete.setMaxWidth(999);

        //on click
        btnDelete.setOnAction(e -> handle_btnDelete());
    }

    private void handle_btnDelete() {

        if (profileFragment.getFileName().contentEquals("NoImage")) {
            return;
        }

        String header = "Delete Profile Image?";
        String title = "Delete?";
        String desc = "Are you sure? This Action is Irreversible." ;


        //prompt user
        Label lblTitle = new Label(header);
        lblTitle.getStyleClass().add("label-large");

        //buttons
        Button btnYes = new Button("Yes");
        Button btnNo = new Button("No");
        btnNo.setPrefWidth(160);
        btnYes.setPrefWidth(160);

        Label lblDesc = new Label(desc);
        lblDesc.setMaxWidth(350);
        lblDesc.setMinWidth(350);
        lblDesc.setWrapText(true);

        btnNo.setOnAction(e -> {
            Stage stage = (Stage) btnNo.getScene().getWindow();
            stage.close();
        });

        btnYes.setOnAction(e -> {

            //delete action
            FileManagement.delete(staff.getImage());
            staff.setImage("");
            profileFragment.init_image();
            staff.save(db);


            //to save action when closed
            handle_onClose();

            Stage stage = (Stage) btnNo.getScene().getWindow();
            stage.close();
        });

        HBox buttons = new HBox(btnYes, btnNo);
        buttons.setSpacing(20);

        VBox root = new VBox(lblTitle, lblDesc, buttons);
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        new CreateStage(root, title, true);

    }

    private void init_btnAdd() {
        btnAdd = new Button("Add A New Profile Image");
        btnAdd.setMaxWidth(999);
        btnAdd.setPrefHeight(50);

        //click
        btnAdd.setOnAction(e -> handle_btnAdd());
    }

    private void handle_btnAdd() {

        File file = fileChooser.showOpenDialog(btnAdd.getScene().getWindow());

        if (file == null) {
            return;
        }

        String name = db.getNextImage() + getFileExtension(file);

        //get old image to delete
        String old = staff.getImage();

        //set new and save
        staff.setImage(name);
        staff.save(db);

        //copy image
        FileManagement.save(file, name);
        profileFragment.init_image();

        //delete old image
        FileManagement.delete(old);



        //to save action when closed
        handle_onClose();

    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf("."));
        else return "";
    }

    private void init_btnClose(){
        //create, format
        btnClose = new Button("Close");
        btnClose.setMaxWidth(999);
        btnClose.setPrefHeight(30);

        //click
        btnClose.setOnAction(e -> handle_btnClose());
    }

    private void handle_btnClose() {
        cancel.fire();
        Stage stage = (Stage)btnClose.getScene().getWindow();
        stage.close();
    }

    private void init_fileChooser() {

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        //file extension filter to only accept image files
        FileChooser.ExtensionFilter filter =
                new FileChooser.ExtensionFilter("Image Files ", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(filter);
    }
}
