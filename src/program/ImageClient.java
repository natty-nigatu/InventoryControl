package program;

import data.ImageFilePacket;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ImageClient{

    static int port = 5065;

    static Socket socket;
    static ObjectOutputStream out;
    static ObjectInputStream in;
    static boolean connecting = false;

    public static ImageFilePacket request(ImageFilePacket file) {

        /*
        if (connecting) {
            return null;
        }
         */

        if (out == null) {
            return null;
        }

        try {
            System.out.print(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [FILE CLIENT] Sending Image request");
            out.writeObject(file);
            out.flush();
            System.out.println("| Image request sent");

            ImageFilePacket image = (ImageFilePacket) in.readObject();
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [FILE CLIENT] Response received");

            return image;

        } catch (SocketException se) {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [FILE CLIENT] Connection with server lost.");

            try {
                return null;
            }finally {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        init();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void close() {
        try{
            socket.close();
            out.close();
            in.close();
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [FILE CLIENT] Connection Closed");
        } catch (Exception e){}
    }

    public static void init() {
        connecting = true;

        while (true)
            try {
                //create connection
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [FILE CLIENT] Looking for server");
                socket = new Socket(LANClient.ip, port);
                connecting = false;
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [FILE CLIENT] Connected to server");
                break;

            } catch (Exception e) {
                try {
                    //try again after 1 second
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }


        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
