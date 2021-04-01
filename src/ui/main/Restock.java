package ui.main;

import data.Category;
import data.Database;
import data.Product;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import program.LANClient;
import ui.CreateStage;
import ui.image.ImageUpload;

import java.util.ArrayList;

public class Restock {
    public LANClient lanClient;
    //create ui controls that will be accessed by the class
    private Label lblInfoRed, lblInfoGreen;
    private Button btnSearch, btnSave, btnEdit, btnImages, btnNew;
    private TextField txtName, txtColor, txtSize, txtId, txtPrice, txtInStock, txtQty;
    private ComboBox comboSex, comboCategory;
    private String mode;
    private Database db;
    private Product product;
    private ArrayList<Number> allIds;
    private int account;

    public Restock(Database db, int account) {
        this.db = db;
        this.account = account;
    }

    public HBox createUI() {
        //starts in view mode
        mode = "view";

        //create controls
        init_txtFields();
        init_btnSearch();
        init_comboBoxes();
        init_btnSave();
        init_btnImages();
        init_btnEdit();
        init_btnNew();
        updateObject();

        return init_interface();
    }

    private HBox init_interface() {
        //Create Labels
        Label lblName = new Label("Name");
        Label lblCategory = new Label("Category");
        Label lblColor = new Label("Color");
        Label lblSize = new Label("Size");
        Label lblGender = new Label("Gender");
        Label lblId = new Label("Id");
        Label lblPrice = new Label("Price");
        Label lblInStock = new Label("In Stock");
        Label lblQty = new Label("Arrival Qty");

        //create and add nodes to grid pane
        GridPane left = new GridPane();
        GridPane right = new GridPane();

        left.addColumn(0, lblInfoRed, lblName, lblCategory, lblColor, lblSize, lblGender);
        GridPane.setColumnSpan(lblInfoRed, 2);
        left.addColumn(1, txtName, comboCategory, txtColor, txtSize, comboSex);
        right.addColumn(0, lblId, btnNew, lblPrice, lblInStock, lblQty);
        right.addColumn(1, txtId, btnSearch, txtPrice, txtInStock, txtQty);

        left.add(lblInfoGreen, 0, 0, 2, 1);

        //add multiple colspan buttons
        right.add(btnSave, 0, 5, 2, 1);
        left.add(btnImages, 0, 6, 2, 1);
        right.add(btnEdit, 0, 6, 2, 1);

        //format grid panes
        left.setHgap(10);
        left.setVgap(30);
        right.setHgap(10);
        right.setVgap(30);

        //create separator
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        //create add nodes and return h box
        HBox root = new HBox(left, separator, right);
        root.setSpacing(10);
        root.setPadding(new Insets(30));

        return root;
    }

    private void init_txtFields() {
        //label for info
        lblInfoRed = new Label("");
        lblInfoRed.getStyleClass().add("label-large");
        lblInfoRed.getStyleClass().add("label-red");
        lblInfoGreen = new Label("");
        lblInfoGreen.getStyleClass().add("label-large");
        lblInfoGreen.getStyleClass().add("label-green");

        //create text fields
        txtName = new TextField();
        txtColor = new TextField();
        txtSize = new TextField();
        txtId = new TextField();
        txtPrice = new TextField();
        txtInStock = new TextField();
        txtQty = new TextField();

        //fields are disabled unless in edit
        txtName.setEditable(false);
        txtColor.setEditable(false);
        txtSize.setEditable(false);
        txtPrice.setEditable(false);
        txtInStock.setEditable(false);
        txtQty.setEditable(false);

        try {
            txtSize.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (t1.length() > 25)
                        txtSize.setText(s);
                }
            });
        }
        catch (Exception ex){}

        try {
            txtColor.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (t1.length() > 25)
                        txtColor.setText(s);
                }
            });
        }
        catch (Exception ex){}

        try {
            txtName.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (t1.length() > 25)
                        txtName.setText(s);
                }
            });
        }
        catch (Exception ex){}

        //number handle
        try {
            txtId.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    //number only
                    if (!t1.matches("\\d*") || t1.length() > 9)
                        txtId.setText(s);

                    //clear lblInfo data
                    lblInfoRed.setText("");
                    lblInfoGreen.setText("");

                    //clear data, it's associated to id
                    if (mode.equals("view")) {
                        product = null;
                        txtQty.setEditable(false);
                        clearView();
                    }
                }
            });
        }
        catch (Exception ex){}

        //number input only qty
        try {
            txtQty.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (!t1.matches("\\d*") || t1.length() > 9)
                        txtQty.setText(s);
                }
            });
        }
        catch (Exception ex){}

        //decimal only for price
        txtPrice.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                    txtPrice.setText(oldValue);
                } else if (newValue.matches("\\d*") && newValue.length() > 6) {
                    txtPrice.setText(oldValue);
                }else if (newValue.matches("\\d*(\\.\\d{0,2})?") && newValue.length() > 9) {
                    txtPrice.setText(oldValue);
                }
            }
        });

        //handle enter
        txtId.setOnKeyReleased(e -> handle_enter(e));
        txtQty.setOnKeyReleased(e -> handle_enter(e));
    }

    private void init_btnNew() {
        btnNew = new Button("Add New");
        btnNew.setMaxWidth(999);
        btnNew.setPrefHeight(30);

        //on click
        btnNew.setOnAction(e -> handle_btnNew());
    }

    private void handle_btnNew(){
        product = null;
        lblInfoGreen.setText("");
        lblInfoRed.setText("");

        mode = "new";

        //modify controls
        btnNew.setDisable(true);
        btnSearch.setDisable(true);
        btnImages.setDisable(true);
        btnEdit.setText("Cancel");
        btnSave.setText("Save");

        //activate inputs
        txtName.setEditable(true);
        txtColor.setEditable(true);
        txtSize.setEditable(true);
        txtPrice.setEditable(true);
        txtQty.setEditable(true);

        //clear fields
        clearView();

        try {
            comboCategory.getSelectionModel().select(0);
            comboSex.getSelectionModel().select(0);
        } catch (Exception e){}
    }

    private void init_btnSave() {
        btnSave = new Button("Add Qty");
        btnSave.setPrefWidth(300);
        btnSave.setPrefHeight(30);

        //on click
        btnSave.setOnAction(e -> handle_btnSave());
    }

    private void handle_btnSave() {
        if(mode.contentEquals("view")){//add qty btn
            handle_btnAddQty();
        }
        else {
            if(mode.contentEquals("edit")){//edit save

                handle_editSave();
            }
            else {//new save

                handle_newSave();
            }
        }
    }

    private void handle_btnAddQty() {
        if (product == null) {
            lblInfoRed.setText("No Product Selected.");
            lblInfoGreen.setText("");
            txtId.requestFocus();
            return;
        }

        if (txtQty.getText().isBlank()) {
            lblInfoRed.setText("Qty is Empty.");
            lblInfoGreen.setText("");
            return;
        }

        int qty = Integer.parseInt(txtQty.getText());

        if (qty == 0) {
            lblInfoRed.setText("Qty is Zero.");
            lblInfoGreen.setText("");
            return;
        }

        product.setInCart(qty);
        product.restock(db, account);

        lanClient.sender.send("update");



        lblInfoRed.setText("");
        lblInfoGreen.setText("Arrived Product Added.");
        txtId.requestFocus();
        product = null;
        clearView();
    }

    private void init_btnSearch() {
        btnSearch = new Button("Search");
        btnSearch.setMaxWidth(300);
        btnSearch.setPrefHeight(30);

        //onclick
        btnSearch.setOnAction(e -> handle_btnSearch());
    }

    private void handle_btnSearch() {

        if (txtId.getText().isBlank()) {
            lblInfoGreen.setText("");
            lblInfoRed.setText("Id is Empty.");
            txtQty.setEditable(false);
            txtId.requestFocus();
            return;
        }

        if (!searchProduct()) {
            lblInfoGreen.setText("");
            lblInfoRed.setText("Product is not registered.");
            txtQty.setEditable(false);
            txtId.requestFocus();
            return;
        }

        loadView();
        lblInfoRed.setText("");
        lblInfoGreen.setText("Product Found.");
        txtQty.setEditable(true);
        txtQty.requestFocus();

    }

    private boolean searchProduct() {
        //get searched id
        int id = Integer.parseInt(txtId.getText());

        //if it exits create object
        if (allIds.contains(id)) {
            product = new Product(id);
            product.load(db);
            return true;
        }

        return false;
    }
    private void init_comboBoxes() {
        comboCategory = new ComboBox();
        comboSex = new ComboBox();

        comboCategory.setMaxWidth(999);
        comboSex.setMaxWidth(999);

        //initialize values for gender
        comboSex.getItems().add("Boys");
        comboSex.getItems().add("Girls");
        comboSex.getItems().add("Men");
        comboSex.getItems().add("Women");
        comboSex.getItems().add("Unisex");

    }

    private void init_btnEdit() {

        btnEdit = new Button("Edit Data");
        btnEdit.setPrefWidth(300);
        btnEdit.setPrefHeight(30);

        //click
        btnEdit.setOnAction(e -> handle_btnEdit());
    }

    private void handle_btnEdit() {
        if(mode.contentEquals("view")){//edit button
            if (product == null) {
                lblInfoRed.setText("No Product Selected.");
                lblInfoGreen.setText("");
                txtId.requestFocus();
                return;
            }
            //clear info
            lblInfoGreen.setText("");
            lblInfoRed.setText("");

            mode = "edit";

            //modify controls
            btnNew.setDisable(true);
            btnSearch.setDisable(true);
            btnImages.setDisable(true);
            btnEdit.setText("Cancel");
            btnSave.setText("Save");

            //activate inputs
            txtId.setEditable(false);
            txtName.setEditable(true);
            txtColor.setEditable(true);
            txtSize.setEditable(true);
            txtPrice.setEditable(true);

        }
        else {//cancel button
            mode = "view";

            //modify controls
            btnNew.setDisable(false);
            btnSearch.setDisable(false);
            btnImages.setDisable(false);
            btnEdit.setText("Edit Data");
            btnSave.setText("Add Qty");

            //deactivate inputs
            txtId.setEditable(true);
            txtName.setEditable(false);
            txtColor.setEditable(false);
            txtSize.setEditable(false);
            txtPrice.setEditable(false);
            txtInStock.setEditable(false);

            //clear
            txtId.setText("");
            txtName.setText("");
            txtColor.setText("");
            txtSize.setText("");
            txtPrice.setText("");
            txtInStock.setText("");


            //clear info
            lblInfoRed.setText("");

            loadView();
        }

    }

    private void init_btnImages() {

        btnImages = new Button("Images");
        btnImages.setPrefWidth(300);
        btnImages.setPrefHeight(30);

        //click
        btnImages.setOnAction(e -> handle_btnImages());
    }

    private void handle_btnImages() {
        if (product == null) {
            lblInfoRed.setText("No Product Selected.");
            lblInfoGreen.setText("");
            txtId.requestFocus();
            return;
        }

        product.setImages(db);
        ImageUpload image = new ImageUpload(db, product);

        new CreateStage(image.createUI(), "Images", true);
    }

    private Category getCategoryObject() {
        for (Object c : comboCategory.getItems()) {
            if (((Category) c).getId() == product.getCategory()) {
                return (Category)c;
            }
        }

        return null;
    }

    public void updateObject() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                allIds = db.getAllProduct();

                ObservableList<Category> categories = db.getCategories();
                comboCategory.setItems(categories);

                clearView();
                lblInfoRed.setText("");
                lblInfoGreen.setText("");
                txtId.requestFocus();

                if (product != null && allIds.contains((Number)product.getId())) {
                    product.load(db);
                    loadView();
                }
            }
        });
    }

    private void loadView() {
        if (product == null)
            return;

        txtName.setText(product.getName());
        comboCategory.getSelectionModel().select(getCategoryObject());
        txtColor.setText(product.getColor());
        txtSize.setText(product.getSize());
        comboSex.getSelectionModel().select(product.getGender());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtInStock.setText(String.valueOf(product.getQty()));

        //clear arrival qty txt
        txtQty.setText("");
    }

    private void clearView() {
        if(txtInStock.getText().isBlank() && txtName.getText().isBlank())
            return;

        txtName.setText("");
        comboCategory.getSelectionModel().select(-1);
        txtColor.setText("");
        txtSize.setText("");
        comboSex.getSelectionModel().select(-1);
        txtPrice.setText("");
        txtInStock.setText("");

        //clear arrival qty txt
        txtQty.setText("");
    }

    private void handle_enter(KeyEvent e) {

        if (!mode.equals("view")) {
            return;
        }


        if(e.getCode().toString().equals("ENTER"))
        {
            if (e.getSource() == txtId) {
                handle_btnSearch();
            }
            if(e.getSource() == txtQty)
                handle_btnAddQty();
        }
    }

    private void handle_editSave() {

        if (txtName.getText().isBlank()) {
            lblInfoRed.setText("Name Is Empty.");
            lblInfoGreen.setText("");
            txtName.requestFocus();
            return;
        }

        //get qty
        int qty;
        if (txtQty.getText().isBlank()) {
            qty = 0;
        } else {
            qty= Integer.parseInt(txtQty.getText());
        }

        //get price
        float price;
        if (txtPrice.getText().isBlank()) {
            price = 0;
        } else {
            price = Float.parseFloat(txtPrice.getText());
        }

        //add to product object
        product.setName(txtName.getText());
        product.setCategory(((Category) comboCategory.getSelectionModel().getSelectedItem()).getId());
        product.setColor(txtColor.getText());
        product.setSize(txtSize.getText());
        product.setGender(comboCategory.getSelectionModel().getSelectedIndex());
        product.setPrice(price);
        product.setInCart(qty);

        //save object to db
        product.restock(db, account);

        lanClient.sender.send("update");



        //revert controls
        handle_btnEdit();//cancel btn will run form this
        txtId.requestFocus();

        //inform
        lblInfoGreen.setText("Product Change Saved.");
    }

    private void handle_newSave() {

        if (txtId.getText().isBlank() || Integer.parseInt(txtId.getText()) == 0) {
            lblInfoRed.setText("Id Is Empty.");
            lblInfoGreen.setText("");
            txtId.requestFocus();
            return;
        }

        if (allIds.contains(Integer.parseInt(txtId.getText()))) {
            lblInfoRed.setText("Id Taken By Other Product.");
            lblInfoGreen.setText("");
            txtId.requestFocus();
            return;
        }

        if (txtName.getText().isBlank()) {
            lblInfoRed.setText("Name Is Empty.");
            lblInfoGreen.setText("");
            txtName.requestFocus();
            return;
        }

        if (txtQty.getText().isBlank() || Integer.parseInt(txtQty.getText()) == 0){
            lblInfoRed.setText("Arrival Quantity Is Empty.");
            lblInfoGreen.setText("");
            txtQty.requestFocus();
            return;
        }

        //get qty
        int qty;
        if (txtQty.getText().isBlank()) {
            qty = 0;
        } else {
            qty= Integer.parseInt(txtQty.getText());
        }

        //get price
        float price;
        if (txtPrice.getText().isBlank()) {
            price = 0;
        } else {
            price = Float.parseFloat(txtPrice.getText());
        }

        //create a new product
        Product product = new Product(Integer.parseInt(txtId.getText()));

        //add to product object
        product.setName(txtName.getText());
        product.setCategory(((Category) comboCategory.getSelectionModel().getSelectedItem()).getId());
        product.setColor(txtColor.getText());
        product.setSize(txtSize.getText());
        product.setGender(comboCategory.getSelectionModel().getSelectedIndex());
        product.setPrice(price);
        product.setQty(qty);

        //save to db
        product.add(db);

        lanClient.sender.send("update");



        //add to id list
        allIds.add(product.getId());

        //revert controls
        handle_btnEdit();//cancel btn will run form this

        //clear
        clearView();
        txtId.requestFocus();

        //inform
        lblInfoGreen.setText("Product Saved.");

    }
}
