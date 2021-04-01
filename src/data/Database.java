package data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui.main.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    private Connection connection;
    public static final String host = "192.168.140.247";//"192.168.15.247";

    public boolean createConnection() {
        //db info
        String username = "root";
        String password = "";

        String dbName = "inventorycontrol";

        //create connection

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbName,
                    username, password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            new Message("Error", "Could Not Create Connection with Database", e.toString());
            return false;
        }
    }

    public boolean setUsername(int id, String username) {
        String query = "UPDATE staff SET username = ? where id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setInt(2, id);

            int result;

            result = stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }
    }

    public String getNextImage() {

        //get current number add 1 save new number and return
        String query = "SELECT value FROM misc where name = 'image'";

        try {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);

            String data;

            //get current
            resultSet.next();
            data = resultSet.getString("value");

            //create next
            int next = Integer.parseInt(data);
            next += 1;
            data = String.valueOf(next);

            query = "UPDATE misc SET value ='" + data +"' WHERE name = 'image'";
            stmt.executeUpdate(query);

            return data;




        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public String getTax() {

        //get current number add 1 save new number and return
        String query = "SELECT value FROM misc where name = 'tax'";

        try {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);

            String data;

            //get tax
            resultSet.next();
            data = resultSet.getString("value");

            return data;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public int setTax(String tax) {

        //get current number add 1 save new number and return
        String query = "UPDATE misc SET value ='" + tax +"' WHERE name = 'tax'";

        try {
            Statement stmt = connection.createStatement();

            int resultSet = stmt.executeUpdate(query);

            return resultSet;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }
    }

    public boolean setAccountImage(int id, String image) {
        String query = "UPDATE staff SET picture = ? where id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, image);
            stmt.setInt(2, id);

            int result;

            result = stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (Exception e) {

        }
    }

    public int login(String username, String password) {

        String query = "SELECT accounttype FROM staff WHERE username = ? AND password = ?";
        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet result = stmt.executeQuery();


            if (result.next()) {
                return result.getInt("accounttype");
            } else
                return 0;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }
    }

    public HashMap getAccountData(int id) {
        String query = "SELECT * FROM staff WHERE id = ?";

        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet result = stmt.executeQuery();

            //create hash map and add data
            HashMap data = new HashMap();

            if (result.next()) {
                data.put("name", result.getString("name"));
                data.put("email", result.getString("email"));
                data.put("phone", result.getInt("phone"));
                data.put("username", result.getString("username"));
                data.put("type", result.getString("accounttype"));
                data.put("image", result.getString("picture"));
                return data;
            } else
                return null;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }

    }

    public int setAccountData(int id, String name, String email, int phone) {
        String query = "UPDATE staff SET name = ?, email = ?, phone = ? where id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, phone);
            stmt.setInt(4, id);


            int result;

            result = stmt.executeUpdate();

            return result;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }
    }

    public String getPassword(int id) {
        String query = "SELECT password FROM staff WHERE id = ?";

        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet result = stmt.executeQuery();


            if (result.next()) {
                return result.getString("password");
            } else
                return "";
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return "-1";
        }
    }

    public int getId(String username) {
        String query = "SELECT id FROM staff WHERE username = ?";

        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet result = stmt.executeQuery();


            if (result.next()) {
                return result.getInt("id");
            } else
                return 0;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }
    }

    public int signup(String username) {

        String password = "$%6^&zQxqSKip!g5*@#:)";

        String query = "SELECT id FROM staff WHERE username = ? AND password = ?";
        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet result = stmt.executeQuery();


            if (result.next()) {
                return result.getInt("id");
            } else
                return 0;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }
    }

    public boolean setPassword(int id, String password) {

        String query = "UPDATE staff SET password = ? where id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, password);
            stmt.setInt(2, id);

            int result;

            result = stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }
    }


    public ArrayList<String> getAllUsername() {

        String query = "SELECT username FROM staff";
        ArrayList<String> data = new ArrayList<>();
        try {

            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(result.getString("username"));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public ArrayList<Number> getAllProduct() {

        String query = "SELECT id FROM product";
        ArrayList<Number> data = new ArrayList<>();
        try {

            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(result.getInt("id"));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public ArrayList<Number> getAllProduct(int id) {

        String query = "SELECT id FROM product WHERE id != ?";
        ArrayList<Number> data = new ArrayList<>();
        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(result.getInt("id"));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public ArrayList<String> getAllUsername(int id) {

        String query = "SELECT username FROM staff WHERE id != ?";
        ArrayList<String> data = new ArrayList<>();
        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet result = stmt.executeQuery();


            while (result.next()) {
                data.add(result.getString("username"));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public boolean deleteStaff(int id) {

        String query = "DELETE FROM staff WHERE id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            int result;

            result = stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }
    }

    public ObservableList<Staff> getStaff(int id){
        String query = "SELECT * FROM staff WHERE id != ?";
        ObservableList<Staff> data = FXCollections.observableArrayList();

        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(new Staff(result.getInt("id"),
                                    result.getString("name"),
                                    result.getString("email"),
                                    result.getInt("phone"),
                                    result.getString("username"),
                                    result.getString("password"),
                                    result.getInt("accounttype"),
                                    result.getString("picture")));
            }
                return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public ObservableList<Staff> getStaff(){
        String query = "SELECT * FROM staff";
        ObservableList<Staff> data = FXCollections.observableArrayList();

        try {

            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(new Staff(result.getInt("id"),
                        result.getString("name"),
                        result.getString("email"),
                        result.getInt("phone"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getInt("accounttype"),
                        result.getString("picture")));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public ObservableList<Staff> getRetailStaff(){
        String query = "SELECT * FROM staff WHERE accounttype = 1 OR accounttype = 2";
        ObservableList<Staff> data = FXCollections.observableArrayList();

        try {

            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(new Staff(result.getInt("id"),
                        result.getString("name"),
                        result.getString("email"),
                        result.getInt("phone"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getInt("accounttype"),
                        result.getString("picture")));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }


    public boolean addStaff(String name, String email, int phone, String username, String password, int type) {
        String query = "INSERT INTO staff(name, email, phone, username, password, accounttype) VALUES (?,?,?,?,?,?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, phone);
            stmt.setString(4, username);
            stmt.setString(5, password);
            stmt.setInt(6, type);

            int result;

            result = stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }

    }

    public boolean deleteCategory(int id) {
        String query = "DELETE FROM category WHERE id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            int result;

            result = stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }
    }

    public int addCategory(String name) {
        String query = "INSERT INTO category(name) VALUES (?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);

            ResultSet result;

            stmt.executeUpdate();
            result = stmt.getGeneratedKeys();

            int key = 0;
            if (result.next())
                key = result.getInt(1);


            return key;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }

    }

    public int setCategoryData(int id, String name) {
        String query = "UPDATE category SET name = ? where id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setInt(2, id);


            int result;

            result = stmt.executeUpdate();

            return result;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }
    }

    public int sellProduct(int staff, int product, int qty, float price, int tax) {
        String query = "INSERT INTO transaction (type, staff, product, qty, price, datee, timee, tax) VALUES " +
                        "(?, ?, ?, ?, ?, current_date(), current_time(),?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, 0);
            stmt.setInt(2, staff);
            stmt.setInt(3, product);
            stmt.setInt(4, qty);
            stmt.setFloat(5, price);
            stmt.setInt(6, tax);


            int result;

            result = stmt.executeUpdate();

            return result;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }
    }

    public ObservableList<Category> getCategories(){
        String query = "SELECT * FROM category";

        ObservableList<Category> data = FXCollections.observableArrayList();

        try {

            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(new Category(result.getInt("id"),
                        result.getString("name")));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public HashMap getProduct(int id) {
        String query = "SELECT * FROM product WHERE id = ?";

        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet result = stmt.executeQuery();

            //create hash map and add data
            HashMap data = new HashMap();

            if (result.next()) {
                data.put("name", result.getString("name"));
                data.put("color", result.getString("color"));
                data.put("size", result.getString("size"));
                data.put("gender", result.getString("gender"));
                data.put("qty", result.getInt("qty"));
                data.put("price", result.getFloat("price"));
                data.put("category", result.getInt("category"));
                return data;
            } else
                return null;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public HashMap getCategoryList() {
        String query = "SELECT * FROM category";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet result = stmt.executeQuery();

            //create hash map and add data
            HashMap data = new HashMap();

            while (result.next()) {
                data.put(result.getInt("id"), result.getString("name"));
            }
                return data;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public int addProduct(int id, String name, int category, String color, String size, int gender, int qty, float price) {
        String query = "INSERT INTO product(id, name, category, color, size, gender, qty, price) VALUES " +
                    "(?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setInt(3, category);
            stmt.setString(4, color);
            stmt.setString(5, size);
            stmt.setInt(6, gender);
            stmt.setInt(7, qty);
            stmt.setFloat(8, price);

            int result;

            result = stmt.executeUpdate();

            return result;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }

    }

    public int setProductData(int id, String name, int category, String color, String size, int gender, int qty, float price) {
        String query = "UPDATE product SET name=?, category=?, color=?, size=?, gender=?, qty=?, price=? WHERE " +
                        "id=?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setInt(2, category);
            stmt.setString(3, color);
            stmt.setString(4, size);
            stmt.setInt(5, gender);
            stmt.setInt(6, qty);
            stmt.setFloat(7, price);
            stmt.setInt(8, id);

            int result;

            result = stmt.executeUpdate();

            return result;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }

    }

    public ArrayList<String> getImages(int id) {

        String query = "SELECT file FROM image where product = ?";
        ArrayList<String> data = new ArrayList<>();
        try {

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(result.getString("file"));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public boolean addProductImage(int id, String name) {

        String query = "INSERT INTO image (product, file) VALUE (?,?)";

        try{
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setString(2, name);

            stmt.executeUpdate();

            return true;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }

    }

    public boolean deleteProductImage(String name) {

        String query = "DELETE FROM image where file = ?";

        try{
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);

            stmt.executeUpdate();

            return true;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }

    }

    public boolean deleteProduct(int id) {

        String query = "DELETE FROM product where id = ?";

        try{
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            stmt.executeUpdate();

            return true;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }

    }

    public ObservableList<Transaction> getTransaction(){
        String query = "SELECT transaction.id, transaction.type, staff.name AS staff , product.name AS product, " +
                "transaction.qty, transaction.price, transaction.datee, transaction.timee, transaction.tax, " +
                "category.id as category, product.id as productId, staff.id as staffId, staff.accounttype " +
                "FROM transaction " +
                "LEFT JOIN staff ON transaction.staff  = staff.id " +
                "LEFT JOIN product ON transaction.product = product.id " +
                "LEFT JOIN category ON product.category = category.id";

        ObservableList<Transaction> data = FXCollections.observableArrayList();

        try {

            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(new Transaction(result.getInt("id"),
                        result.getInt("type"),
                        result.getString("staff"),
                        result.getString("product"),
                        result.getInt("qty"),
                        result.getFloat("price"),
                        result.getDate("datee"),
                        result.getTime("timee"),
                        result.getInt("tax"),
                        result.getInt("category"),
                        result.getInt("productId"),
                        result.getInt("staffId"),
                        result.getInt("accounttype")));

            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public boolean clearTransactions() {

        String query = "DELETE FROM transaction";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            int result;

            result = stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return false;
        }
    }

    public int restockProduct(int staff, int product, int qty, float price) {
        String query = "INSERT INTO transaction (type, staff, product, qty, price, datee, timee, tax) VALUES " +
                "(?, ?, ?, ?, ?, current_date(), current_time(),?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, 1);
            stmt.setInt(2, staff);
            stmt.setInt(3, product);
            stmt.setInt(4, qty);
            stmt.setFloat(5, price);
            stmt.setInt(6, 0);


            int result;

            result = stmt.executeUpdate();

            return result;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return -1;
        }
    }

    public ObservableList<Sales> getSalesProduct(){
        String query = "SELECT transaction.staff AS staffId, staff.name AS staff, transaction.product AS productId, " +
                "product.name AS product,  transaction.datee, " +
                "SUM(transaction.qty*transaction.price) as sales , " +
                "SUM(transaction.qty*transaction.price*transaction.tax*0.01) as tax, SUM(transaction.qty) as qty " +
                "FROM transaction " +
                "LEFT JOIN staff ON transaction.staff = staff.id " +
                "LEFT JOIN product ON transaction.product = product.id " +
                "WHERE type = 0 " +
                "GROUP BY product,datee " +
                "ORDER BY sales DESC";

        ObservableList<Sales> data = FXCollections.observableArrayList();

        try {

            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(new Sales(result.getInt("staffId"),
                                    result.getString("staff"),
                                    result.getInt("productId"),
                                    result.getString("product"),
                                    result.getDate("datee"),
                                    result.getDouble("sales"),
                                    result.getDouble("tax"),
                                    result.getInt("qty")));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }

    public ObservableList<Sales> getSalesStaff(){
        String query = "SELECT transaction.staff AS staffId, staff.name AS staff, transaction.product AS productId, " +
                "product.name AS product,  transaction.datee, " +
                "SUM(transaction.qty*transaction.price) as sales , " +
                "SUM(transaction.qty*transaction.price*transaction.tax*0.01) as tax, SUM(transaction.qty) as qty " +
                "FROM transaction " +
                "LEFT JOIN staff ON transaction.staff = staff.id " +
                "LEFT JOIN product ON transaction.product = product.id " +
                "WHERE type = 0 " +
                "GROUP BY staff,datee " +
                "ORDER BY sales DESC";

        ObservableList<Sales> data = FXCollections.observableArrayList();

        try {

            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                data.add(new Sales(result.getInt("staffId"),
                        result.getString("staff"),
                        result.getInt("productId"),
                        result.getString("product"),
                        result.getDate("datee"),
                        result.getDouble("sales"),
                        result.getDouble("tax"),
                        result.getInt("qty")));
            }
            return data;

        } catch (Exception e) {
            new Message("Error", "Connection Error", e.toString());
            return null;
        }
    }
}
