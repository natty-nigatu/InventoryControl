package ui.main;

import data.Database;
import data.ImageFile;
import data.Staff;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import program.LANClient;


public class Chat {

    public LANClient lanClient;
    private Button btnSend;
    private TextField txtMsg;
    private VBox messageList;
    private ScrollPane messages;
    private int account;
    private Database db;
    private Image img = null;

    public Chat(int account, Database db){
        this.account = account;
        this.db = db;

    }


    public BorderPane createUI(){

        init_txtMsg();
        init_btnSend();
        init_view();

        BorderPane root = new BorderPane();
        root.setCenter(messages);

        HBox bottom = new HBox(txtMsg, btnSend);
        bottom.setSpacing(15);
        bottom.setPadding(new Insets(20, 0, 0, 0));
        bottom.setPrefWidth(700);

        root.setBottom(bottom);

        root.setPadding(new Insets(30));

        return root;

    }

    private void init_txtMsg(){
        txtMsg = new TextField();

        txtMsg.setPrefWidth(600);
        txtMsg.setPrefHeight(40);
        txtMsg.setOnKeyReleased(e -> handle_txtEnter(e));
    }

    private void handle_txtEnter(KeyEvent e){

        if(e.getCode().name().equals("ENTER"))
            handle_btnSend();

    }

    private void init_btnSend(){
        btnSend = new Button("Send");

        btnSend.setPrefWidth(100);
        btnSend.setPrefHeight(40);

        btnSend.setOnAction(e-> handle_btnSend());
    }

    private void handle_btnSend(){

        if(txtMsg.getText().isBlank())
            return;

        if(img == null) {
            Staff staff = new Staff(account);
            staff.load(db);

            ImageFile file = new ImageFile(staff.getImage());
            if (file.getException() != null)
                file = new ImageFile(new Image("file:src/assets/error/noImage.png"));

            img = file.getImage();
        }

        lanClient.sender.send("" + account + ":" + txtMsg.getText());
        send(txtMsg.getText());
        txtMsg.clear();
    }

    private void init_view(){

        messageList = new VBox();
        messageList.setSpacing(15);
        messageList.setMaxWidth(999);
        messageList.setPrefWidth(680);
        messageList.setPadding(new Insets(0, 0, 0, 20));

        messages = new ScrollPane();
        messages.setContent(messageList);

        messageList.heightProperty().addListener(o -> messages.setVvalue(1D));

    }

    public void receive(String msg){

        int index = msg.indexOf(":");
        int id = Integer.parseInt(msg.substring(0, index));
        msg = msg.substring(index + 1);

        Staff s = new Staff(id);
        s.load(db);

        Image image = new ImageFile(s.getImage()).getImage();
        if (image.getException() != null)
            image = new Image("file:src/assets/error/noImage.png");

        Label lblName = new Label(s.getName());
        Label lblMsg = new Label(msg);


        ImageView view = new ImageView(image);
        view.setFitWidth(60);
        view.setFitHeight(60);
        Circle circle = new Circle(30, 30, 30);
        view.setClip(circle);

        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        lblName.setAlignment(Pos.CENTER_LEFT);
        lblMsg.setStyle("-fx-font-size: 15px;");
        lblMsg.setAlignment(Pos.CENTER_LEFT);
        lblMsg.setWrapText(true);

        VBox msgPart = new VBox(lblName, lblMsg);
        msgPart.setSpacing(5);

        HBox box = new HBox(view, msgPart);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(15);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageList.getChildren().add(box);
            }
        });


    }

    private void send(String msg){
        ImageView view = new ImageView(img);
        view.setFitWidth(60);
        view.setFitHeight(60);
        Circle circle = new Circle(30, 30, 30);
        view.setClip(circle);

        Label lblName = new Label("You");
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        lblName.setMaxWidth(999);
        lblName.setAlignment(Pos.CENTER_RIGHT);
        Label lblMsg = new Label(msg);
        lblMsg.setMaxWidth(999);
        lblMsg.setStyle("-fx-font-size: 15px;");
        lblMsg.setAlignment(Pos.CENTER_RIGHT);
        lblMsg.setTextAlignment(TextAlignment.RIGHT);
        lblMsg.setWrapText(true);

        VBox msgPart = new VBox(lblName, lblMsg);
        msgPart.setSpacing(5);

        HBox box = new HBox(msgPart, view);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(15);

        messageList.getChildren().add(box);
    }
}
