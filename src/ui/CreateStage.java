package ui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateStage {

    public Stage stage;

    public CreateStage(Node input, String title) {

        Scene scene = new Scene(new StackPane(input));
        scene.getStylesheets().add("style/style.css");
        stage = new Stage();
        stage.setScene(scene);

        stage.setTitle(title);
        stage.setResizable(false);
        stage.show();
    }

    public CreateStage(Node input, String title, boolean dialogue) {

        Scene scene = new Scene(new StackPane(input));
        scene.getStylesheets().add("style/style.css");

        stage = new Stage();

        if (!dialogue) {
            stage.setOpacity(0.97);
            scene.setOnMouseClicked(e -> stage.close());
        }

        stage.setScene(scene);

        stage.setTitle(title);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
