package ui.management;
import data.Database;
import data.ImageFile;
import data.Staff;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import program.LANClient;
import ui.CreateStage;
import ui.image.ImageProfile;
import ui.main.Message;

import java.util.ArrayList;


public class StaffManagement {
    public LANClient lanClient;
    private Button btnEdit, btnAdd, btnDelete;
    private TextField txtName, txtEmail, txtPhone, txtUsername;
    private ImageView imgProfile;
    private TableView tblStaff;
    private ComboBox comboStaffType;
    private Label lblNumber;
    private String mode;
    private Database db;
    private int account;
    private Button btnCancel;
    private ArrayList<String> allUsernames;
    private final String dir = "file:src/assets/profile/";


    public StaffManagement(Database db, int account) {
        this.db = db;
        this.account = account;
    }

    public HBox createUI() {


        //starts in view mode
        mode = "view";

        //create controls
        init_btnAdd();
        init_btnEdit();
        init_txtFields();
        init_tblStaff();
        init_combos();
        init_image();
        init_btnDelete();
        init_btnCancel();
        updateObject();

        return init_interface();

    }

    private HBox init_interface() {
        //left side
        Label lblStaff = new Label("Staff List");
        lblStaff.getStyleClass().add("label-large");
        lblStaff.setMaxWidth(999);
        lblStaff.setAlignment(Pos.CENTER);

        //lbl Count
        Label lblCount = new Label("Staff Count:");
        lblCount.getStyleClass().add("label-large");
        HBox count = new HBox(lblCount, lblNumber);
        count.setSpacing(15);
        count.setAlignment(Pos.CENTER);

        VBox left = new VBox(lblStaff, tblStaff, count);
        left.setSpacing(15);

        //right side
        //labels
        Label lblName = new Label("Name");
        Label lblEmail = new Label("Email");
        Label lblPhone = new Label("Phone");
        Label lblUsername = new Label("Username");
        Label lblType = new Label("Position");

        //grid for right side
        GridPane grid = new GridPane();
        grid.addColumn(0, lblName, lblEmail, lblPhone, lblUsername, lblType);
        grid.addColumn(1, txtName, txtEmail, txtPhone, txtUsername, comboStaffType);
        grid.add(btnEdit, 0, 5, 2, 1);
        grid.add(btnAdd, 0, 6, 2, 1);
        grid.add(btnDelete, 0, 7, 2, 1);

        grid.setHgap(10);
        grid.setVgap(15);

        //image and grid
        VBox right = new VBox(imgProfile, grid);
        right.setSpacing(25);
        right.setAlignment(Pos.CENTER);

        //add elements to root
        HBox root = new HBox(left, right);
        root.setSpacing(30);
        root.setPadding(new Insets(20));

        return root;
    }

    private void init_btnDelete() {
        btnDelete = new Button("Delete");
        btnDelete.setPrefWidth(300);
        btnDelete.setPrefHeight(15);

        //handler
        btnDelete.setOnAction(e -> handle_btnDelete());

    }

    private void handle_btnDelete() {
        //if no person selected do nothing
        if (tblStaff.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        Staff staff = (Staff) tblStaff.getSelectionModel().getSelectedItem();

        String header = "Delete " + staff.getName() + "?";
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
            staff.delete(db);
            tblStaff.getItems().remove(staff);
            tblStaff.refresh();
            count();
            txtUsername.clear();
            txtPhone.clear();
            txtUsername.clear();
            txtName.clear();
            txtEmail.clear();
            comboStaffType.getSelectionModel().clearSelection();
            imgProfile.setImage(null);
            handle_tblStaff();


            lanClient.sender.send("update");

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


    private void init_btnAdd() {
        btnAdd = new Button("Add New");
        btnAdd.setPrefWidth(300);
        btnAdd.setPrefHeight(30);

        //onclick
        btnAdd.setOnAction(e -> handle_btnAdd());
    }

    private void handle_btnAdd() {
        if (mode.contentEquals("view")) {//add button
            mode = "new";

            //disable table
            tblStaff.setDisable(true);
            btnDelete.setVisible(false);
            imgProfile.setImage(null);
            imgProfile.setDisable(true);

            //format buttons
            btnAdd.setText("Cancel");
            btnEdit.setText("Save");

            // enable edit
            txtName.setEditable(true);
            txtEmail.setEditable(true);
            txtPhone.setEditable(true);
            txtUsername.setEditable(true);
            //comboStaffType.setDisable(false);

            // set text to empty
            txtName.setText("");
            txtEmail.setText("");
            txtPhone.setText("");
            txtUsername.setText("");

        }
        else {//cancel button

            mode = "view";

            //enable table
            tblStaff.setDisable(false);
            btnDelete.setVisible(true);

            imgProfile.setDisable(false);

            //format buttons
            btnAdd.setText("Add New");
            btnEdit.setText("Edit");

            // disable edit
            txtName.setEditable(false);
            txtEmail.setEditable(false);
            txtPhone.setEditable(false);
            txtUsername.setEditable(false);
            //comboStaffType.setDisable(true);

            handle_tblStaff();
        }
    }

    private void init_btnEdit() {

        btnEdit = new Button("Edit");
        btnEdit.setPrefWidth(300);
        btnEdit.setPrefHeight(30);

        //on click
        btnEdit.setOnAction(e -> handle_btnEdit());
    }

    private void handle_btnEdit() {


        if (mode.contentEquals("view")) {//edit button

            //check if a person is selected
            if (tblStaff.getSelectionModel().getSelectedItem() == null)
                return;

            mode = "edit";

            //disable table
            tblStaff.setDisable(true);
            btnDelete.setVisible(false);
            imgProfile.setDisable(true);

            //format buttons
            btnAdd.setText("Cancel");
            btnEdit.setText("Save");

            // enable edit
            txtName.setEditable(true);
            txtEmail.setEditable(true);
            txtPhone.setEditable(true);
            txtUsername.setEditable(true);
            //comboStaffType.setDisable(false);


        }
        else {
            if (mode.contentEquals("edit")) {//edit save
                if(txtUsername.getText().length() < 5){
                    new Message("Warning", "Username is too short", "Please try another username.");
                    return;
                }

                if (editUsernameTaken()) {
                    new Message("Warning", "Username already taken", "Please try another username.");
                    return;
                }
                handle_editSave();
            }
            else {//new save
                if(txtUsername.getText().length() < 5){
                    new Message("Warning", "Username is too short", "Please try another username.");
                    return;
                }

                if (newUsernameTaken()) {
                    new Message("Warning", "Username already taken", "Please try another username.");
                    return;
                }
                handle_newSave();

            }
            //use method to revert controls back to edit mode
            handle_btnAdd();
            count();

        }
    }

    private boolean editUsernameTaken() {
        //get currently selected item
        Staff s = (Staff)tblStaff.getSelectionModel().getSelectedItem();
        allUsernames = db.getAllUsername(s.getId());

        if (allUsernames.contains(txtUsername.getText()))
            return true;
        else
            return false;

    }

    private boolean newUsernameTaken() {
        allUsernames = db.getAllUsername();

        if (allUsernames.contains(txtUsername.getText()))
            return true;
        else
            return false;

    }

    private void count() {
        lblNumber.setText(String.valueOf(tblStaff.getItems().size()));
    }

    private void handle_editSave() {
        //get currently selected item and update it
        Staff s = (Staff)tblStaff.getSelectionModel().getSelectedItem();

        int phone;
        try{
            phone = Integer.parseInt(txtPhone.getText());}
        catch (Exception e)
        {phone = 0;}

        s.setName(txtName.getText());
        s.setEmail(txtEmail.getText());
        s.setPhone(phone);
        s.setUsername(txtUsername.getText());
        s.setType(comboStaffType.getSelectionModel().getSelectedIndex() + 1);

        s.save(db);

        lanClient.sender.send("update");

        tblStaff.refresh();
    }

    private void handle_newSave() {
        int phone;
        try{
            phone = Integer.parseInt(txtPhone.getText());}
        catch (Exception e)
        {phone = 0;}


        Staff staff = new Staff(0,
                         txtName.getText(),
                         txtEmail.getText(),
                         phone,
                          txtUsername.getText(),
                 "$%6^&zQxqSKip!g5*@#:)",
                     comboStaffType.getSelectionModel().getSelectedIndex() + 1,
                         "none");

        staff.add(db);
        staff.setId(db.getId(staff.getUsername()));

        lanClient.sender.send("update");

        tblStaff.getItems().add(staff);
        tblStaff.refresh();

    }

    private void init_txtFields() {

        //create fields
        txtName = new TextField();
        txtEmail = new TextField();
        txtPhone = new TextField();
        txtUsername = new TextField();
        lblNumber = new Label("0");
        lblNumber.getStyleClass().add("label-large");

        //disable edit until needed
        txtName.setEditable(false);
        txtEmail.setEditable(false);
        txtPhone.setEditable(false);
        txtUsername.setEditable(false);

        try {
            txtUsername.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (t1.length() > 25)
                        txtUsername.setText(s);
                }
            });
        }
        catch (Exception ex){}

        try {
            txtEmail.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (t1.length() > 25)
                        txtEmail.setText(s);
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

        //number input only
        try {
            txtPhone.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (!t1.matches("\\d*") || t1.length() > 9)
                        txtPhone.setText(s);
                }
            });
        }
        catch (Exception ex){}

    }

    private void init_tblStaff() {

        tblStaff = new TableView();
        tblStaff.setPrefWidth(400);
        tblStaff.setPrefHeight(500);

        //Name
        TableColumn<Staff, String> colName = new TableColumn<>("Name");
        colName.setMinWidth(250);
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        //type
        TableColumn<Staff, String> colType = new TableColumn<>("Position");
        colType.setMinWidth(145);
        colType.setCellValueFactory(new PropertyValueFactory<>("position"));

        tblStaff.getColumns().addAll(colName, colType);

        // item changed
        tblStaff.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            handle_tblStaff();
        });
    }

    private void handle_tblStaff() {
        Staff staff = (Staff)tblStaff.getSelectionModel().getSelectedItem();

        if (staff == null) {
            return;
        }

        txtName.setText(staff.getName());
        txtEmail.setText(staff.getEmail());
        txtPhone.setText(String.valueOf(staff.getPhone()));
        txtUsername.setText(staff.getUsername());
        comboStaffType.getSelectionModel().select(staff.getType() - 1);
        setImage(staff.getImage());
    }


    public void updateObject() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                tblStaff.setItems(db.getStaff(account));
                txtUsername.clear();
                txtPhone.clear();
                txtUsername.clear();
                txtName.clear();
                txtEmail.clear();
                comboStaffType.getSelectionModel().clearSelection();
                imgProfile.setImage(null);
                count();
            }
        });

    }


    private void init_combos() {

        comboStaffType = new ComboBox();

        ObservableList<String>  type = FXCollections.observableArrayList();
        type.addAll("Administrator", "Retail", "Restock");

        comboStaffType.setItems(type);

        //format size
        comboStaffType.setMaxWidth(999);
        comboStaffType.getSelectionModel().select(0);

        //disable type until edit mode
        //comboStaffType.setDisable(true);
    }

    private void init_image() {

        imgProfile = new ImageView();
        imgProfile.setFitHeight(200);
        imgProfile.setFitWidth(200);
        imgProfile.setPreserveRatio(true);

        Circle clip = new Circle(100, 100, 100);
        imgProfile.setClip(clip);

        imgProfile.setOnMouseClicked(e -> handle_image());
    }

    private void handle_image() {

        //image should not work in add or edit mode
        if(!mode.contentEquals("view"))
            return;

        Staff staff = (Staff)tblStaff.getSelectionModel().getSelectedItem();

        if (staff == null) {
            return;
        }

        ImageProfile upload = new ImageProfile(btnCancel, db, staff);
        new CreateStage(upload.createUI(), "Profile Image", true);
        lanClient.sender.send("update");
    }

    private void setImage(String file) {
        ImageFile img = new ImageFile(file);
        if (img.getException() != null)
            img = new ImageFile(new Image("file:src/assets/error/noImage.png"));

        imgProfile.setImage(img.getImage());
    }

    private void init_btnCancel() {
        btnCancel = new Button();

        btnCancel.setOnAction(e-> {
            tblStaff.refresh();
            handle_tblStaff();
        });
    }
}
