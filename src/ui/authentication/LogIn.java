package ui.authentication;

import data.Database;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import program.ImageClient;
import program.LANClient;
import ui.CreateStage;
import ui.main.*;
import ui.management.AccountManagement;
import ui.management.CategoryManagement;
import ui.management.ProductManagement;
import ui.management.StaffManagement;


public class LogIn{

    SalesSummary sales;
    StaffManagement staff;
    ProductManagement product;
    CategoryManagement category;
    AccountManagement account;
    TransactionSummary transaction;
    Restock restock;
    Retail retail;
    Chat chat;

    private TextField txtUsername;
    private PasswordField txtPassword;
    private Button btnLogin, btnSignup;
    private Label lblInfo;
    private Database db;
    private int accountId;

    public LogIn(Database db){
        this.db = db;

    }

    public LogIn() {
        db = new Database();
    }

    public BorderPane createUI() {
        //initialize inputs
        init_textFields();
        init_btnLogin();
        init_btnSignup();

        return init_interface();

    }

    private BorderPane init_interface() {

        //create center
        Label lblUsername = new Label("Username");
        Label lblPassword = new Label("Password");

        GridPane center = new GridPane();
        center.addColumn(0, lblUsername, lblPassword);
        center.addColumn(1, txtUsername, txtPassword);
        center.add(lblInfo, 1, 2);
        center.add(btnLogin, 1, 3);

        //format grid pane
        center.setVgap(10);
        center.setHgap(20);
        center.setPadding(new Insets(30, 0, 30, 0));

        //create top
        Label top = new Label("WELCOME");
        top.getStyleClass().add("label-large");

        //add elements to border pane
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(center);
        root.setBottom(btnSignup);

        //format border pane
        root.setPadding(new Insets(70, 80, 80, 80));
        BorderPane.setAlignment(btnSignup, Pos.BOTTOM_RIGHT);
        BorderPane.setAlignment(top, Pos.CENTER);

        return root;
    }

    private void init_textFields() {

        //label for information
        lblInfo = new Label();
        lblInfo.getStyleClass().add("label-red");

        //inputs
        txtUsername = new TextField();
        txtPassword = new PasswordField();

        //input verify handler
        txtUsername.setOnKeyTyped(e -> handle_txtUsername());
        txtPassword.setOnKeyTyped(e -> handle_txtPassword());
        txtUsername.setOnKeyReleased(e -> handle_Enter(e));
        txtPassword.setOnKeyReleased(e -> handle_Enter(e));
    }

    private void handle_Enter(KeyEvent e){

        if(e.getCode().toString().equals("ENTER"))
        {
            if (e.getSource() == txtUsername) {
                txtPassword.requestFocus();
            }
            if(!btnLogin.isDisabled())
                handle_btnLogin();
        }
    }

    private void handle_txtUsername(){
        String text = txtUsername.getText();

        if (text.length() < 5 ) {
            lblInfo.setText("Username is too short.");
            btnLogin.setDisable(true);
            return;
        }

        handle_txtPassword();
    }

    private void handle_txtPassword(){
        String text = txtPassword.getText();

        if (text.length() < 8) {
            lblInfo.setText("Password is too short.");
            btnLogin.setDisable(true);
            return;
        }

        if(!text.matches(".*\\d.*")) {
            lblInfo.setText("Password must have at least 1 Number.");
            btnLogin.setDisable(true);
            return;
        }

        if(!text.matches(".*[a-z|A-Z].*")) {
            lblInfo.setText("Password must have at least 1 Letter.");
            btnLogin.setDisable(true);
            return;
        }

        //check for username
        text = txtUsername.getText();

        if (text.length() < 5) {
            lblInfo.setText("Username is too short.");
            btnLogin.setDisable(true);
            return;
        }

        lblInfo.setText("");
        btnLogin.setDisable(false);

    }

    private void init_btnSignup(){
        btnSignup = new Button("Sign Up");
        btnSignup.setPrefSize(60, 30);

        //click handler
        btnSignup.setOnAction(e -> handle_btnSignup());
    }

    private void handle_btnSignup(){
        SignUp signUp = new SignUp(db);
        CreateStage stagePrimary = new CreateStage(signUp.createUI(), "Sign Up");

        stagePrimary.stage.setOnCloseRequest(e -> {
            db.close();
            ImageClient.close();
        });

        Stage stage = (Stage)btnSignup.getScene().getWindow();
        stage.close();
    }

    private void init_btnLogin() {
        btnLogin = new Button("Log In");
        btnLogin.setPrefWidth(250);
        btnLogin.setPrefHeight(40);

        //disable button until verified
        //btnLogin.setDisable(true);

        txtUsername.setText("natty_nigatu");
        txtPassword.setText("natty1234");

        //click handler
        btnLogin.setOnAction(e -> handle_btnLogin());
    }

    private void handle_btnLogin() {
        //authenticate
        int result = db.login(txtUsername.getText(), txtPassword.getText());

        //create connection object
        LANClient lan;

        //get current stage
        Stage stage = (Stage)btnSignup.getScene().getWindow();

        switch (result){

            case 0:
                lblInfo.setText("Wrong Username or Password.");
                return;

            case 1:
                accountId = db.getId(txtUsername.getText());
                new CreateStage(admin(), "Fua Clothing");
                lan = new LANClient(sales, staff, product, category, account, transaction, restock, retail, chat);
                lan.start();
                sales.lanClient = lan;
                staff.lanClient = lan;
                product.lanClient = lan;
                category.lanClient = lan;
                transaction.lanClient = lan;
                restock.lanClient = lan;
                retail.lanClient = lan;
                account.lanClient = lan;
                chat.lanClient = lan;

                stage.close();
                break;

            case 2:
                accountId = db.getId(txtUsername.getText());
                new CreateStage(retail(), "Fua Clothing");
                lan = new LANClient(retail, account, chat);
                lan.start();

                retail.lanClient = lan;
                account.lanClient = lan;
                chat.lanClient = lan;

                stage.close();
                break;

            case 3:
                accountId = db.getId(txtUsername.getText());
                new CreateStage(restock(), "Fua Clothing");
                lan = new LANClient(restock, account, chat);
                lan.start();
                stage.close();

                restock.lanClient = lan;
                account.lanClient = lan;
                chat.lanClient = lan;

                break;

            default:
                lblInfo.setText("Connection problem occurred.");
                return;
        }


    }

    private Node retail(){
        //create interface objects
        retail = new Retail(db, this.accountId);
        account = new AccountManagement(db, this.accountId);
        chat = new Chat(this.accountId, db);

        //create tabs
        Tab retailTab = new Tab("Retail");
        retailTab.setContent(center(retail.createUI()));
        retailTab.setClosable(false);

        Tab accountTab = new Tab("Account");
        accountTab.setContent(center(account.createUI()));
        accountTab.setClosable(false);

        Tab chatTab = new Tab("Chat");
        chatTab.setContent(chat.createUI());
        chatTab.setClosable(false);

        TabPane tabPane = new TabPane(retailTab, chatTab, accountTab);

        tabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        if(accountTab == t1)
                            account.updateObject();
                        else if(retailTab == t1)
                            retail.updateObject();
                    }
                }
        );

        //add tabs to pane and return

        return tabPane;
    }

    private Node restock(){
        //create interface objects
        restock = new Restock(db, this.accountId);
        account = new AccountManagement(db, this.accountId);
        chat = new Chat(this.accountId, db);

        //createTabs
        Tab restockTab = new Tab("Restock");
        restockTab.setContent(center(restock.createUI()));
        restockTab.setClosable(false);

        Tab accountTab = new Tab("Account");
        accountTab.setContent(center(account.createUI()));
        accountTab.setClosable(false);

        Tab chatTab = new Tab("Chat");
        chatTab.setContent(chat.createUI());
        chatTab.setClosable(false);

        TabPane tabPane = new TabPane(restockTab, chatTab, accountTab);

        tabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        if(accountTab == t1)
                            account.updateObject();
                        else if(restockTab == t1)
                            restock.updateObject();
                    }
                }
        );

        //add tabs to pane and return
        return tabPane;

    }

    private Node admin(){
        //create interface objects
        sales = new SalesSummary(db);
        staff = new StaffManagement(db, this.accountId);
        product = new ProductManagement(db, this.accountId);
        category = new CategoryManagement(db);
        account = new AccountManagement(db, this.accountId);
        transaction = new TransactionSummary(db);
        restock = new Restock(db, this.accountId);
        retail = new Retail(db, this.accountId);
        chat = new Chat(this.accountId, db);

        //create Tabs
        Tab salesTab = new Tab("Sales");
        salesTab.setContent(center(sales.createUI()));
        salesTab.setClosable(false);

        Tab staffTab = new Tab("Staff");
        staffTab.setContent(center(staff.createUI()));
        staffTab.setClosable(false);

        Tab categoryTab = new Tab("Category");
        categoryTab.setContent(center(category.createUI()));
        categoryTab.setClosable(false);

        Tab accountTab = new Tab("Account");
        accountTab.setContent(center(account.createUI()));
        accountTab.setClosable(false);

        Tab chatTab = new Tab("Chat");
        chatTab.setContent(chat.createUI());
        chatTab.setClosable(false);

        //product Tab
        Tab adminTab = new Tab("Admin");
        adminTab.setContent(center(product.createUI()));
        adminTab.setClosable(false);

        Tab retailTab = new Tab("Retail");
        retailTab.setContent(center(retail.createUI()));
        retailTab.setClosable(false);

        Tab restockTab = new Tab("Restock");
        restockTab.setContent(center(restock.createUI()));
        restockTab.setClosable(false);

        Tab transactionTab = new Tab("Transaction");
        transactionTab.setContent(center(transaction.createUI()));
        transactionTab.setClosable(false);


        TabPane productChild = new TabPane(transactionTab, adminTab, retailTab, restockTab);
        productChild.setId("product-tab");

        //listener inner tab
        productChild.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        if(adminTab == t1)
                            product.updateObject();
                        else if(retailTab == t1)
                            retail.updateObject();
                        else if (restockTab == t1)
                            restock.updateObject();
                        else if (transactionTab == t1)
                            transaction.updateObject();
                    }
                }
        );

        Tab productTab = new Tab("Product");
        productTab.setContent(productChild);
        productTab.setClosable(false);

        //add tabs to pane
        TabPane mainTab = new TabPane(salesTab, staffTab, productTab, categoryTab, chatTab, accountTab);

        //listener main tab
        mainTab.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {

                        if(productTab == t1) {

                            //check which child tab is visible
                            switch (productChild.getSelectionModel().getSelectedIndex()) {

                                case 0:
                                    transaction.updateObject();
                                    break;

                                case 1:
                                    product.updateObject();
                                    break;

                                case 2:
                                    retail.updateObject();
                                    break;

                                case 3:
                                    restock.updateObject();
                                    break;
                            }

                        }
                        else if(salesTab == t1)
                            sales.updateObject();

                        else if(staffTab == t1)
                            staff.updateObject();

                        else if(categoryTab == t1)
                            category.updateObject();

                        else if(accountTab == t1)
                            account.updateObject();
                    }
                }
        );

        return mainTab;


    }

    private Node center(Node node){

        GridPane root = new GridPane();
        root.add(node, 0, 0);
        root.setMaxSize(9999, 9999);
        root.setAlignment(Pos.CENTER);

        return root;
    }

}
