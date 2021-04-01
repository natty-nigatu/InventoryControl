package ui.main;

import data.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import program.LANClient;
import ui.CreateStage;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SalesSummary {
    public LANClient lanClient;
    private Button btnAllDate, btnAllProduct, btnAllStaff;
    private TextField txtQty, txtSales, txtVat;
    private ComboBox comboProduct, comboStaff;
    private DatePicker dateFrom, dateTo;
    private PieChart pieByProduct, pieByStaff;
    private Label lblInfo;
    private Database db;
    private ObservableList<Sales> salesProduct, salesStaff;
    private FilteredList<Sales> filteredProduct, filteredStaff;

    public SalesSummary(Database db) {
        this.db = db;
    }

    public HBox createUI(){

        //create controls
        init_btnAllDate();
        init_btnAllProducts();
        init_btnAllStaff();
        init_txtFields();
        init_comboProduct();
        init_comboStaff();
        init_Dates();

        init_pieByProduct();
        init_pieByStaff();

        updateObject();

        return init_interface();

    }

    private HBox init_interface() {
        //left
        //labels
        Label lblSort = new Label("Results From");
        lblSort.getStyleClass().add("label-large");
        lblSort.setMaxWidth(999);
        lblSort.setAlignment(Pos.CENTER);

        Label lblFrom = new Label("From");
        Label lblTo = new Label("To");
        Label lblProduct = new Label("Product");
        lblProduct.setMinWidth(45);
        Label lblStaff = new Label("Staff");
        Label lblQty = new Label("Items");
        Label lblTotal = new Label("Sales");
        Label lblVat = new Label("Vat");

        //add top elements to left grid
        GridPane left = new GridPane();
        left.add(lblSort, 0, 0, 2, 1);
        left.addRow(1, lblFrom, dateFrom);
        left.addRow(2, lblTo, dateTo);
        left.add(btnAllDate, 0, 3, 2, 1);
        left.addRow(4, lblProduct, comboProduct);
        left.add(btnAllProduct, 0, 5, 2, 1);
        left.addRow(6, lblStaff, comboStaff);
        left.add(btnAllStaff, 0, 7, 2, 1);

        Separator separator = new Separator();
        separator.setPadding(new Insets(20, 0, 5, 0));

        //label
        Label lblTextSummary = new Label("Sales Summary");
        lblTextSummary.getStyleClass().add("label-large");
        lblTextSummary.setMaxWidth(999);
        lblTextSummary.setAlignment(Pos.CENTER);

        left.add(separator, 0, 8, 2, 1);
        left.add(lblTextSummary, 0, 9, 2, 1);
        left.addRow(10, lblQty, txtQty);
        left.addRow(11, lblTotal, txtSales);
        left.addRow(12, lblVat, txtVat);
        left.add(lblInfo, 0, 13, 2, 1);

        //format
        left.setHgap(10);
        left.setVgap(15);

        //right
        //labels
        Label lblChartSummary = new Label("Summary Chart");
        lblChartSummary.getStyleClass().add("label-large");
        lblChartSummary.setMaxWidth(999);
        lblChartSummary.setAlignment(Pos.CENTER);

        Label lblProductChart = new Label("Sales Chart, Grouped By Product");
        lblProductChart.setMaxWidth(999);
        lblProductChart.setAlignment(Pos.CENTER);
        Label lblStaffChart = new Label("Sales Chart, Grouped By Retailers");
        lblStaffChart.setMaxWidth(999);
        lblStaffChart.setAlignment(Pos.CENTER);

        //add elements to grid
        GridPane right = new GridPane();
        right.addColumn(0, lblChartSummary, pieByProduct, lblProductChart, pieByStaff, lblStaffChart);

        //format
        right.setHgap(15);

        //add elements to root
        HBox root = new HBox(left, right);
        root.setPadding(new Insets(30));

        return root;
    }

    private void init_pieByStaff() {

        //create chart data with data
        pieByStaff = new PieChart();

        //format
        pieByStaff.setPrefHeight(250);
        pieByStaff.setMaxWidth(500);
        pieByStaff.setLegendSide(Side.RIGHT);

        pieByStaff.setOnMouseClicked(e -> handle_pieClick(e) );
    }

    private void handle_pieClick(MouseEvent e) {
        LinkedHashMap<String, Number> input = new LinkedHashMap<>();

        if (e.getSource() == pieByStaff) {

            int i = 0;
            String temp;

            //for all the products in the filtered list
            for (Sales s : filteredStaff) {
                //set identifying value
                if (s.getStaff() == null)
                    temp = "Deleted Staff.";
                else
                    temp = "|" + s.getStaffId() + "|" + s.getStaff();

                if (input.containsKey(temp)) {//if it exists in the list add their values
                    input.replace(temp, input.get(temp).doubleValue() + s.getSales());
                } else//doesn't exist
                {
                    if (i < 7) {//keep sectors under 8 to reduce clutter
                        input.put(temp, s.getSales());
                        i += 1;
                    } else {//add what's left as others
                        if (input.containsKey("Others")) {//if others is in the list
                            input.replace("Others", input.get("Others").doubleValue() + s.getSales());
                        } else {//others is not in the list
                            input.put("Others", s.getSales());
                        }

                    }
                }
            }
        } //same as load for staff
        else {
            int i = 0;
            String temp;

            //for all the products in the filtered list
            for(Sales s: filteredProduct){
                //set identifying value
                if (s.getProduct() == null)
                    temp = "Deleted Product";
                else
                    temp = "|" + s.getProductId() + "|" + s.getProduct();

                if(input.containsKey(temp)){//if it exists in the list add their values
                    input.replace(temp, input.get(temp).doubleValue() + s.getSales());
                }
                else//doesn't exist
                {
                    if(i < 7){//keep sectors under 8 to reduce clutter
                        input.put(temp, s.getSales());
                        i += 1;
                    }
                    else {//add what's left as others
                        if (input.containsKey("Others")){//if others is in the list
                            input.replace("Others", input.get("Others").doubleValue() + s.getSales());
                        } else{//others is not in the list
                            input.put("Others", s.getSales());
                        }

                    }
                }
            }
        } //same as load for product


        //if there is nothing to show
        if (input.isEmpty()) {
            return;
        }


        //get Linked hash map of name value pair
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        //change name value pair to pie chart data
        Iterator it = input.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            data.add(new PieChart.Data((String) pair.getKey(), Double.parseDouble(pair.getValue().toString())));
            it.remove();
        }

        //create pie chart
        PieChart pieChart = new PieChart(data);
        pieChart.setMinWidth(700);
        pieChart.setPrefHeight(600);
        pieChart.setLegendSide(Side.BOTTOM);



        //code for having values next to the names
        pieChart.getData().forEach(d -> {
            Optional<Node> opTextNode = pieChart.lookupAll(".chart-pie-label").stream().filter(n -> n instanceof Text && ((Text) n).getText().contains(d.getName())).findAny();
            opTextNode.ifPresent(node -> ((Text) node).setText(d.getName() + "  " + d.getPieValue()));
        });

        new CreateStage(pieChart, "Pie", false);
    }


    private void view_pieByStaff(LinkedHashMap<String, Number> input) {

        //get Linked hash map of name value pair
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        //change name value pair to pie chart data
        Iterator it = input.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            data.add(new PieChart.Data((String) pair.getKey(), Double.parseDouble(pair.getValue().toString())));
            it.remove();
        }

        if (data.isEmpty()) {
            data.add(new PieChart.Data("No Data", 1));
        }

        //set data
        pieByStaff.setData(data);
    }

    private void init_pieByProduct() {

        //create chart
        pieByProduct = new PieChart();

        //format
        pieByProduct.setPrefHeight(250);
        pieByProduct.setPrefWidth(500);
        pieByProduct.setLegendSide(Side.RIGHT);


        pieByProduct.setOnMouseClicked(e -> handle_pieClick(e));

    }

    private void view_pieByProduct(LinkedHashMap<String, Number> input) {

        //get Linked hash map of name value pair
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        //change name value pair to pie chart data
        Iterator it = input.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            data.add(new PieChart.Data((String) pair.getKey(), Double.parseDouble(pair.getValue().toString())));
            it.remove();
        }

        if (data.isEmpty()) {
            data.add(new PieChart.Data("No Data", 1));
        }

        //set data
        pieByProduct.setData(data);

    }

    private void init_btnAllDate() {

        btnAllDate = new Button("All Dates");
        btnAllDate.setMaxWidth(999);

        //handler
        btnAllDate.setOnAction(e -> handle_btnAllDate());

    }

    private void handle_btnAllDate() {
        //set values of dates the maximum range
        LocalDate minDate = LocalDate.of(2021, 1, 1);
        LocalDate maxDate = LocalDate.now();

        dateFrom.setValue(minDate);
        dateTo.setValue(maxDate);

        refreshView();
    }

    private void init_btnAllProducts() {

        btnAllProduct = new Button("All Products");
        btnAllProduct.setMaxWidth(999);

        //click
        btnAllProduct.setOnAction(e -> handle_btnAllProducts());
    }

    private void handle_btnAllProducts() {
        comboProduct.getSelectionModel().select(-1);

        refreshView();
    }

    private void init_btnAllStaff() {

        btnAllStaff = new Button("All Retailers");
        btnAllStaff.setMaxWidth(999);

        //click
        btnAllStaff.setOnAction(e -> handle_btnAllStaff());
    }

    private void handle_btnAllStaff() {
        comboStaff.getSelectionModel().select(-1);

        refreshView();
    }

    private void init_txtFields() {

        //Label for information
        lblInfo = new Label("");
        lblInfo.getStyleClass().add("label-red");
        lblInfo.getStyleClass().add("label-large");

        //text fields
        txtQty = new TextField();
        txtSales = new TextField();
        txtVat = new TextField();

        //fields should not be edited by user
        txtVat.setEditable(false);
        txtSales.setEditable(false);
        txtQty.setEditable(false);
    }

    private void init_comboProduct() {

        comboProduct = new ComboBox();

        comboProduct.setMaxWidth(999);

        //listener for refresh
        comboProduct.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            refreshView();
        });
    }

    private void init_comboStaff() {

        comboStaff = new ComboBox();

        comboStaff.setMaxWidth(999);

        //listener for refresh
        comboStaff.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            refreshView();
        });
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

        refreshView();
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

        refreshView();
    }

    public void updateObject() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {


                //load data for combo boxes
                ObservableList<Product> products = FXCollections.observableArrayList();
                ObservableList<Staff> staff = db.getRetailStaff();

                ArrayList<Number> idList = new ArrayList<>();
                idList = db.getAllProduct();

                for (Number x : idList) {
                    Product p = new Product(x.intValue());
                    p.load(db);
                    products.add(p);
                }

                //populate comobos
                comboProduct.setItems(products);
                comboStaff.setItems(staff);

                //load sales data;
                salesProduct = db.getSalesProduct();
                salesStaff = db.getSalesStaff();

                //set filtered source
                filteredStaff = new FilteredList<>(salesStaff);
                filteredProduct = new FilteredList<>(salesProduct);

                //view changes
                refreshView();
            }
        });
    }

    private Date from;
    private Date to;
    private int staff;
    private int product;

    private void refreshView() {
        from = Date.from(dateFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        to = Date.from(dateTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        if(comboProduct.getSelectionModel().getSelectedIndex() == -1)
            product = 0;
        else
            product =((Product)comboProduct.getSelectionModel().getSelectedItem()).getId();

        if (comboStaff.getSelectionModel().getSelectedIndex() == -1)
            staff = 0;
        else
            staff = ((Staff)comboStaff.getSelectionModel().getSelectedItem()).getId();


        filteredProduct.setPredicate(sales -> check(sales));
        filteredStaff.setPredicate(sales -> check(sales));

        loadCharts();

    }

    private boolean check(Sales sales) {
        if (from.compareTo(sales.getDate()) <= 0  && to.compareTo(sales.getDate()) >=0 &&
                (staff == 0 || sales.getStaffId() == staff) &&
                (product == 0 || sales.getProductId() == product))
            return true;

        return false;
    }

    private void loadCharts() {
        load_pieByStaff();
        load_pieByProduct();
    }

    private void load_pieByStaff() {
        LinkedHashMap<String, Number> data = new LinkedHashMap<>();

        int i = 0;
        String temp;

        //for all the products in the filtered list
        for(Sales s: filteredStaff){
            //set identifying value
            if(s.getStaff() == null)
                temp = "Deleted Staff.";
            else
                temp = "|" + s.getStaffId() + "|" + s.getStaff();

            if(data.containsKey(temp)){//if it exists in the list add their values
                data.replace(temp, data.get(temp).doubleValue() + s.getSales());
            }
            else//doesn't exist
            {
                if(i < 7){//keep sectors under 8 to reduce clutter
                    data.put(temp, s.getSales());
                    i += 1;
                }
                else {//add what's left as others
                    if (data.containsKey("Others")){//if others is in the list
                        data.replace("Others", data.get("Others").doubleValue() + s.getSales());
                    } else{//others is not in the list
                        data.put("Others", s.getSales());
                    }

                }
            }
        }

        //send data compiled to view
        view_pieByStaff(data);
    }

    private void load_pieByProduct() {
        //counters for the summary
        int items = 0;
        double sales = 0, tax = 0;

        LinkedHashMap<String, Number> data = new LinkedHashMap<>();

        int i = 0;
        String temp;

        //for all the products in the filtered list
        for(Sales s: filteredProduct){
            //set identifying value
            if (s.getProduct() == null)
                temp = "Deleted Product";
            else
                temp = "|" + s.getProductId() + "|" + s.getProduct();

            if(data.containsKey(temp)){//if it exists in the list add their values
                data.replace(temp, data.get(temp).doubleValue() + s.getSales());
            }
            else//doesn't exist
            {
                if(i < 7){//keep sectors under 8 to reduce clutter
                    data.put(temp, s.getSales());
                    i += 1;
                }
                else {//add what's left as others
                    if (data.containsKey("Others")){//if others is in the list
                        data.replace("Others", data.get("Others").doubleValue() + s.getSales());
                    } else{//others is not in the list
                        data.put("Others", s.getSales());
                    }

                }
            }

            //add all values for the summary
            items += s.getQty();
            sales += s.getSales();
            tax += s.getTax();
        }


        //set values calculated
        txtQty.setText("" + items);
        txtSales.setText("" + sales);
        txtVat.setText("" + tax);

        //send data compiled to view
        view_pieByProduct(data);
    }
}
