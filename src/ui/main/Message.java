package ui.main;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.CreateStage;

public class Message {
    public Message(String title, String header, String desc) {
        //class for showing message boxes to user
        Label lblTitle = new Label(header);
        lblTitle.getStyleClass().add("label-large");

        Label lblDesc = new Label(desc);
        lblDesc.setMaxWidth(350);
        lblDesc.setMinWidth(350);
        lblDesc.setWrapText(true);

        Button close = new Button("Close");
        close.setMaxWidth(999);
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });

        VBox root = new VBox(lblTitle, lblDesc, close);
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        new CreateStage(root, title, true);
    }

    public Message(String title, String header, String desc, boolean x) {
        //class for showing message boxes to user
        Label lblTitle = new Label(header);
        lblTitle.getStyleClass().add("label-large");

        Label lblDesc = new Label(desc);
        lblDesc.setMaxWidth(350);
        lblDesc.setMinWidth(350);
        lblDesc.setWrapText(true);

        Button close = new Button("Close");
        close.setMaxWidth(999);
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });

        VBox root = new VBox(lblTitle, lblDesc, close);
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        new CreateStage(root, title);
    }
}
