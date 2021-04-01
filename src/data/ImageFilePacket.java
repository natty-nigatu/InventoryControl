package data;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;

public class ImageFilePacket implements Serializable{

    byte[] image;
    String name;
    String type;

    public ImageFilePacket(File file, String name, String type) {

        if (file != null)
            try {
                image = Files.readAllBytes(file.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }

        this.name = name;
        this.type = type;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
