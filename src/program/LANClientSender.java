package program;

import java.io.DataOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LANClientSender extends Thread {

    Socket socket;
    DataOutputStream out;

    @Override
    public void run() {

        try {
            out = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send(String msg) {

        try {
                out.writeUTF(msg);
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [CLIENT] Data sent to server.");

            } catch (Exception e) {
                System.out.println(e);
            }

    }

    public LANClientSender(Socket socket) {

        this.socket = socket;
    }

    public void setSocket(Socket socket) {

        this.socket = socket;

        try {
            out = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
