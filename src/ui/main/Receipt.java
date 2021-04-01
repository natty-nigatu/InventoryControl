package ui.main;

import data.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ui.CreateStage;

import javax.print.DocFlavor;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Receipt {

    public Receipt(ArrayList<Product> sold, int taxPercent) {

        //create grid
        GridPane grid = new GridPane();


        //calculate values
        double sub = 0, total = 0, tax = 0;
        int items = 0;

        //header
        grid.addRow(0, new Label("Item"), new Label("Qty"),
                new Label("Unit Price"),new Label("Total Price"));
        grid.add(new Separator(), 0, 1, 4, 1);

        //start form row 2
        int row = 2;

        for (Product p : sold) {
            items += p.getInCart();
            sub += p.getPrice() * p.getInCart();

            grid.addRow(row, new Label(p.getName()), new Label(String.valueOf(p.getInCart())),
                    new Label(String.valueOf(p.getPrice())), new Label(String.valueOf(p.getPrice() * p.getInCart())));

            row += 1;
        }

        tax = sub * taxPercent * 0.01;
        total = sub + tax;

        DecimalFormat df = new DecimalFormat("0.00");

        row = 0;
        GridPane grid2 = new GridPane();
        //separator
        grid2.add(new Separator(), 0, row, 5, 1);
        row += 1;


        //add others

        grid2.add(new Label("Items"), 2, row);
        grid2.add(new Label(String.valueOf(items)), 3, row);
        row += 1;

        grid2.add(new Label("Subtotal"), 2, row);
        grid2.add(new Label(df.format(sub)), 3, row);
        row += 1;

        grid2.add(new Label("Tax"), 2, row);
        grid2.add(new Label(df.format(tax)), 3, row);
        row += 1;

        grid2.add(new Label("total"), 2, row);
        grid2.add(new Label(df.format(total)), 3, row);

        //format
        grid.setVgap(15);
        grid.setHgap(15);
        grid2.setHgap(30);
        grid2.setVgap(10);
        grid2.setMaxWidth(999);

        //header
        Label top = new Label("Thank You!");
        top.getStyleClass().add("label-large");
        top.setMaxWidth(999);
        top.setAlignment(Pos.CENTER);

        VBox root = new VBox(top, grid, grid2);
        root.setSpacing(5);
        root.setPadding(new Insets(30));

        new CreateStage(root, "Receipt");
    }
}
