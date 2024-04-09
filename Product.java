import javax.swing.*;
import java.io.Serializable;
/**
 * Product
 *
 * This class represents a Product in the online marketplace, containing details such as name, quantity, price, and description.
 *
 *
 */
public class Product implements Serializable {

    private String name;
    private String storeName;
    private String description;
    private int quantity;
    private double price;
    private int totalProductSales; //total number of times the product has sold
    public Product(String name, int quantity, double price, String description, String storeName) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.storeName = storeName;
        this.description = description;
        this.totalProductSales = 0;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getStoreName() {
        return storeName;
    }

    public double getPrice() {
        return price;
    }

    public int getTotalProductSales() {
        return totalProductSales;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setTotalProductSales(int totalProductSales) {
        this.totalProductSales = totalProductSales;
    }

    public String printProductPage() {
        return "Product Name: " + getName() + "~"
                + "Product Description: " + getDescription() + "~"
                + "Quantity Available: " + getQuantity() + "~";
    }

}
