package program;

import data.Database;
import ui.main.*;
import ui.management.AccountManagement;
import ui.management.CategoryManagement;
import ui.management.ProductManagement;
import ui.management.StaffManagement;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LANClient extends Thread{

    SalesSummary sales;
    StaffManagement staff;
    ProductManagement product;
    CategoryManagement category;
    AccountManagement account;
    TransactionSummary transaction;
    Restock restock;
    Retail retail;
    Chat chat;
    String type;

    Socket socket;
    DataInputStream in;
    public static final String ip = Database.host;
    final int port = 5060;

    public LANClientSender sender;


    @Override
    public void run() {
        //initialize image client for file transfer
        ImageClient.init();

        while (true)
            try {
                //create connection
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [CLIENT] Looking for server");
                socket = new Socket(ip, port);
                socket.setSoTimeout(10000);
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [CLIENT] Connected to server");
                break;
            } catch (Exception e) {
                try {
                    //try again after 1 second
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {}

                if (!account.hasParent()) {
                    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [CLIENT] Program Closed");
                    return;
                }
            }


        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create sender
        sender = new LANClientSender(socket);
        sender.start();

        //run the corresponding function according to type
        switch (type) {
            case "admin":
                adminListener();
                break;

            case "retail":
                retailListener();
                break;

            case "restock":
                restockListener();
                break;

            default:
                System.out.println("Type Error");
        }

    }

    public void adminListener() {

        try {
            //always listening
            while (true) {
                //read data
                try {
                    String msg = in.readUTF();

                    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [SERVER] " + msg);

                    switch (msg) {
                        case "update":
                            sales.updateObject();
                            staff.updateObject();
                            product.updateObject();
                            category.updateObject();
                            account.updateObject();
                            transaction.updateObject();
                            restock.updateObject();
                            retail.updateObject();
                            break;

                        default:
                            chat.receive(msg);

                    }

                } catch (SocketTimeoutException e) {
                    //check if window is still alive
                    if (!account.hasParent())
                        break;
                } catch (SocketException e) {
                    if(!reconnect())
                        break;
                }


            }

            socket.close();
            in.close();
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [CLIENT] Connection Closed");

        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void setIn() {
        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void retailListener() {

        try {
            //always listening
            while (true) {
                //read data
                try {
                    String msg = in.readUTF();

                    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [SERVER] " + msg);

                    switch (msg) {
                        case "update":
                            account.updateObject();
                            retail.updateObject();
                            break;
                        default:
                            chat.receive(msg);

                    }

                } catch (SocketTimeoutException e) {
                    //check if window is still alive
                    if (!account.hasParent())
                        break;
                } catch (SocketException e) {
                    if(!reconnect())
                        break;
                }


            }

            socket.close();
            in.close();
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [CLIENT] Connection Closed");

        } catch (Exception e) {
            e.printStackTrace();
        }    }

    public void restockListener() {

        try {
            //always listening
            while (true) {
                //read data
                try {
                    String msg = in.readUTF();

                    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [SERVER] " + msg);

                    switch (msg) {
                        case "update":
                            account.updateObject();
                            restock.updateObject();
                            break;
                        default:
                            chat.receive(msg);

                    }

                } catch (SocketTimeoutException e) {
                    //check if window is still alive
                    if (!account.hasParent())
                        break;
                } catch (SocketException e) {
                    if(!reconnect())
                        break;
                }


            }

            socket.close();
            in.close();
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [CLIENT] Connection Closed");

        } catch (Exception e) {
            e.printStackTrace();
        }    }

    public LANClient(SalesSummary sales, StaffManagement staff, ProductManagement product, CategoryManagement category,
                     AccountManagement account, TransactionSummary transaction, Restock restock, Retail retail, Chat chat) {
        this.sales = sales;
        this.staff = staff;
        this.product = product;
        this.category = category;
        this.account = account;
        this.transaction = transaction;
        this.restock = restock;
        this.retail = retail;
        this.chat = chat;

        type = "admin";
    }

    public LANClient(Retail retail, AccountManagement account, Chat chat) {
        this.retail = retail;
        this.account = account;
        this.chat = chat;

        type = "retail";
    }

    public LANClient(Restock restock, AccountManagement account, Chat chat) {
        this.restock = restock;
        this.account = account;
        this.chat = chat;

        type = "restock";
    }

    private boolean reconnect() {
        while (true) {
            //if window is closed
            if (!account.hasParent()) {
                return false;
            }

            try {
                //recreate connection
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [CLIENT] Connection with server lost.");
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [CLIENT] Looking for server");
                socket = new Socket(ip, port);
                socket.setSoTimeout(10000);
                setIn();
                sender.setSocket(socket);
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [CLIENT] Connected to server");
                return true;
            } catch (Exception ex) {
                try {
                    //try again after 1 second
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
