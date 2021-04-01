package data;


import java.util.ArrayList;
import java.util.HashMap;


public class Product implements Cloneable{
    private int id;
    private String name;
    private String color;
    private String size;
    private int qty;
    private float price;
    private int category;
    //private String categoryName;
    private int gender;
    private String genderName;
    private int inCart;
    private ArrayList<String> images;
    //public static HashMap categoryList;

    public Product clone(){
        try {
            return (Product)super.clone();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Product(int id) {
        this.id = id;
        this.inCart = 0;
    }

    public void save(Database db) {
        db.setProductData(this.id, this.name, this.category, this.color, this.size, this.gender, this.qty, this.price);
    }

    public void add(Database db) {
        db.addProduct(this.id, this.name, this.category, this.color, this.size, this.gender, this.qty, this.price);
    }

    public void delete(Database db) {
        db.deleteProduct(this.id);
    }

    public void load(Database db) {

        HashMap data = db.getProduct(this.id);

        this.name = data.get("name").toString();
        this.color = data.get("color").toString();
        this.size = data.get("size").toString();
        this.qty = Integer.parseInt(data.get("qty").toString());
        this.price = Float.parseFloat(data.get("price").toString());
        this.category = Integer.parseInt(data.get("category").toString());
        this.gender = Integer.parseInt(data.get("gender").toString());

        //this.categoryList = db.getCategoryList();
        loadNames();
        setImages(db);
    }

    public void sell(Database db, int accountId, int tax) {
        db.sellProduct(accountId, this.id, this.inCart, this.price, tax);
        this.qty -= this.inCart;
        this.inCart = 0;
        this.save(db);
    }

    public void restock(Database db, int accountId) {
        db.restockProduct(accountId, this.id, this.inCart, this.price);
        this.qty += this.inCart;
        this.inCart = 0;
        this.save(db);
    }

    private void loadNames() {

        /*
        try {
            this.categoryName = categoryList.get(this.category).toString();
        } catch (Exception e) {
            this.categoryName = "";
        }

         */

        switch (this.gender) {
            case 0:
                this.genderName = "Boys";
                break;
            case 1:
                this.genderName = "Girls";
                break;
            case 2:
                this.genderName = "Men";
                break;
            case 3:
                this.genderName = "Women";
                break;
            default:
                this.genderName = "Unisex";
                break;
        }
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void addImage(Database db, String name) {
        db.addProductImage(this.id, name);
        this.images.add(name);
    }

    public void deleteImage(Database db, String name) {
        db.deleteProductImage(name);
        this.images.remove(name);
    }

    public void setImages(Database db) {
        this.images = db.getImages(this.id);
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
        loadNames();
    }

    //public String getCategoryName() {
    //   return categoryName;
    //}

    //public void setCategoryName(String categoryName) {
    //     this.categoryName = categoryName;

     //    this.category = getKey(categoryName);

     //}

    /*
    private int getKey(String category) {
        for (Object entry : Product.categoryList.entrySet()) {
            if (((HashMap.Entry<Number, String>)entry).getValue().equals(category)) {
                return Integer.parseInt(((HashMap.Entry<Number, String>)entry).getKey().toString());
            }
        }
        return 0;
    }

     */

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
        loadNames();
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public int getInCart() {
        return inCart;
    }

    public void setInCart(int inCart) {
        this.inCart = inCart;
    }

    /*
    public static HashMap getCategoryList() {
        return categoryList;
    }

    public static void setCategoryList(HashMap categoryList) {
        Product.categoryList = categoryList;
    }

     */

    public String toString() {
        return this.name;
    }
}
