package ui.main;

import data.*;
import javafx.application.Platform;
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

import java.time.ZoneId;
import java.util.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;


public class TransactionSummary {

    public LANClient lanClient;
    private Button btnAllStaff, btnAllItem, btnAllDate, btnClear, btnRefresh;
    private DatePicker dateFrom, dateTo;
    private ComboBox comboPosition, comboPerson, comboCategory, comboProduct;
    private TableView tblTransaction;
    private Database db;
    private ObservableList<Transaction> transactions;
    private ObservableList<Category> categories;
    private ObservableList<Product> products;
    private FilteredList<Product> filteredProducts;
    private ObservableList<Staff> staff;
    private FilteredList<Staff> filteredStaff;
    private FilteredList<Transaction> filteredTransactions;

    public TransactionSummary(Database db) {
        this.db = db;
    }

    public VBox createUI() {

        //init controls
        init_btnAllDate();
        init_btnAllItem();
        init_btnAllStaff();
        init_btnClear();
        init_btnRefresh();
        init_combos();
        init_Dates();
        init_tblTransaction();
        updateObject();


        //create labels
        Label lblStaff = new Label("Staff");
        lblStaff.getStyleClass().add("label-large");
        lblStaff.setMaxWidth(999);
        lblStaff.setAlignment(Pos.CENTER);

        Label lblItem = new Label("Item");
        lblItem.getStyleClass().add("label-large");
        lblItem.setMaxWidth(999);
        lblItem.setAlignment(Pos.CENTER);

        Label lblDate = new Label("Date");
        lblDate.getStyleClass().add("label-large");
        lblDate.setMaxWidth(999);
        lblDate.setAlignment(Pos.CENTER);

        Label lblPosition = new Label("Position");
        Label lblEmployee = new Label("Employee");
        Label lblCategory = new Label("Category");
        Label lblProduct = new Label("Product");
        Label lblFrom = new Label("From");
        Label lblTo = new Label("To");


        //create grid, add elements
        GridPane grid = new GridPane();
        grid.addColumn(0, lblStaff, lblPosition, lblEmployee, btnAllStaff);
        grid.addColumn(1, new Label(), comboPosition, comboPerson);
        grid.addColumn(2, lblItem, lblCategory, lblProduct, btnAllItem);
        grid.addColumn(3, new Label(), comboCategory, comboProduct);
        grid.addColumn(4, lblDate, lblFrom, lblTo, btnAllDate);
        grid.addColumn(5, new Label(), dateFrom, dateTo);

        //format
        GridPane.setColumnSpan(lblStaff, 2);
        GridPane.setColumnSpan(lblItem, 2);
        GridPane.setColumnSpan(lblDate, 2);
        GridPane.setColumnSpan(btnAllStaff, 2);
        GridPane.setColumnSpan(btnAllItem, 2);
        GridPane.setColumnSpan(btnAllDate, 2);

        grid.setHgap(15);
        grid.setVgap(15);

        HBox bottom = new HBox(btnRefresh, btnClear);
        bottom.setSpacing(40);

        //create root
        VBox root = new VBox(grid, tblTransaction, bottom);

        //format

        root.setSpacing(15);
        root.setPadding(new Insets(20, 30, 30, 30));


        return root;
    }

    private void init_tblTransaction() {
        tblTransaction = new TableView();
        tblTransaction.setPrefWidth(600);
        tblTransaction.setPrefHeight(350);

        //Type
        TableColumn<Transaction, String> colType = new TableColumn<>("Type");
        colType.setMinWidth(50);
        colType.setCellValueFactory(new PropertyValueFactory<>("typeName"));

        //Staff
        TableColumn<Product, String> colStaff = new TableColumn<>("Staff");
        colStaff.setMinWidth(90);
        colStaff.setCellValueFactory(new PropertyValueFactory<>("staff"));

        //Product
        TableColumn<Product, String> colProduct = new TableColumn<>("Product");
        colProduct.setMinWidth(130);
        colProduct.setCellValueFactory(new PropertyValueFactory<>("product"));

        //Qty
        TableColumn<Product, Number> colQty = new TableColumn<>("Qty");
        colQty.setMinWidth(40);
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        //Price
        TableColumn<Product, Number> colPrice = new TableColumn<>("Price");
        colPrice.setMinWidth(90);
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        //Tax
        TableColumn<Product, Number> colTax = new TableColumn<>("Tax");
        colTax.setMinWidth(40);
        colTax.setCellValueFactory(new PropertyValueFactory<>("tax"));

        //Total
        TableColumn<Product, Number> colTotal = new TableColumn<>("Total");
        colTotal.setMinWidth(75);
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        //Date
        TableColumn<Product, Date> colDate = new TableColumn<>("Date");
        colDate.setMinWidth(80);
        colDate.setMaxWidth(80);
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        //Time
        TableColumn<Product, Time> colTime = new TableColumn<>("Time");
        colTime.setMinWidth(75);
        colTime.setMaxWidth(75);
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        tblTransaction.getColumns().addAll(colType, colStaff, colProduct, colQty, colPrice, colTax, colTotal, colDate, colTime);


    }

    public void updateObject() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                transactions = db.getTransaction();
                categories = db.getCategories();
                products = FXCollections.observableArrayList();
                staff = db.getStaff();

                ArrayList<Number> idList = new ArrayList<>();
                idList = db.getAllProduct();

                for (Number x : idList) {
                    Product p = new Product(x.intValue());
                    p.load(db);
                    products.add(p);
                }

                //categories
                comboCategory.setItems(categories);

                //Products
                filteredProducts = new FilteredList<>(products);
                comboProduct.setItems(filteredProducts);

                //staff
                filteredStaff = new FilteredList<>(staff);
                comboPerson.setItems(filteredStaff);

                //transaction
                filteredTransactions = new FilteredList<>(transactions);
                tblTransaction.setItems(filteredTransactions);
                refreshTable();
            }
        });


    }

    private void init_btnRefresh() {
        btnRefresh = new Button("Refresh");
        btnRefresh.setPrefWidth(510);

        //on click
        btnRefresh.setOnAction(e -> updateObject());
    }

    private void init_combos() {
        comboPosition = new ComboBox();
        comboPerson = new ComboBox();
        comboCategory = new ComboBox();
        comboProduct = new ComboBox();

        comboPosition.setPrefWidth(155);
        comboPerson.setPrefWidth(155);
        comboCategory.setPrefWidth(155);
        comboProduct.setPrefWidth(155);

        //set combo position
        comboPosition.getItems().add("All");
        comboPosition.getItems().add("Administrator");
        comboPosition.getItems().add("Retail");
        comboPosition.getItems().add("Restock");
        comboPosition.getSelectionModel().select(0);

        //category listener
        comboCategory.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->{
            filteredProducts.setPredicate(product -> checkCategorySelected(product));
            if(oldValue != newValue)
                refreshTable();
        });

        //position Listener
        comboPosition.getSelectionModel().selectedItemProperty().addListener((options, old, newValue) -> {
            filteredStaff.setPredicate(staff -> checkPositionSelected(staff));
            comboPerson.getSelectionModel().select(-1);
            if(old != newValue)
                refreshTable();
        });

        //person listener
        comboPerson.getSelectionModel().selectedItemProperty().addListener((options, old, newValue) -> {
            if(old != newValue)
                refreshTable();
        });

        //product listener
        comboProduct.getSelectionModel().selectedItemProperty().addListener((options, old, newValue) -> {
            if(old != newValue)
                refreshTable();
        });

    }

    private boolean checkPositionSelected(Staff staff) {
        if(comboPosition.getSelectionModel().getSelectedIndex() == -1 ||
                comboPosition.getSelectionModel().getSelectedIndex() == 0)
            return true;

        if (comboPosition.getSelectionModel().getSelectedIndex() == staff.getType() )
            return true;
        else
            return false;
    }

    private boolean checkCategorySelected(Product product) {
        if (comboCategory.getSelectionModel().getSelectedIndex() == -1) {
            return true;
        }

        int category = ((Category)comboCategory.getSelectionModel().getSelectedItem()).getId();

        if (product.getCategory() == category) {
            return true;
        }

        return false;
    }
    private void init_btnAllStaff() {
        btnAllStaff = new Button("All Staff");
        btnAllStaff.setPrefWidth(220);

        //on click
        btnAllStaff.setOnAction(e -> handle_btnStaff());
    }

    private void handle_btnStaff() {
        comboPosition.getSelectionModel().select(0);
        filteredStaff.setPredicate(staff -> checkPositionSelected(staff));

        refreshTable();
    }

    private void init_btnAllItem() {
        btnAllItem = new Button("All Items");
        btnAllItem.setPrefWidth(220);

        //on click
        btnAllItem.setOnAction(e -> handle_btnAllItem());
    }

    private void handle_btnAllItem() {
        comboCategory.getSelectionModel().select(-1);
        filteredProducts.setPredicate(product -> checkCategorySelected(product));

        refreshTable();
    }

    private void init_btnAllDate() {
        btnAllDate = new Button("All Dates");
        btnAllDate.setPrefWidth(225);

        //on click
        btnAllDate.setOnAction(e -> handle_btnAllDate());
    }
    private void handle_btnAllDate() {
        //set values of dates the maximum range
        LocalDate minDate = LocalDate.of(2021, 1, 1);
        LocalDate maxDate = LocalDate.now();

        dateFrom.setValue(minDate);
        dateTo.setValue(maxDate);

        refreshTable();
    }


    private void init_btnClear() {
        btnClear = new Button("Clear Records");
        btnClear.setPrefWidth(150);

        //click
        btnClear.setOnAction(e -> handle_btnClear());
    }

    private void handle_btnClear() {

        String header = "Clear Records?";
        String title = "Clear Transaction Records?";
        String desc = "Are you sure? This Action is Irreversible and unrecommended." ;

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
            db.clearTransactions();
            updateObject();

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

    private void init_Dates() {

        dateFrom = new DatePicker();
        dateFrom.setEditable(false);
        dateTo = new DatePicker();
        dateTo.setEditable(false);

        LocalDate minDate = LocalDate.of(2021, 1, 1);
        LocalDate maxDate = LocalDate.now();

        //set dates
        dateFrom.setValue(maxDate);
        dateTo.setValue(maxDate);

        //disable not possible dates
        dateFrom.setDayCellFactory(d ->
                new DateCell() {
                    @Override public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(item.isBefore(minDate) || item.isAfter(maxDate));
                    }});

        dateTo.setDayCellFactory(d ->
                new DateCell() {
                    @Override public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(!item.isEqual(maxDate));
                    }});

        //set event handlers
        dateTo.setOnAction(e -> handle_dateTo());
        dateFrom.setOnAction(e -> handle_dateFrom());
    }

    private void handle_dateTo() {

        //disable dates on date from that are after date to
        LocalDate minDate = LocalDate.of(2021, 1, 1);
        LocalDate maxDate = dateTo.getValue();

        dateFrom.setDayCellFactory(d ->
                new DateCell() {
                    @Override public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(item.isAfter(maxDate) || item.isBefore(minDate));
                    }});

        //correct if it is already after
        if(dateFrom.getValue().isAfter(dateTo.getValue()))
            dateFrom.setValue(dateTo.getValue());

        refreshTable();
    }

    private void handle_dateFrom() {

        //disable dates on date to that are before date from
        LocalDate minDate = dateFrom.getValue();
        LocalDate maxDate = LocalDate.now();

        dateTo.setDayCellFactory(d ->
                new DateCell() {
                    @Override public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(item.isAfter(maxDate) || item.isBefore(minDate));
                    }});

        //correct if it is already before
        if(dateTo.getValue().isBefore(dateFrom.getValue()))
            dateTo.setValue(dateFrom.getValue());

        refreshTable();
    }

    private Date from;
    private Date to;
    private int category;
    private int product;
    private int position;
    private int person;

    private void refreshTable() {
        from = Date.from(dateFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        to = Date.from(dateTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());


        if(comboCategory.getSelectionModel().getSelectedIndex() == -1)
            category = 0;
        else
            category = ((Category)comboCategory.getSelectionModel().getSelectedItem()).getId();


        if(comboProduct.getSelectionModel().getSelectedIndex() == -1)
            product = 0;
        else
            product =((Product)comboProduct.getSelectionModel().getSelectedItem()).getId();


        if(comboPosition.getSelectionModel().getSelectedIndex() == -1 ||
                comboPosition.getSelectionModel().getSelectedIndex() == 0)
            position = 0;
        else
            position = comboPosition.getSelectionModel().getSelectedIndex();

        if (comboPerson.getSelectionModel().getSelectedIndex() == -1)
            person = 0;
        else
            person = ((Staff)comboPerson.getSelectionModel().getSelectedItem()).getId();


        filteredTransactions.setPredicate(transaction -> checkForTable(transaction));
    }

    private boolean checkForTable(Transaction transaction) {


        if (from.compareTo(transaction.getDateObject()) <= 0  && to.compareTo(transaction.getDateObject()) >=0 &&
                (category == 0 || transaction.getCategory() == category) &&
                (product == 0 || transaction.getProductId() == product) &&
                (position == 0 || transaction.getPosition() == position) &&
                (person == 0 || transaction.getStaffId() == person)) {
            return true;
        }
        return false;
    }
}
