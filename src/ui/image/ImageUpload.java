package ui.image;

import data.Database;
import data.Product;
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
import java.util.List;

public class ImageUpload {
    private FileChooser fileChooser;
    private Button btnAdd, btnDelete, btnClose;
    private ImageDisplayFragment displayFragment;
    private Database db;
    private Product product;

    public ImageUpload(Database db, Product product) {
        this.db = db;
        this.product = product;
    }

    public VBox createUI() {


        //create controls
        init_fileChooser();
        init_btnClose();
        init_btnAdd();
        init_btnDelete();

        return init_interface();

    }

    private VBox init_interface() {
        //create display
        displayFragment = new ImageDisplayFragment(product);
        //add elements to v box

        VBox root = new VBox(displayFragment.createUI(), btnDelete, btnAdd, btnClose);

        //format
        root.setSpacing(20);
        root.setPadding(new Insets(30));

        return root;
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
        Stage stage = (Stage)btnClose.getScene().getWindow();
        stage.close();
    }

    private void init_btnAdd() {

        btnAdd = new Button("Add A New Image");
        btnAdd.setMaxWidth(999);
        btnAdd.setPrefHeight(50);

        //click
        btnAdd.setOnAction(e -> handle_btnAdd());
    }

    private void handle_btnAdd() {

        /*File file = fileChooser.showOpenDialog(btnAdd.getScene().getWindow());
        if (file == null) {
            return;
        }

         */

        List<File> files = fileChooser.showOpenMultipleDialog(btnAdd.getScene().getWindow());

        if(files == null || files.size() == 0)
            return;

        for(File file: files){


            String name = db.getNextImage() + getFileExtension(file);

            //add image to db
            product.addImage(db, name);

            //copy image
            FileManagement.save(file, name);

            //add image to view
            displayFragment.add(name);
        }

    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf("."));
        else return "";
    }

    private void init_fileChooser() {

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        //file extension filter to only accept image files
        FileChooser.ExtensionFilter filter =
                new FileChooser.ExtensionFilter("Image Files ", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(filter);
    }

    private void init_btnDelete() {
        btnDelete = new Button("Delete Image");
        btnDelete.setMaxWidth(999);

        //on click
        btnDelete.setOnAction(e -> handle_btnDelete());
    }

    private void handle_btnDelete() {
        if (displayFragment.getFileName().equals("NoImage")) {
            return;
        }

        String header = "Delete This Image?";
        String title = "Delete?";
        String desc = "Are you sure? This Action is Irreversible" ;

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
            //get file name
            String fileName = displayFragment.getFileName();
            //delete from db
            product.deleteImage(db, fileName);
            //delete from file
            FileManagement.delete(fileName);
            //delete from view
            displayFragment.delete(displayFragment.getImageDisplayed());

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
}
