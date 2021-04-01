package program;

import data.ImageFile;
import data.ImageFilePacket;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManagement{
    private static final String dir ="src/assets/images/";

    public static boolean save(File src, String name) {

        try {
            //create packet to transfer and send
            ImageFilePacket imageFilePacket = new ImageFilePacket(src, name, "save");
            ImageClient.request(imageFilePacket);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void delete(String fileName) {

        ImageFilePacket imageFilePacket = new ImageFilePacket(null, fileName, "delete");
        ImageClient.request(imageFilePacket);

    }
}
