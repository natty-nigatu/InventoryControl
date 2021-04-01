package ui.management;

import data.Category;
import data.Database;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import ui.main.Message;

public class CategoryManagement {
    public LANClient lanClient;
    private TextField txtName;
    private Button btnEdit, btnAdd, btnDelete;
    private TableView tblCategory;
    private String mode;
    private Database db;
    private Label lblInfo;

    public CategoryManagement(Database db) {
        this.db = db;
    }

    public HBox createUI() {

        //starts in view mode
        mode = "view";

        //create controls
        init_txtFields();
        init_btnAdd();
        init_btnEdit();
        init_btnDelete();
        init_tblCategory();
        updateObject();

        return init_interface();

    }

    private HBox init_interface() {
        //right
        //label
        Label lblCategory = new Label("Category");
        lblCategory.getStyleClass().add("label-large");
        lblCategory.setMaxWidth(999);
        lblCategory.setAlignment(Pos.CENTER);

        //add elements in left
        VBox left = new VBox(lblCategory, tblCategory);
        left.setSpacing(20);

        //left
        //label
        Label lblI = new Label("Information");
        lblI.getStyleClass().add("label-large");
        lblI.setMaxWidth(999);
        lblI.setAlignment(Pos.CENTER);

        lblInfo = new Label();
        lblInfo.getStyleClass().add("label-red");

        Label lblName = new Label("Name");

        //create grid and add elements
        GridPane right = new GridPane();
        right.addRow(1, lblName, txtName);
        right.add(lblI, 0, 0, 2, 1);
        right.add(lblInfo, 0, 3, 2, 1);
        right.add(btnEdit, 0, 4, 2, 1);
        right.add(btnAdd, 0, 5, 2, 1);
        right.add(btnDelete, 0, 6, 2, 1);

        //format grid
        right.setVgap(20);
        right.setHgap(10);
        right.setAlignment(Pos.CENTER);

        //create root and add elements format
        HBox root = new HBox(left, right);
        root.setSpacing(20);
        root.setPadding(new Insets(30));

        return root;
    }

    private void init_txtFields() {

        txtName = new TextField();

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

        //disable controls
        txtName.setEditable(false);
    }

    private void init_btnEdit() {

        btnEdit = new Button("Edit Name");
        btnEdit.setMaxWidth(999);
        btnEdit.setPrefHeight(40);

        //click
        btnEdit.setOnAction(e -> handle_btnEdit());
    }

    private void handle_btnEdit() {
        if (mode.contentEquals("view")) {//edit button

            //check if a category is selected
            if (tblCategory.getSelectionModel().getSelectedItem() == null)
                return;

            mode = "edit";

            //format controls
            btnEdit.setText("Save");
            btnAdd.setText("Cancel");
            btnDelete.setVisible(false);

            //enable
            txtName.setEditable(true);
        }
        else {
            if(txtName.getText().length() == 0){

                lblInfo.setText("Category name is too short");
                return;
            }

            if (txtName.getText().isBlank()) {
                lblInfo.setText("Category name is empty");
                return;
            }


            if (mode.contentEquals("edit")) {//edit save
                handle_editSave();
            }
            else {//new Save
                handle_newSave();
            }

            //revert back to edit mode
            handle_btnAdd();
        }
    }

    private void handle_editSave() {
        Category category = (Category)tblCategory.getSelectionModel().getSelectedItem();

        category.setName(txtName.getText());
        category.save(db);

        lanClient.sender.send("update");

        tblCategory.refresh();

    }

    private void handle_newSave() {
        Category category = new Category(0, txtName.getText());

        category.add(db);

        lanClient.sender.send("update");



        tblCategory.getItems().add(category);
        tblCategory.refresh();
    }

    private void init_btnAdd() {

        btnAdd = new Button("Add New");
        btnAdd.setPrefWidth(300);
        btnAdd.setPrefHeight(40);

        //on click
        btnAdd.setOnAction(e -> handle_btnAdd());
    }

    private void handle_btnAdd() {
        if (mode.contentEquals("view")) {//add new button

            mode = "new";

            //format controls
            btnEdit.setText("Save");
            btnAdd.setText("Cancel");
            btnDelete.setVisible(false);

            //enable
            txtName.setEditable(true);

            //clear fields
            txtName.setText("");
        }
        else {//cancel button
            mode = "view";

            //format controls
            btnEdit.setText("Edit Name");
            btnAdd.setText("Add New");
            btnDelete.setVisible(true);
            lblInfo.setText("");

            //enable
            txtName.setEditable(false);

            handle_tblCategory();
        }
    }

    private void init_btnDelete() {

        btnDelete = new Button("Delete");
        btnDelete.setMaxWidth(999);

        //on click
        btnDelete.setOnAction(e -> handle_btnDelete());
    }

    private void handle_btnDelete() {

        if (tblCategory.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        Category category = (Category)tblCategory.getSelectionModel().getSelectedItem();

        String header = "Delete " + category.getName() + "?";
        String title = "Delete?";
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
            category.delete(db);

            lanClient.sender.send("update");



            txtName.clear();

            tblCategory.getItems().remove(category);
            tblCategory.refresh();
            handle_tblCategory();

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

    private void init_tblCategory() {

        tblCategory = new TableView();
        tblCategory.setPrefWidth(300);
        tblCategory.setPrefHeight(450);

        //Name
        TableColumn<Category, String> colName = new TableColumn<>("Name");
        colName.setMinWidth(295);
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        tblCategory.getColumns().addAll(colName);

        // item changed
        tblCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            handle_tblCategory();
        });
    }

    private void handle_tblCategory() {
        Category category = (Category)tblCategory.getSelectionModel().getSelectedItem();

        try {
            txtName.setText(category.getName());
        }
        catch (Exception e){}
    }

    public void updateObject() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                tblCategory.setItems(db.getCategories());

                //clear fields
                txtName.setText("");

                if(!mode.equals("view"))
                    handle_btnAdd();
            }
        });
    }
}
