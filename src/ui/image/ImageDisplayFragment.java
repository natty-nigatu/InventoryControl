package ui.image;

import data.ImageFile;
import data.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

public class ImageDisplayFragment {
    private ImageView view;
    private ArrayList<ImageFile> files;
    private Button btnPrevious, btnNext;
    private int index, total;
    private Product product;

    public ImageDisplayFragment(Product product) {
        this.product = product;
    }

    public StackPane createUI(){
        //receives a list of filenames

        //load images
        init_files();

        //check and show images
        validateImages();
        init_imageView();
        changeImage();

        //create buttons
        init_btnNext();
        init_btnPrevious();

        //create full interface
        return init_interface();

    }

    String getFileName(){
        String name =  files.get(index).toString();

        return name;
    }

    private StackPane init_interface(){
        //Label
        Label top = new Label(product.getName());
        top.getStyleClass().add("label-large");

        //add elements to main pane
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(top);
        mainPane.setCenter(view);
        mainPane.setLeft(btnPrevious);
        mainPane.setRight(btnNext);

        //format
        BorderPane.setAlignment(top, Pos.CENTER);
        BorderPane.setMargin(view, new Insets(5));
        BorderPane.setAlignment(view, Pos.CENTER);

        //add everything to stack pane to preserve ratio
        StackPane root = new StackPane();
        root.getChildren().add(mainPane);
        root.setMinSize(500, 400);
        root.setMaxSize(500, 400);
        StackPane.setAlignment(mainPane, Pos.CENTER);

        return root;
    }

    private void init_files() {

        ArrayList<String> filesList = product.getImages();

        //file directory
        files = new ArrayList<>();

        //load available images
        for (String file : filesList) {
            ImageFile x = new ImageFile(file);
            if(x.getException() == null)
                files.add(x);
            else
                System.out.println(x.getException());
        }
    }

    public ImageFile getImageDisplayed() {
        return files.get(index);
    }

    public void delete(ImageFile img) {
        files.remove(img);
        validateImages();
        handle_btnNext();
    }

    public void add(String file) {
        //check for no image image
        checkNoImage();

        ImageFile x = new ImageFile(file);
        if(x.getException() == null)
            files.add(x);

        validateImages();

        //set new image visible
        int size = files.size() - 1;
        if(size >= 0)
            index = size;

        changeImage();
    }

    private void checkNoImage() {
        if (files.size() > 1) {
            return;
        }

        ImageFile img = files.get(0);

        if(img.getName().equals("NoImage"))
            files.remove(img);
    }

    private void init_btnNext() {
        //create, format
        btnNext = new Button("›");
        btnNext.setMaxHeight(999);
        btnNext.setPrefWidth(60);
        btnNext.setMaxWidth(60);
        btnNext.getStyleClass().add("image-button");
        btnNext.setId("image-button");

        //click
        btnNext.setOnAction(e -> handle_btnNext());

    }

    private void init_btnPrevious(){
        //create, format
        btnPrevious = new Button("‹");
        btnPrevious.setMaxHeight(999);
        btnPrevious.setPrefWidth(60);
        btnPrevious.setMaxWidth(60);
        btnPrevious.getStyleClass().add("image-button");
        btnPrevious.setId("image-button");

        //click
        btnPrevious.setOnAction(e -> handle_btnPrevious());
    }

    private void init_imageView(){

        view = new ImageView();
        view.setFitWidth(350);
        view.setFitHeight(350);
        view.setPreserveRatio(true);
    }

    private void handle_btnNext(){
        //select next index to loop on the files and change image
        if (index < total)
            index++;
        else
            index = 0;

        changeImage();
    }

    private void handle_btnPrevious(){
        //select next index to loop on the files and change image
        if (index > 0)
            index--;
        else
            index = total;

        changeImage();
    }

    private void changeImage() {
        //set image at current index
        view.setImage(files.get(index).getImage());
    }

    private void validateImages(){
        int size = files.size();

        if (size > 0) {
            index = 0;
            total = size - 1;
        }
        else
        {
            String file = "file:src/assets/error/noImage.png";
            files.add(new ImageFile(new Image(file)));
            index = 0;
            total = 0;

        }
    }

}
