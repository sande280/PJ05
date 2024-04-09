import javax.swing.*;
import java.util.*;
import java.io.*;
/**
 * Online Marketplace
 *
 * This class represents a Customer in the online marketplace. It extends the User class and
 * provides additional functionalities such as adding and removing stores, viewing sales,
 * and printing the seller dashboard.
 *
 *
 */
public class Customer extends User {
    private ArrayList<Product> shoppingCart;
    private ArrayList<Product> purchased;

    public Customer(String username, String password) {
        super(username, password);
        shoppingCart = new ArrayList<Product>();
        purchased = new ArrayList<Product>();
    }

    public ArrayList<Product> getShoppingCart() {
        return shoppingCart;
    }

    public void addToCart(Product product) {
        this.shoppingCart.add(product);
    }

    public void removeFromCart(Product product) {
        this.shoppingCart.remove(product);

    }

    public void purchasedItem(Product product) {
        this.purchased.add(product);
    }
    public String purchasedToFile() throws IOException {
        String purchaseHistory = "";

        for (Product product : purchased) {
            purchaseHistory += "You purchased " + product.getName() + ".~";
        }

        return purchaseHistory;
    }

    public String printPurchaseHistory() {
        String purchaseHistory = "Previous Purchases:~";
        for (Product product : purchased) {
            purchaseHistory += "You purchased " + product.getName() + "~";
        }

        return purchaseHistory;
    }

    public String printCart() {
        String itemsInCart = "";
        for (Product product : shoppingCart) {
            itemsInCart += product.getName() + "~";
        }

        return itemsInCart;
    }
}
