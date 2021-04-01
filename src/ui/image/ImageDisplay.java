package ui.image;

import data.Database;
import data.Product;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ImageDisplay {
    private ImageDisplayFragment displayFragment;
    private Button btnClose;
    private Product product;

    public ImageDisplay(Product product) {
        this.product = product;
    }

    public VBox createUI() {

        //create image display pane
        displayFragment = new ImageDisplayFragment(product);

        //button
        init_btnClose();

        return init_interface();

    }

    private VBox init_interface() {
        //add elements to v box
        VBox root = new VBox(displayFragment.createUI(), btnClose);

        //format
        root.setPadding(new Insets(30));
        root.setSpacing(30);

        return root;
    }

    private void init_btnClose() {
        //create, format
        btnClose = new Button("Close");
        btnClose.setMaxWidth(999);
        btnClose.setPrefHeight(30);

        //click
        btnClose.setOnAction(e -> handle_btnClose());
    }

    private void handle_btnClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
