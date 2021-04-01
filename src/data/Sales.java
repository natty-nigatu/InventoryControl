package data;

import java.text.DecimalFormat;
import java.util.Date;

public class Sales {
    private int staffId;
    private String staff;
    private int productId;
    private String product;
    private Date date;
    private double sales;
    private double tax;
    private int qty;

    public Sales(int staffId, String staff, int productId, String product, Date date, double sales, double tax, int qty) {
        this.staffId = staffId;
        this.staff = staff;
        this.productId = productId;
        this.product = product;
        this.date = date;

        DecimalFormat df = new DecimalFormat("0.00");
        this.sales = Double.parseDouble(df.format(sales));
        this.tax = Double.parseDouble(df.format(tax));
        this.qty = qty;
    }

    public int getStaffId() {
        return staffId;
    }

    public String getStaff() {
        return staff;
    }

    public int getProductId() {
        return productId;
    }

    public String getProduct() {
        return product;
    }

    public Date getDate() {
        return date;
    }

    public int getQty() {
        return qty;
    }

    public double getSales() {
        return sales;
    }

    public double getTax() {
        return tax;
    }
}
