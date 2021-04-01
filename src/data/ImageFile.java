package data;

import javafx.scene.image.Image;
import program.ImageClient;
import program.LANClient;

import java.io.ByteArrayInputStream;
import java.io.File;

public class ImageFile {
    private Image image;
    private String name;
    static LANClient lanClient;

    public ImageFile(String name) {
        this.name = name;

        //get image file and if found return

        if (!name.isBlank()) {
            ImageFilePacket img = ImageClient.request(new ImageFilePacket(null , name, "send"));

            if(img != null && img.image != null) {
                this.image = new Image(new ByteArrayInputStream(img.image));
                return;
            }
        }

        //set image to a location that doesn't exist to trigger no image
        this.image = new Image("file:x");

    }

    public ImageFile(Image img) {
        this.image = img;
        this.name = "NoImage";
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public Exception getException() {
        return this.image.getException();
    }
}
