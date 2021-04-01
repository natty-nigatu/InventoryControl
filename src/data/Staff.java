package data;

import java.util.HashMap;

public class Staff {
    int id;
    String name;
    String email;
    int phone;
    String username;
    String password;
    int type;
    String image;
    String position;

    public Staff(int id){
        //this constructor must be used with load
        this.id = id;

    }

    public Staff(int id, String name, String email, int phone, String username, String password,  int type, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.type = type;
        this.image = image;

        updatePosition(type);

    }

    public void load(Database db) {
        HashMap data = db.getAccountData(this.getId());

        this.name = data.get("name").toString();
        this.email = data.get("email").toString();
        this.username = data.get("username").toString();
        this.type = Integer.parseInt(data.get("type").toString());
        this.phone = Integer.parseInt(data.get("phone").toString());
        try {
            this.image = data.get("image").toString();
        } catch (Exception ex) {
            this.image = "";
        }

        updatePosition(this.type);

    }

    private void updatePosition(int type) {


        switch (type) {
            case 1:
                position = "Administrator";
                break;
            case 2:
                position = "Retail";
                break;
            case 3:
                position = "Restock";
                break;
            default:
                position = "Not Set";
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        updatePosition(type);
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void save(Database db) {

        db.setAccountData(this.id, this.name, this.email, this.phone);
        db.setUsername(this.id, this.username);
        db.setAccountImage(this.id, this.image);
    }

    public void add(Database db) {
        db.addStaff(this.name, this.email, this.phone, this.username, this.password, this.type);
    }

    public void delete(Database db) {
        db.deleteStaff(this.id);
    }

    public String toString() {
        return this.name;
    }
}
