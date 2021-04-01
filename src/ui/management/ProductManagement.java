package ui.management;

import data.Category;
import data.Database;
import data.Product;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import program.LANClient;
import ui.CreateStage;
import ui.image.ImageUpload;

import java.util.ArrayList;

public class ProductManagement {
    public LANClient lanClient;
    private Button btnEdit, btnCancel, btnImages, btnTaxEdit, btnTaxCancel, btnAll, btnDelete;
    private TextField txtId, txtName, txtColor, txtSize, txtQty, txtPrice, txtTax;
    private ComboBox comboCategory, comboSex, comboListOf;
    private TableView tblInventory;
    private String mode;
    private Label lblInfo;
    private Database db;
    private ArrayList<Number> allIds;
    private FilteredList<Product> allFilteredProducts;
    private ObservableList<Product> allProducts;
    private int account;

    public ProductManagement(Database db, int account) {
        this.db = db;
        this.account = account;
    }

    public VBox createUI() {

        //starts in view mode
        mode = "view";

        //create controls
        init_btnEdit();
        init_btnCancel();
        init_btnImages();
        init_txtFields();
        init_combos();
        init_btnAll();
        init_tblInventory();
        init_btnTaxEdit();
        init_btnTaxCancel();
        init_btnDelete();
        loadCategory();
        updateObject();


        return init_interface();
    }

    private VBox init_interface() {
        //Left side
        Label lblInventory = new Label("Inventory");
        lblInventory.getStyleClass().add("label-large");
        lblInventory.setMaxWidth(999);
        lblInventory.setAlignment(Pos.CENTER);

        //list of
        Label lblListOf = new Label("List of");
        HBox top = new HBox(lblListOf, comboListOf, btnAll);
        top.setSpacing(20);

        VBox left = new VBox(lblInventory, top, tblInventory);
        left.setSpacing(15);

        //right side
        //create labels
        Label lblId = new Label("ID");
        Label lblName = new Label("Name");
        Label lblCategory = new Label("Category");
        Label lblColor = new Label("Color");
        Label lblSize = new Label("Size");
        Label lblGender = new Label("Gender");
        Label lblQty = new Label("In Stock");
        Label lblPrice = new Label("Price");

        //right side
        //add elements to grid pane
        GridPane grid = new GridPane();
        grid.addColumn(0, lblId, lblName, lblCategory, lblColor, lblSize, lblGender, lblQty, lblPrice);
        grid.addColumn(1, txtId, txtName, comboCategory, txtColor, txtSize, comboSex, txtQty, txtPrice);

        //add multi span elements
        grid.addRow(8, btnDelete, btnImages);//, 0, 8, 2, 1);
        grid.add(btnEdit, 0, 9, 2, 1);
        grid.add(btnCancel, 0, 10, 2, 1);
        grid.setHgap(10);
        grid.setVgap(15);


        //add elements to right
        Label lblDetails = new Label("Details");
        lblDetails.getStyleClass().add("label-large");
        lblDetails.setMaxWidth(999);
        lblDetails.setAlignment(Pos.CENTER);
        VBox right = new VBox(lblDetails, lblInfo, grid);
        right.setSpacing(10);
        right.setAlignment(Pos.CENTER);

        //add left and right to h box
        HBox main = new HBox(left, right);
        main.setSpacing(20);

        //add tax controls
        Label lblTax = new Label("VAT ( % )");
        lblTax.getStyleClass().add("label-large");
        HBox tax = new HBox(lblTax, txtTax, btnTaxEdit, btnTaxCancel);
        tax.setSpacing(20);

        VBox root = new VBox(main, new Separator(), tax);
        root.setSpacing(10);
        root.setPadding(new Insets(10, 20, 10, 20));

        return root;
    }

    private void init_btnDelete() {
        btnDelete = new Button("Delete");

        //on click
        btnDelete.setOnAction(e -> handle_btnDelete());
    }

    private void handle_btnDelete() {
        if (tblInventory.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        Product p = (Product)tblInventory.getSelectionModel().getSelectedItem();

        String header = "Delete " + p.getName() + "?";
        String title = "Delete?";
        String desc = "Are you sure? This Action is Irreversible and unrecommended." +
                "\n\nIt may result in transaction data fragment being lost." ;

        //prompt user
        Label lblTitle = new Label(header);
        lblTitle.getStyleClass().add("label-large");

        //buttons
        Button btnYes = new Button("Yes");
        Button btnNo = new Button("No");
        btnNo.setPrefWidth(160);
        btnYes.setPrefWidth(160);

        Label lblDesc = new Label(desc);
        lblDesc.setMaxWidth(350);
        lblDesc.setMinWidth(350);
        lblDesc.setWrapText(true);

        btnNo.setOnAction(e -> {
            Stage stage = (Stage) btnNo.getScene().getWindow();
            stage.close();
        });

        btnYes.setOnAction(e -> {

            //delete action

            allProducts.remove(p);
            p.delete(db);

            lanClient.sender.send("update");


            refreshTable();

            Stage stage = (Stage) btnNo.getScene().getWindow();
            stage.close();
        });

        HBox buttons = new HBox(btnYes, btnNo);
        buttons.setSpacing(20);

        VBox root = new VBox(lblTitle, lblDesc, buttons);
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        new CreateStage(root, title, true);
    }

    private void init_txtFields() {
        //info label
        lblInfo = new Label();
        lblInfo.getStyleClass().add("label-red");

        //inputs
        txtId = new TextField();
        txtName = new TextField();
        txtColor = new TextField();
        txtSize = new TextField();
        txtQty = new TextField();
        txtPrice = new TextField();
        txtTax = new TextField();

        //disabled until edit mode
        txtId.setEditable(false);
        txtName.setEditable(false);
        txtColor.setEditable(false);
        txtSize.setEditable(false);
        txtQty.setEditable(false);
        txtPrice.setEditable(false);
        txtTax.setEditable(false);

        //tax wider
        txtTax.setPrefColumnCount(16);

        //id check
        txtId.setOnKeyTyped(e -> handle_input());
        txtName.setOnKeyTyped(e -> handle_input());
        txtColor.setOnKeyTyped(e -> handle_input());
        txtSize.setOnKeyTyped(e -> handle_input());
        txtQty.setOnKeyTyped(e -> handle_input());
        txtPrice.setOnKeyTyped(e -> handle_input());

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
            txtName.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (t1.length() > 25)
                        txtName.setText(s);
                }
            });
        }
        catch (Exception ex){}


        //number input only id
        try {
            txtId.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (!t1.matches("\\d*") || t1.length() > 9)
                        txtId.setText(s);
                }
            });
        }
        catch (Exception ex){}

        //number input only qty
        try {
            txtQty.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (!t1.matches("\\d*") || t1.length() > 5)
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

        //number input only tax
        try {
            txtTax.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (!t1.matches("\\d*") || t1.length() > 2)
                        txtTax.setText(s);
                }
            });
        }
        catch (Exception ex){}

    }

    private void handle_input() {

        if(mode.contentEquals("view"))
            return;

        if (txtId.getText().length() == 0) {
            btnEdit.setDisable(true);
            lblInfo.setText("Id Cannot be Empty");
            return;
        }

        if (txtName.getText().length() == 0 || txtName.getText().isBlank()) {

            btnEdit.setDisable(true);
            lblInfo.setText("Name Cannot be Empty");
            return;
        }

        //check if id is taken
        try {

            if (allIds.contains(Integer.parseInt(txtId.getText()))) {
                btnEdit.setDisable(true);
                lblInfo.setText("Product Id already taken");
            } else {

                btnEdit.setDisable(false);
                lblInfo.setText("");
            }
        }catch (Exception e){}
    }

    private void init_btnEdit() {
        //create
        btnEdit = new Button("Edit");
        btnEdit.setPrefWidth(240);
        btnEdit.setPrefHeight(30);

        //click
        btnEdit.setOnAction(e -> handle_btnEdit());

    }

    private void handle_btnEdit() {
        if (mode.contentEquals("view")) {//edit button
            if (tblInventory.getSelectionModel().getSelectedItem() == null) {
                return;
            }

            mode = "edit";
            //enable controls
            btnCancel.setText("Cancel");
            btnEdit.setText("Save");
            btnEdit.setDisable(true);

            txtName.setEditable(true);
            txtColor.setEditable(true);
            txtSize.setEditable(true);
            txtQty.setEditable(true);
            txtPrice.setEditable(true);
            btnImages.setVisible(false);
            btnDelete.setVisible(false);
            tblInventory.setDisable(true);
            comboListOf.setDisable(true);
            btnAll.setDisable(true);

            if (comboSex.getSelectionModel().getSelectedIndex() == -1) {
                comboSex.getSelectionModel().select(0);
                comboCategory.getSelectionModel().select(0);
            }

            //init ids for checking
            allIds = db.getAllProduct(Integer.parseInt(txtId.getText()));


        } else
        {
            //if empty set to 0
            if (txtQty.getText().length() == 0) {
                txtQty.setText("0");
            }

            if (txtPrice.getText().length() == 0) {
                txtPrice.setText("0");
            }

            if (mode.contentEquals("edit")) {//save edit button
                handle_editSave();
            }
            else{// save new button
                handle_newSave();

            }
            //cancel
            handle_btnCancel();
        }

    }

    private void handle_newSave() {
        //create new product
        Product product = new Product(Integer.parseInt(txtId.getText()));

        product.setName(txtName.getText());
        product.setCategory(((Category)comboCategory.getSelectionModel().getSelectedItem()).getId());
        product.setColor(txtColor.getText());
        product.setSize(txtSize.getText());
        product.setGender(comboSex.getSelectionModel().getSelectedIndex());
        product.setQty(Integer.parseInt(txtQty.getText()));
        product.setPrice(Float.parseFloat(txtPrice.getText()));

        product.add(db);

        lanClient.sender.send("update");



        allProducts.add(product);
        refreshTable();
    }

    private void handle_editSave() {
        //get selected item
        Product product = (Product)tblInventory.getSelectionModel().getSelectedItem();

        product.setId(Integer.parseInt(txtId.getText()));
        product.setName(txtName.getText());
        product.setCategory(((Category)comboCategory.getSelectionModel().getSelectedItem()).getId());
        product.setColor(txtColor.getText());
        product.setSize(txtSize.getText());
        product.setGender(comboSex.getSelectionModel().getSelectedIndex());
        product.setQty(Integer.parseInt(txtQty.getText()));
        product.setPrice(Float.parseFloat(txtPrice.getText()));

        product.save(db);

        lanClient.sender.send("update");


        refreshTable();
    }

    private void init_btnCancel() {
        //create button
        btnCancel = new Button("Add New");
        btnCancel.setPrefWidth(240);
        btnCancel.setPrefHeight(30);

        //click
        btnCancel.setOnAction( e -> handle_btnCancel());
    }

    private void handle_btnCancel() {
        //in view mode cancel btn is add button
        if (mode.contentEquals("view")) {//add new button
            mode = "new";
            //enable controls
            btnCancel.setText("Cancel");
            btnEdit.setText("Save");
            btnEdit.setDisable(true);

            txtId.setEditable(true);
            txtName.setEditable(true);
            txtColor.setEditable(true);
            txtSize.setEditable(true);
            txtQty.setEditable(true);
            txtPrice.setEditable(true);
            btnImages.setVisible(false);
            btnDelete.setVisible(false);
            tblInventory.setDisable(true);
            comboListOf.setDisable(true);
            btnAll.setDisable(true);

            //clear data
            txtId.setText("");
            txtName.setText("");
            txtColor.setText("");
            txtSize.setText("");
            txtQty.setText("");
            txtPrice.setText("");

            if (comboSex.getSelectionModel().getSelectedIndex() == -1) {
                comboSex.getSelectionModel().select(0);
                comboCategory.getSelectionModel().select(0);
            }

            //init ids for checking
            allIds = db.getAllProduct();
        }
        else {// else it is cancel button
            mode = "view";
            //disable controls
            btnCancel.setText("Add New");
            btnEdit.setText("Edit");

            txtId.setEditable(false);
            txtName.setEditable(false);
            txtColor.setEditable(false);
            txtSize.setEditable(false);
            txtQty.setEditable(false);
            txtPrice.setEditable(false);
            btnImages.setVisible(true);
            btnDelete.setVisible(true);
            tblInventory.setDisable(false);
            comboListOf.setDisable(false);
            btnAll.setDisable(false);
            btnEdit.setDisable(false);
            lblInfo.setText("");
            //clear
            txtId.setText("");
            txtName.setText("");
            txtColor.setText("");
            txtSize.setText("");
            txtQty.setText("");
            txtPrice.setText("");

            handle_tblInventory();
        }

    }

    private void init_btnImages() {
        //create controls
        btnImages = new Button("Images");
        btnImages.setMaxWidth(240);
        btnImages.setPrefHeight(30);

        //click
        btnImages.setOnAction(e -> handle_btnImages());

    }

    private void handle_btnImages() {
        //
        Product product = (Product)tblInventory.getSelectionModel().getSelectedItem();

        if (product == null) {
            return;
        }

        //load data
        product.setImages(db);

        //create image object
        ImageUpload img = new ImageUpload(db, product);

        //create and show window
        new CreateStage(img.createUI(), "Images", true);
    }

    private void init_combos() {

        comboCategory = new ComboBox();
        comboSex = new ComboBox();
        comboListOf = new ComboBox();

        //fill the space available
        comboCategory.setMaxWidth(999);
        comboSex.setMaxWidth(999);
        //fit
        comboListOf.setPrefWidth(250);



        //init sex combo
        ObservableList sex = FXCollections.observableArrayList();
        sex.add("Boys");
        sex.add("Girls");
        sex.add("Men");
        sex.add("Women");
        sex.add("Unisex");

        comboSex.setItems(sex);


        comboCategory.setOnAction(e ->handle_input());
        comboSex.setOnAction(e ->handle_input());

        //combo list of listener
        comboListOf.getSelectionModel().selectedItemProperty().addListener((option, old, newValue) ->{
            refreshTable();

            //clear text boxes
            txtName.clear();
            txtId.clear();
            txtPrice.clear();
            txtQty.clear();
            txtSize.clear();
            txtColor.clear();
            txtId.clear();
            comboCategory.getSelectionModel().select(-1);
            comboSex.getSelectionModel().select(-1);
        });

    }

    private void init_btnAll() {
        btnAll = new Button("All");
        btnAll.setPrefWidth(100);

        //on click
        btnAll.setOnAction(e -> comboListOf.getSelectionModel().select(-1));
    }

    private void init_tblInventory() {
        tblInventory = new TableView();
        tblInventory.setPrefHeight(450);
        tblInventory.setPrefWidth(460);

        //id
        TableColumn<Product, String> colId = new TableColumn<>("Id");
        colId.setMinWidth(65);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        //Name
        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setMinWidth(150);
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        //size
        TableColumn<Product, String> colSize = new TableColumn<>("Size");
        colSize.setMinWidth(70);
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        //gender
        TableColumn<Product, String> colGender = new TableColumn<>("Gender");
        colGender.setMinWidth(90);
        colGender.setCellValueFactory(new PropertyValueFactory<>("genderName"));

        //qty
        TableColumn<Product, Number> colQty = new TableColumn<>("Qty");
        colQty.setMinWidth(70);
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));


        tblInventory.getColumns().addAll(colId, colName, colSize, colGender, colQty);

        // item changed
        tblInventory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            handle_tblInventory();
        });

    }

    private void handle_tblInventory(){
        try {
            Product product = (Product) tblInventory.getSelectionModel().getSelectedItem();

            txtId.setText(String.valueOf(product.getId()));
            txtName.setText(product.getName());
            comboCategory.getSelectionModel().select(getCategoryObject(product));
            txtColor.setText(product.getColor());
            txtSize.setText(product.getSize());
            comboSex.getSelectionModel().select(product.getGender());
            txtQty.setText(String.valueOf(product.getQty()));
            txtPrice.setText(String.valueOf(product.getPrice()));
        }
        catch (Exception e){}
    }

    private Category getCategoryObject(Product product) {

        for (Object c : comboCategory.getItems()) {
            if (((Category)c).getId() == product.getCategory())
                return (Category)c;
        }

        return null;
    }

    public void updateObject() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadCategory();

                //get list of id
                ArrayList<Number> allId = db.getAllProduct();

                //create observable list for the table
                ObservableList<Product> list = FXCollections.observableArrayList();

                for (Number id : allId) {
                    Product product = new Product(Integer.parseInt(id.toString()));
                    product.load(db);
                    list.add(product);
                }

                allProducts = list;
                allFilteredProducts = new FilteredList<>(allProducts);

                tblInventory.setItems(allFilteredProducts);
                txtTax.setText(db.getTax());
                refreshTable();

                //clear data
                txtId.setText("");
                txtName.setText("");
                txtColor.setText("");
                txtSize.setText("");
                txtQty.setText("");
                txtPrice.setText("");

                if (!mode.equals("view")) {
                    handle_btnCancel();
                }

            }
        });
    }

    private int category;
    private void refreshTable(){
        if (comboListOf.getSelectionModel().getSelectedIndex() == -1)
            category = 0;
        else
            category = ((Category)comboListOf.getSelectionModel().getSelectedItem()).getId();

        allFilteredProducts.setPredicate(prodcut -> categorySelected(prodcut));
    }

    private boolean categorySelected(Product product) {
        if (category == 0 || product.getCategory() == category) {
            return true;
        }

        return false;
    }

    private void init_btnTaxEdit() {
        btnTaxEdit = new Button("Change");
        btnTaxEdit.setPrefWidth(200);

        //handle tax edit
        btnTaxEdit.setOnAction(e -> handle_btnTaxEdit());
    }

    private void handle_btnTaxEdit() {
        if (btnTaxEdit.getText().contentEquals("Change")) {//edit btn

            btnTaxEdit.setText("Save");
            btnTaxCancel.setVisible(true);

            txtTax.setEditable(true);


        } else {//save btn
            if (txtTax.getText().length() == 0) {
                txtTax.setText("0");
            }

            db.setTax(txtTax.getText());

            lanClient.sender.send("update");


            handle_btnTaxCancel();

        }
    }

    private void init_btnTaxCancel() {
        btnTaxCancel = new Button("Cancel");
        btnTaxCancel.setPrefWidth(200);

        //hidden until needed
        btnTaxCancel.setVisible(false);

        //on click
        btnTaxCancel.setOnAction(e -> handle_btnTaxCancel());
    }

    private void handle_btnTaxCancel() {
        btnTaxEdit.setText("Change");
        btnTaxCancel.setVisible(false);

        txtTax.setEditable(false);

        //load tax data
        txtTax.setText(db.getTax());
    }

    private void loadCategory() {
        ObservableList<Category> categories = db.getCategories();

        comboCategory.setItems(categories);
        comboListOf.setItems(categories);
    }
}

