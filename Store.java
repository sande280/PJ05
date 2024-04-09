import javax.swing.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
/**
 * Store
 *
 * This class represents a Store in the online marketplace, containing a list of products and other store-related information.
 *
 *
 */
public class Store implements Serializable {
    private String storeName; //name of the store
    private ArrayList<Product> products; //list of products for sale in the store
    private int totalProductsSold; //total products sold by the store
    private ArrayList<String> saleHistory; //list of sale history

    //create a new store given a name and list of products to be sold in the store
    public Store(String storeName) {
        this.storeName = storeName;
        products = new ArrayList<Product>();
        totalProductsSold = 0;
        saleHistory = new ArrayList<String>();
    }

    //get the store name
    public String getStoreName() {
        return storeName;
    }

    //set the store name
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    //get the list of products available at the store
    public ArrayList<Product> getProducts() {
        return products;
    }

    //set the list of products from the store from a given list
    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<String> getSaleHistory() {
        return saleHistory;
    }

    public void setTotalProductsSold(int totalProductsSold) {
        this.totalProductsSold = totalProductsSold;
    }

    public int getTotalProductsSold() {
        return totalProductsSold;
    }

    //add a product to the store
    public int addProduct(Product product) {
        if (products.contains(product)) {
            return -1;
        } else {
            products.add(product);
            return 0;
        }
    }

    //remove a product from the store
    public int removeProduct(Product product) {
        if (!products.contains(product)) {
            return -1;
        } else {
            products.remove(product);
            product.setQuantity(0);
            return 0;
        }
    }

    //when an item is purchased from the store, add to the record of sales by the store
    public void storeRecord(Product product, String customerUsername) {
        double revenue = product.getPrice(); //calculates store revenue from purchase

        //setTotalProductsSold(getTotalProductsSold() + 1); //increases the total products sold

        String sale = customerUsername + " purchased " + product.getName()
                + " for a total revenue of " + revenue + " dollars.";
        saleHistory.add(sale); //adds record of the sale to a list of store sales
    }

    //print the sale history of the store to the terminal so the seller can see list of sales by store
    public String printSaleHistory() {
        String saleHistory = "Sales for " + getStoreName() + ":~";

        for (int i = 0; i < getSaleHistory().size(); i++) {
            saleHistory += "Sale " + (i + 1) + ". " + getSaleHistory().get(i) + "~"; //print sale history
        }
        saleHistory += "--------------------------------------------------------~";

        return saleHistory;
    }

    public Product findProduct(String productName) {
        Product product = null;
        for (Product value : products) {
            if (productName.equals(value.getName())) {
                product = value;
            }
        }

        return product;
    }

    public String printStore() {
        String store = getStoreName() + "~";
        store += "Products Available in Store~";

        for (int i = 0; i < products.size(); i++) {
            store += (i + 1) + ". " + products.get(i).getName() + " - $" + products.get(i).getPrice() + "~";
        }
        store += "--------------------------------------------------------~"; //separate stores

        return store;
    }

    //prints the total sales for each product in the store
    public String printProdSaleDashboard() {
        String prodSaleDashboard = "Total Product Sales For "+ getStoreName() + ":~";
        prodSaleDashboard += getStoreName() + " has sold a total of " + getTotalProductsSold() + " products.~";
        for (int i = 0; i < getProducts().size(); i++) {
            prodSaleDashboard += getProducts().get(i).getName() + " sold " +
                    getProducts().get(i).getTotalProductSales() + " times.~";
        }
        prodSaleDashboard += "--------------------------------------------------------~";

        return prodSaleDashboard;
    }

    //prints the total times a customer has bought from the store
    public String printBuyerStatDashboard() {
        String buyerStatDashboard = "";
        ArrayList<String> customerNames = new ArrayList<>();
        boolean alreadyName = false;

        //creates an array list of all the customer usernames that have purchased from the store
        for (int i = 0; i < getSaleHistory().size(); i++) {
            int storeRecordSpace = getSaleHistory().get(i).indexOf(' ');
            String customerName = getSaleHistory().get(i).substring(0, storeRecordSpace);

            for (String name : customerNames) {
                alreadyName = false;
                if (customerName.equals(name)) {
                    alreadyName = true;
                    break;
                }
            }

            if (!alreadyName) {
                customerNames.add(customerName);
            }
        }

        int[] purchaseTimes = new int[customerNames.size()];

        //determines the number of times each customer has made a purchase in the store
        for (int i = 0; i < customerNames.size(); i++) {
            for (int j = 0; j < getSaleHistory().size(); j++) {
                int storeRecordSpace = getSaleHistory().get(i).indexOf(' ');
                String customerName = getSaleHistory().get(i).substring(0, storeRecordSpace);

                if (customerNames.get(i).equals(customerName)) {
                    purchaseTimes[i]++;
                }
            }
        }

        //prints the customer purchases for the store
        buyerStatDashboard += "Customers Who Purchased From " + getStoreName() + ":~";
        for (int i = 0; i < customerNames.size(); i++) {
            if (purchaseTimes[i] > 0) {
                buyerStatDashboard += "Customer " + customerNames.get(i) + " has made " +
                        purchaseTimes[i] + " purchases.~";
            }
        }
        buyerStatDashboard += "--------------------------------------------------------"; //separate stores

        return buyerStatDashboard;
    }

    public String printCustomerDashboard(String customerUsername) {
        ArrayList<String> customerNames = new ArrayList<>();
        int customerPurchaseTimes = timesCustomerPurchasedFromStore(customerUsername);
        boolean alreadyName = false;

        String customerDashboard = getStoreName();
        customerDashboard += "You have purchased from " + getStoreName() + " " + customerPurchaseTimes + " time(s).~";
        customerDashboard += getStoreName() + " has sold a total of " + getTotalProductsSold() + " product(s).~";
        customerDashboard += "--------------------------------------------------------~"; //separate stores

        return customerDashboard;
    }

    public int timesCustomerPurchasedFromStore(String customerUsername) {
        ArrayList<String> customerNames = new ArrayList<>();
        int customerPurchaseTimes = 0;
        boolean alreadyName = false;

        //creates an array list of all the customer usernames that have purchased from the store
        for (int i = 0; i < getSaleHistory().size(); i++) {
            int storeRecordSpace = getSaleHistory().get(i).indexOf(' ');
            String customerName = getSaleHistory().get(i).substring(0, storeRecordSpace);

            for (String name : customerNames) {
                alreadyName = false;
                if (customerName.equals(name)) {
                    alreadyName = true;
                    break;
                }
            }

            if (!alreadyName) {
                customerNames.add(customerName);
            }
        }

        for (int i = 0; i < getSaleHistory().size(); i++) {
            int spaceInHistory = getSaleHistory().get(i).indexOf(' ');
            String name = getSaleHistory().get(i).substring(0, spaceInHistory);

            if (name.equals(customerUsername)) {
                customerPurchaseTimes++;
            }
        }

        return customerPurchaseTimes;
    }

    public int uploadProductsFromCSV(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean headerLine = true; // Assuming the first line is the header

            while ((line = br.readLine()) != null) {
                if (headerLine) {
                    headerLine = false;
                    continue;
                }

                // Split the line using the comma separator
                String[] values = line.split(",");

                // Assuming the CSV file structure is: name,quantity,price,description,storeName
                String name = values[0].trim();
                int quantity = Integer.parseInt(values[1].trim());
                double price = Double.parseDouble(values[2].trim());
                String description = values[3].trim();
                String storeName = values[4].trim();

                // Create a new Product instance and add it to the store
                Product product = new Product(name, quantity, price, description, storeName);
                addProduct(product);
            }
        } catch (IOException e) {
            return -1;
        }

        return 0;
    }
}
