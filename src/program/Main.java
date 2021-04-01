package program;

import data.Database;
import data.Product;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import ui.CreateStage;
import ui.authentication.LogIn;
import ui.main.Receipt;

import java.util.ArrayList;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Database db = new Database();

        //if connection can't be created don't launch
        if(!db.createConnection())
            return;

        //Program entry, the login window
        LogIn login = new LogIn(db);
        CreateStage stagePrimary = new CreateStage(login.createUI(),"Login");

        //close connection with db and server on close
        stagePrimary.stage.setOnCloseRequest(e -> {
            db.close();
            ImageClient.close();
        });


    }

    public static void main(String[] args) {
        launch(args);
    }
}
