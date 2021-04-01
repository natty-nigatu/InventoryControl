package ui.image;

import data.ImageFile;
import data.Staff;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;


public class ImageProfileFragment {
    private ImageView view;
    private ImageFile image;
    private Staff staff;

    public ImageProfileFragment(Staff staff) {
        this.staff = staff;
    }

    public StackPane createUI(){

        //receives a file src
        String fileSrc = staff.getImage();

        //load images
        init_imageView();
        init_image();

        //create full interface
        return init_interface(staff);

    }

    String getFileName(){
        return image.getName();
    }

    private StackPane init_interface(Staff staff){
        //Label
        Label top = new Label(staff.getName());
        top.getStyleClass().add("label-large");

        //add elements to main pane
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(top);
        mainPane.setCenter(view);

        //format
        BorderPane.setAlignment(top, Pos.CENTER);
        BorderPane.setMargin(view, new Insets(30, 5, 5, 5));
        BorderPane.setAlignment(view, Pos.CENTER);

        //add everything to stack pane to preserve ratio
        StackPane root = new StackPane();
        root.getChildren().add(mainPane);
        root.setMinSize(350, 300);
        root.setMaxSize(350, 300);
        StackPane.setAlignment(mainPane, Pos.CENTER);

        return root;
    }

    public void init_image() {
        //file directory
        String fileSrc = staff.getImage();

        //load image if available
            image = new ImageFile(fileSrc);
            if(image.getException() == null)
                view.setImage(image.getImage());
            else {
                image = new ImageFile(new Image("file:src/assets/error/noImage.png"));
                view.setImage(image.getImage());
            }
    }

    private void init_imageView(){
        view = new ImageView();
        view.setFitWidth(250);
        view.setFitHeight(250);
        view.setPreserveRatio(true);
    }

}
