package data;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.text.DecimalFormat;

public class Transaction {
    int id;
    int type;
    String Staff;
    String Product;
    int qty;
    float price;
    Date date;
    Time time;
    int tax;
    int category;
    int productId;
    int staffId;
    String typeName;
    double total;
    int position;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Transaction(int id, int type, String staff, String product, int qty, float price, Date date, Time time,
                       int tax, int category, int productId, int staffId, int position) {
        this.id = id;
        this.type = type;
        Staff = staff;
        Product = product;
        this.qty = qty;
        this.price = price;
        this.date = date;
        this.time = time;
        this.tax = tax;
        this.category = category;
        this.productId = productId;
        this.staffId = staffId;
        this.position = position;

        DecimalFormat df = new DecimalFormat("0.00");

        this.total = Double.parseDouble(df.format(qty * price * (1 + (tax * 0.01))));


        if (type == 0) {
            typeName = "Retail";
        } else {
            typeName = "Restock";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStaff() {
        return Staff;
    }

    public void setStaff(String staff) {
        Staff = staff;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
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

    public Date getDateObject() {
        return date;
    }

    public String getDate() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyy");
        return df.format(date);
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getTotal() {
        if (this.getType() == 1)
            return 0;

        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
