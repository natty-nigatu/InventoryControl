package ui.main;

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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import program.LANClient;
import ui.CreateStage;
import ui.image.ImageDisplay;
import ui.image.ImageUpload;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.Predicate;


public class Retail {
    public LANClient lanClient;
    //Create ui controls that will be accessed by the class
    private Button btnSearch, btnAdd, btnRemove, btnCheckout, btnImages;
    private TextField txtId, txtQty, txtName, txtPrice,  txtInStock, txtItems, txtSubTotal, txtTax, txtTotal;
    private Label lblInfo;
    private TableView tblCart;
    private Database db;
    private Product product;
    private int account;
    private FilteredList<Product> allproductsList;
    private ObservableList<Product> allProducts;
    private int tax;
    private Label lblTax;

    public Retail(Database db, int account) {
        this.db = db;
        this.account = account;
    }

    public HBox createUI() {

        //create controls
        init_btnAdd();
        init_btnRemove();
        init_btnCheckout();
        init_txtFields();
        init_btnSearch();
        init_btnImages();
        init_productList();
        init_tblCart();

        // init tax
        tax = Integer.parseInt(db.getTax());
        lblTax = new Label("Tax ("+ String.valueOf(tax) + "%)");


       return init_interface();
    }

    private HBox init_interface() {
        //Left side
        Label lblCart = new Label("Cart");
        lblCart.getStyleClass().add("label-large");
        lblCart.setMaxWidth(999);
        lblCart.setAlignment(Pos.CENTER);

        VBox left = new VBox(lblCart, tblCart, btnRemove);
        left.setSpacing(15);

        //Right side
        //Create Labels
        Label lblId = new Label("Item Id");
        Label lblName = new Label("Item Name");
        Label lblInStock = new Label("In Stock");
        Label lblQty = new Label("Qty");
        Label lblPrice = new Label("Price");
        Label lblItems = new Label("No. of Items");
        Label lblSubTotal = new Label("Subtotal");
        Label lblTotal = new Label("Total");

        //create grid for left side and add elements
        GridPane right = new GridPane();

        //Line Top Separator
        right.add(new Separator(), 0, 0, 2, 1);

        //Line Item
        HBox item = new HBox(txtId, btnSearch);
        item.setSpacing(10);
        right.addRow(1, lblId, item);

        //Line Second Separator
        right.add(new Separator(), 0, 2, 2, 1);

        //Line Info label
        right.add(lblInfo, 0, 3, 2, 1);

        //line Name
        right.addRow(4, lblName, txtName);

        //line Price
        right.addRow(5, lblPrice, txtPrice);

        //line Category
        right.addRow(6, lblInStock, txtInStock);

        //line qty
        right.addRow(7, lblQty, txtQty);

        //line add
        right.addRow(8, btnImages, btnAdd);

        //line third separator
        right.add(new Separator(), 0, 9, 2, 1);
        right.add(new Separator(), 0, 10, 2, 1);

        //line Items
        right.addRow(11, lblItems, txtItems);

        //line Subtotal
        right.addRow(12, lblSubTotal, txtSubTotal);

        //line vat
        right.addRow(13, lblTax, txtTax);

        //line total
        right.addRow(14, lblTotal, txtTotal);

        //last separator
        right.add(new Separator(), 0, 15, 2, 1);

        // line checkout
        right.add(btnCheckout, 0, 16, 2, 1);

        //format grid spacing
        right.setVgap(13);
        right.setHgap(10);
        right.setAlignment(Pos.CENTER);

        //create and return main container
        HBox root = new HBox(left, right);
        root.setSpacing(20);
        root.setPadding(new Insets(10, 30, 20, 30));

        return root;
    }

    private void init_tblCart() {
        tblCart = new TableView();
        tblCart.setPrefWidth(310);
        tblCart.setPrefHeight(470);

        //Id
        TableColumn<Product, String> colId = new TableColumn<>("Id");
        colId.setMinWidth(45);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        //Name
        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setMinWidth(145);
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        //Qty
        TableColumn<Product, String> colInCart = new TableColumn<>("In Cart");
        colInCart.setMinWidth(35);
        colInCart.setCellValueFactory(new PropertyValueFactory<>("inCart"));

        tblCart.getColumns().addAll(colId, colName, colInCart);

        tblCart.setItems(allproductsList);

        // item changed
        tblCart.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            try {
                product = (Product) tblCart.getSelectionModel().getSelectedItem();
                loadProduct();
            } catch (Exception e){}
        });

    }

    private void init_productList() {
        //get all id
        ArrayList<Number> allIds = db.getAllProduct();
        //create main list
        allProducts = FXCollections.observableArrayList();
        //add elements to main list
        for (Number x : allIds) {
            Product product = new Product(x.intValue());
            product.load(db);

            allProducts.add(product);
        }
        //add main list to filtered list
        allproductsList = new FilteredList<>(FXCollections.observableArrayList(allProducts));
        allproductsList.setPredicate(createPredicate());

    }

    private void init_btnAdd() {
        btnAdd = new Button("Add");
        btnAdd.setMaxWidth(500);
        btnAdd.setPrefHeight(30);

        //on click
        btnAdd.setOnAction(e -> handle_btnAdd());
    }

    private void handle_btnAdd() {

        if (product == null) {
            return;
        }

        if (txtQty.getText().length() == 0) {
            return;
        }

        //set value
        product.setInCart(Integer.parseInt(txtQty.getText()));

        //update list
        allproductsList.setPredicate(createPredicate());
        calculate();

        //clear records and focus back to id
        product = null;
        txtId.clear();
        txtQty.clear();
        txtName.clear();
        txtPrice.clear();
        txtInStock.clear();
        lblInfo.setText("");
        txtId.requestFocus();
    }


    private void init_btnCheckout() {

        btnCheckout = new Button("Checkout");
        btnCheckout.setMaxWidth(400);
        btnCheckout.setPrefHeight(40);

        //on click
        btnCheckout.setOnAction(e -> handle_btnCheckout());
    }

    private void handle_btnCheckout() {
        boolean sellError = false;
        //if nothing is added
        if (allproductsList.size() == 0) {
            return;
        }

        ArrayList<Product> sold = new ArrayList<>();

        for (Product p : allproductsList) {
            //check if item has qty
            p.load(db);
            if (p.getQty() >= p.getInCart()) {
                sold.add(p.clone());
                p.sell(db, account, tax);

                lanClient.sender.send("update");


            } else {
                sellError = true;
            }
        }

        //if qty changed
        if (sellError) {
            new Message("Qty Error", "Some Items has NOT been sold.",
                    "Some Items has been sold by another retailer and now are not available. " +
                            "\nPlease update products in cart");
        }

        //generate receipt
        if (sold.size() > 0)
            new Receipt(sold, tax);

        allproductsList.setPredicate(createPredicate());
        calculate();


    }

    private void init_btnRemove() {

        btnRemove = new Button("Remove");
        btnRemove.setPrefWidth(310);

        //on click
        btnRemove.setOnAction(e -> handle_btnRemove());
    }

    private void handle_btnRemove() {
        if (tblCart.getSelectionModel().getSelectedItem() == null) {
            new Message("No selection.", "No Product Selected",
                    "Please select a product in cart and try again.");
            return;
        }

        //set cart to zero and update
        product.setInCart(0);
        allproductsList.setPredicate(createPredicate());
        calculate();

        //clear records and focus back to id
        product = null;
        txtId.clear();
        txtQty.clear();
        txtName.clear();
        txtPrice.clear();
        txtInStock.clear();
        lblInfo.setText("");
        txtId.requestFocus();

    }

    private void init_btnSearch() {

        btnSearch = new Button("Search");
        btnSearch.setPrefHeight(30);

        // on click
        btnSearch.setOnAction(e -> handle_btnSearch());
    }

    private void handle_btnSearch() {
        if (txtId.getText().length() == 0) {
            lblInfo.setText("Id is empty.");
            txtId.requestFocus();
            return;
        }

        product = search(Integer.parseInt(txtId.getText()));

        if (product == null) {
            lblInfo.setText("No product found.");
            //clear records and focus back to id
            product = null;
            txtId.selectAll();
            txtQty.clear();
            txtName.clear();
            txtPrice.clear();
            txtInStock.clear();
            txtId.requestFocus();
            return;
        }

        if (product.getQty() == 0) {
            lblInfo.setText("Product is out of stock.");
            //clear records and focus back to id
            product = null;
            txtId.selectAll();
            txtQty.clear();
            txtName.clear();
            txtPrice.clear();
            txtInStock.clear();
            txtId.requestFocus();
            return;
        }

        if (product.getPrice() == 0) {
            lblInfo.setText("Product is not for sale.");
            //clear records and focus back to id
            product = null;
            txtId.selectAll();
            txtQty.clear();
            txtName.clear();
            txtPrice.clear();
            txtInStock.clear();
            txtId.requestFocus();
            return;
        }

        //if product exists
        txtQty.requestFocus();
        loadProduct();

    }

    private void loadProduct(){
        txtId.setText(String.valueOf(product.getId()));
        txtName.setText(product.getName());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtInStock.setText(String.valueOf(product.getQty()));

        txtQty.setText("");

        for (Product p: allproductsList)
            if(p == product)
                txtQty.setText(String.valueOf(product.getInCart()));

    }

    private void init_btnImages() {

        btnImages = new Button("Images");
        btnImages.setPrefHeight(30);

        //click
        btnImages.setOnAction(e -> handle_btnImages());
    }

    private void handle_btnImages() {
        if (product == null) {
            return;
        }

        product.setImages(db);
        ImageDisplay image = new ImageDisplay(product);

        new CreateStage(image.createUI(), "Images", true);
    }

    private void init_txtFields() {

        //information label
        lblInfo = new Label();
        lblInfo.getStyleClass().add("label-red");
        lblInfo.setMaxWidth(999);
        lblInfo.setAlignment(Pos.CENTER);

        txtId = new TextField();
        txtId.setPrefHeight(30);
        txtQty = new TextField();
        txtName = new TextField();
        txtPrice = new TextField();
        txtInStock = new TextField();
        txtItems = new TextField();
        txtSubTotal = new TextField();
        txtTax = new TextField();
        txtTotal = new TextField();

        //some fields cannot be edited
        txtName.setEditable(false);
        txtPrice.setEditable(false);
        txtInStock.setEditable(false);
        txtItems.setEditable(false);
        txtSubTotal.setEditable(false);
        txtTax.setEditable(false);
        txtTotal.setEditable(false);

        //set on action
        txtId.setOnKeyReleased(e -> handle_enter(e));
        txtQty.setOnKeyReleased(e -> handle_enter(e));


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
                    if (!t1.matches("\\d*") || t1.length() > 9)
                        txtQty.setText(s);

                    if (t1.length() > 0) {
                        if((Integer.parseInt(t1) > Integer.parseInt(txtInStock.getText())))
                            txtQty.setText(txtInStock.getText());
                    }
                }
            });
        }
        catch (Exception ex){}

    }

    private void calculate() {

        DecimalFormat df = new DecimalFormat("0.00");

        //calculate values
        double sub = 0, total = 0, tax = 0;
        int items = 0;

        for (Product p : allproductsList) {
            items += p.getInCart();
            sub += p.getPrice() * p.getInCart();
        }

        tax = sub * this.tax * 0.01;
        total = sub + tax;

        //show values
        txtItems.setText(String.valueOf(items));
        txtSubTotal.setText(df.format(sub));
        txtTax.setText(df.format(tax));
        txtTotal.setText(df.format(total));


    }

    private void handle_enter(KeyEvent e) {

        if(e.getCode().toString().equals("ENTER"))
        {
            if (e.getSource() == txtId) {
                handle_btnSearch();
            }
            if(e.getSource() == txtQty)
                handle_btnAdd();
        }
    }

    private boolean checkInCart(Product product) {
        if (product.getInCart() > 0) {
            return true;
        }
        else
            return false;
    }

    private Predicate<Product> createPredicate() {
        return product -> {
            return checkInCart(product);
        };
    }

    private Product search(int id) {
        for (Product p : allProducts) {
            if(p.getId() == id)
                return p;
        }

        return null;
    }

    public void updateObject() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //get all id
                ArrayList<Number> allIds = db.getAllProduct();
                ArrayList<Product> deleted = new ArrayList<>();

                int sold = 0;

                for (Product p : allProducts) {

                    //remove from all ids
                    if (allIds.contains(p.getId())) {
                        allIds.remove((Number) p.getId());
                    } else {
                        deleted.add(p);
                        continue;
                    }

                    p.load(db);

                    if (p.getInCart() > 0 && p.getInCart() > p.getQty()) {
                        p.setInCart(p.getQty());
                        sold += 1;
                    }
                }



                //if there is any id left it is a new id so add elements to main list
                for (Number x : allIds) {
                    Product product = new Product(x.intValue());
                    product.load(db);

                    allProducts.add(product);
                }


                //deleted products
                int size = 0;
                if ( deleted.size() != 0) {
                    for (Product p : deleted) {
                        allProducts.remove(p);

                        //if product was in cart
                        if (p.getInCart() > 0) {
                            size += 1;
                            p.setInCart(0);
                        }
                    }

                    new Message("Notice", String.valueOf(size) + "Product(s) Deleted",
                            String.valueOf(size) + " product(s) were deleted by an admin." +
                                    "\n\n The items in cart has been adjusted accordingly.", true);


                }


                tax = Integer.parseInt(db.getTax());
                lblTax.setText("Tax ("+ String.valueOf(tax) + "%)");

                allproductsList = new FilteredList<>(FXCollections.observableArrayList(allProducts));
                tblCart.setItems(allproductsList);
                allproductsList.setPredicate(createPredicate());
                calculate();

                if(sold > 0)
                    new Message("Notice", String.valueOf(sold) + "Product(s) Sold",
                            String.valueOf(sold) + " product(s) were sold by another retailer." +
                                    "\n\n The items in cart has been adjusted accordingly.", true);

                //clear records and focus back to id
                product = null;
                txtId.clear();
                txtQty.clear();
                txtName.clear();
                txtPrice.clear();
                txtInStock.clear();
                lblInfo.setText("");
                txtId.requestFocus();
            }
        });

    }

}
