package ui.authentication;

import data.Database;
import data.Staff;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.main.Message;
import ui.management.AccountManagement;
import ui.management.StaffManagement;

import java.util.ArrayList;

public class ChangeUsername {
    private TextField txtUsername, txtNewUsername;
    private Button btnSave, btnCancel;
    private Label lblInfo;
    private Database db;
    private int account;

    public ChangeUsername(Database db, int account) {
        this.db = db;
        this.account = account;
    }

    public VBox createUI() {

        //create controls
        init_txtFields();
        init_btnSave();
        init_btnCancel();
        setUsername();

        return init_interface();
    }

    private VBox init_interface() {
        //create grid
        Label lblUsername = new Label("Current Username");
        Label lblNewUsername = new Label("New Username");

        GridPane grid = new GridPane();
        grid.addColumn(0, lblUsername, lblNewUsername);
        grid.addColumn(1, txtUsername, txtNewUsername, lblInfo, btnSave, btnCancel);

        //format grid pane
        grid.setVgap(10);
        grid.setHgap(5);
        grid.setPadding(new Insets(30, 0, 30, 0));

        //create top
        Label lblChange = new Label("Change Username");
        lblChange.getStyleClass().add("label-large");
        lblChange.setAlignment(Pos.CENTER);
        lblChange.setMaxWidth(400);

        //add elements to vBox and format
        VBox root = new VBox(lblChange, grid);
        root.setPadding(new Insets(30));

        return root;
    }

    private void setUsername() {
        //load account data
        Staff staff = new Staff(account);
        staff.load(db);

        txtUsername.setText(staff.getUsername());

        txtNewUsername.requestFocus();

    }

    private void init_btnSave() {
        //create button
        btnSave = new Button("Save");
        btnSave.setPrefWidth(250);

        //disable until validated
        btnSave.setDisable(true);

        //set event handler
        btnSave.setOnAction(e -> handle_btnSave());
    }

    private void handle_btnSave() {

        //load account data
        Staff s = new Staff(account);
        s.load(db);

        if (usernameTaken(s.getId())) {
            new Message("Warning", "Username already taken", "Please try another username.");
            return;
        }

        db.setUsername(s.getId(), txtNewUsername.getText());


        new Message("Success", "Username changed Successfully", "Please click close to continue");
        handle_btnCancel();
    }

    private boolean usernameTaken(int id) {

        ArrayList<String>  allUsernames = db.getAllUsername(id);

        if (allUsernames.contains(txtUsername.getText()))
            return true;
        else
            return false;

    }

    private void init_btnCancel() {
        //create button
        btnCancel = new Button("Cancel");
        btnCancel.setPrefWidth(250);

        //set event handler
        btnCancel.setOnAction(e -> handle_btnCancel());
    }

    private void handle_btnCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void init_txtFields() {
        //Info label
        lblInfo = new Label();
        lblInfo.getStyleClass().add("label-red");

        //inputs
        txtUsername = new TextField();
        txtUsername.setEditable(false);
        txtNewUsername = new TextField();

        //validate inputs
        txtNewUsername.setOnKeyTyped(e -> handle_username(e.getSource()));
        txtUsername.setOnKeyTyped(e -> handle_username(e.getSource()));
    }

    private void handle_username(Object txt) {

        if (((TextField) txt).getText().length() < 5){
            if (txt == txtNewUsername)
                lblInfo.setText("New username is too short.");
            else
                lblInfo.setText("Current username is too short.");

            btnSave.setDisable(true);
            return;
        }

        if (txtUsername.getText().contentEquals(txtNewUsername.getText())){
            lblInfo.setText("Current and new username are the same.");
            btnSave.setDisable(true);
            return;
        }

        //last check
        if (txtNewUsername.getText().length() < 5) {
            lblInfo.setText("New username is too short.");
            btnSave.setDisable(true);
            return;
        }
        else if (txtUsername.getText().length() < 5) {
            lblInfo.setText("Current username is too short.");
            btnSave.setDisable(true);
            return;
        }


        lblInfo.setText("");
        btnSave.setDisable(false);

    }
}
