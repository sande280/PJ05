import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Server
 *
 * This class represents the server in the online marketplace, which updates the market and connects to the client
 *
 * @author @author Anthony McCrovitz, Christopher Tan, Amanda Drozt, Neha Venkatraman, Jackson Sanders LE-1
 *
 * @version 2023-05-01
 */
public class Server implements Runnable {

    private final static int FIND_SELLER_METHOD = 1;
    private final static int FIND_CUSTOMER_METHOD = 2;
    private final static int FIND_SELLER_PASSWORD_METHOD = 3;
    private final static int FIND_CUSTOMER_PASSWORD_METHOD = 4;
    private final static int REGISTER_SELLER_METHOD = 5;
    private final static int REGISTER_CUSTOMER_METHOD = 6;
    private final static int CREATE_STORE_METHOD = 7;
    private final static int MODIFY_STORE_METHOD = 8;
    private final static int VIEW_STORE_STATS_METHOD = 9;
    private final static int VIEW_PRODS_IN_CART_METHOD = 10;
    private final static int VIEW_DASHBOARD_METHOD = 11;
    private final static int EXPORT_CSV_METHOD = 12;
    private final static int ADD_PRODUCT_METHOD = 13;
    private final static int MODIFY_PRODUCT_METHOD = 14;
    private final static int REMOVE_PRODUCT_METHOD = 15;
    private final static int VIEW_MARKET_METHOD = 16;
    private final static int SEARCH_MARKET_METHOD = 17;
    private final static int VIEW_CART_METHOD = 18;
    private final static int PURCHASE_HIST_METHOD = 19;
    private final static int SORT_MARKET_METHOD = 20;
    private final static int VIEW_DASH_METHOD = 21;
    private final static int REMOVE = 0;
    private final static int PURCHASE = 1;


    static Market market;
    Socket clientSocket;

    public Server(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            DataOutputStream writer = new DataOutputStream(clientSocket.getOutputStream());
            PrintWriter stringWriter = new PrintWriter(clientSocket.getOutputStream());
            DataInputStream reader = new DataInputStream(clientSocket.getInputStream());
            BufferedReader stringReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            stringWriter.println("connected!"); //tell client that the server is connected
            stringWriter.flush();
            while (true) {
                //read what action the user is doing
                int method = reader.readInt();

                if (method == FIND_SELLER_METHOD) {
                    String username = stringReader.readLine();
                    boolean userExists = (findSeller(username, market) != null);
                    writer.writeBoolean(userExists);
                    writer.flush();
                } else if (method == FIND_CUSTOMER_METHOD) {
                    String username = stringReader.readLine();
                    boolean userExists = (findCustomer(username, market) != null);
                    writer.writeBoolean(userExists);
                    writer.flush();
                } else if (method == FIND_SELLER_PASSWORD_METHOD) {
                    String username = stringReader.readLine();
                    String password = Objects.requireNonNull(findSeller(username, market)).getPassword();
                    stringWriter.println(password);
                    stringWriter.flush();
                } else if (method == FIND_CUSTOMER_PASSWORD_METHOD) {
                    String username = stringReader.readLine();
                    String password = Objects.requireNonNull(findCustomer(username, market)).getPassword();
                    stringWriter.println(password);
                    stringWriter.flush();
                } else if (method == REGISTER_SELLER_METHOD) {
                    String username = stringReader.readLine();
                    String password = stringReader.readLine();
                    market.sellers.add(new Seller(username, password));
                } else if (method == REGISTER_CUSTOMER_METHOD) {
                    String username = stringReader.readLine();
                    ;
                    String password = stringReader.readLine();
                    market.customers.add(new Customer(username, password));
                } else if (method == CREATE_STORE_METHOD) {
                    String username = stringReader.readLine();
                    Seller seller = findSeller(username, market);

                    boolean alreadyExists = false;
                    String storeName;
                    do {
                        alreadyExists = false;
                        storeName = stringReader.readLine();
                        for (Seller value : market.sellers) { //checks for stores in the market with identical name
                            for (int j = 0; j < value.getStores().size(); j++) {
                                if (value.getStores().get(j).getStoreName().equals(storeName)) {
                                    alreadyExists = true;
                                    break;
                                }
                            }
                        }

                        writer.writeBoolean(alreadyExists);
                        writer.flush();
                    } while (storeName == null || storeName.isEmpty() || alreadyExists);

                    assert seller != null;
                    seller.addStore(new Store(storeName)); //create new store
                } else if (method == ADD_PRODUCT_METHOD) {
                    String username = stringReader.readLine();
                    Seller seller = findSeller(username, market);

                    assert seller != null;
                    int numberStores = seller.getStores().size();
                    writer.writeInt(numberStores);
                    writer.flush();

                    if (numberStores == 0) {
                        continue;
                    }

                    Store store = getStoreDropdown(seller, stringWriter, stringReader);

                    int throughCSV = reader.readInt();
                    if (throughCSV == JOptionPane.NO_OPTION) {
                        boolean dupProdName = false;
                        String prodName;
                        do {
                            prodName = stringReader.readLine();

                            for (int i = 0; i < seller.getStores().size(); i++) {
                                for (int j = 0; j < seller.getStores().get(i).getProducts().size(); j++) {
                                    if (seller.getStores().get(i).getProducts().get(j).getName().equals(prodName)) {
                                        dupProdName = true;
                                        break;
                                    }
                                }
                            }

                            writer.writeBoolean(dupProdName);
                            writer.flush();
                        } while (prodName == null || prodName.isEmpty() || dupProdName);

                        int quantity = reader.readInt();
                        String description = stringReader.readLine();
                        double price = reader.readDouble();

                        int alrIn = store.addProduct(new Product(prodName, quantity, price, description, store.getStoreName()));
                        writer.writeInt(alrIn);
                        writer.flush();
                    } else {
                        String csvFilePath = stringReader.readLine();
                        int error = store.uploadProductsFromCSV(csvFilePath);
                        writer.writeInt(error);
                        writer.flush();
                    }
                } else if (method == MODIFY_PRODUCT_METHOD) {
                    String username = stringReader.readLine();
                    Seller seller = findSeller(username, market);

                    assert seller != null;
                    int numStores = seller.getStores().size();
                    writer.writeInt(numStores);
                    writer.flush();

                    if (numStores == 0) {
                        continue;
                    }

                    Store store = getStoreDropdown(seller, stringWriter, stringReader);

                    int numProd = store.getProducts().size();
                    writer.writeInt(numProd);
                    writer.flush();
                    if (numProd == 0) {
                        continue;
                    }

                    Product product = getProductDropdown(stringWriter, stringReader, store);

                    int modQuality = reader.readInt();
                    int MOD_NAME = 1;
                    int MOD_DESCR = 2;
                    int MOD_QUANTITY = 3;
                    int MOD_PRICE = 4;

                    if (modQuality == MOD_NAME) {
                        boolean dupProdName = false;
                        String name;
                        do {
                            name = stringReader.readLine();

                            for (int i = 0; i < seller.getStores().size(); i++) {
                                for (int j = 0; j < seller.getStores().get(i).getProducts().size(); j++) {
                                    if (seller.getStores().get(i).getProducts().get(j).getName().equals(name)) {
                                        dupProdName = true;
                                        break;
                                    }
                                }
                            }

                            writer.writeBoolean(dupProdName);
                            writer.flush();
                        } while (name == null || name.isEmpty() || dupProdName);
                        product.setName(name);
                    } else if (modQuality == MOD_DESCR) {
                        String description = stringReader.readLine();
                        product.setDescription(description);
                    } else if (modQuality == MOD_QUANTITY) {
                        int quantity = reader.readInt();
                        product.setQuantity(quantity);
                    } else {
                        double price = reader.readDouble();
                        product.setPrice(price);
                    }
                } else if (method == REMOVE_PRODUCT_METHOD) {
                    String username = stringReader.readLine();
                    Seller seller = findSeller(username, market);

                    assert seller != null;
                    int numStores = seller.getStores().size();
                    writer.writeInt(numStores);
                    writer.flush();

                    if (numStores == 0) {
                        continue;
                    }

                    Store store = getStoreDropdown(seller, stringWriter, stringReader);

                    int numProd = store.getProducts().size();
                    writer.writeInt(numProd);
                    writer.flush();

                    if (numProd == 0) {
                        continue;
                    }

                    Product product = getProductDropdown(stringWriter, stringReader, store);
                    int error = store.removeProduct(product);
                    writer.writeInt(error);
                    writer.flush();
                } else if (method == VIEW_STORE_STATS_METHOD) {
                    String username = stringReader.readLine();
                    Seller seller = findSeller(username, market);

                    assert seller != null;
                    String saleHistory = seller.viewSales();
                    stringWriter.println(saleHistory);
                    stringWriter.flush();
                } else if (method == VIEW_PRODS_IN_CART_METHOD) {
                    String productsInCarts = "";

                    // Iterate through customers' shopping carts and display products
                    for (Customer customer : market.customers) {
                        productsInCarts += "Customer: " + customer.getUsername() + "~";
                        productsInCarts += "Items in cart:~";
                        productsInCarts += customer.printCart();
                    }

                    stringWriter.println(productsInCarts);
                    stringWriter.flush();
                } else if (method == VIEW_DASHBOARD_METHOD) {
                    String username = stringReader.readLine();
                    Seller seller = findSeller(username, market);

                    int sort = reader.readInt();
                    assert seller != null;
                    String dashboard = seller.printSellerDashboard(sort);
                    stringWriter.println(dashboard);
                    stringWriter.flush();
                } else if (method == EXPORT_CSV_METHOD) {
                    String username = stringReader.readLine();
                    Seller seller = findSeller(username, market);

                    String lines = "";
                    for (int j = 0; j < Objects.requireNonNull(seller).getStores().size(); j++) {
                        Store store = seller.getStores().get(j);
                        for (int i = 0; i < store.getProducts().size(); i++) {
                            Product p = store.getProducts().get(i);
                            lines += p.getName() + "," + p.getQuantity() + "," + p.getPrice() + "," +
                                    p.getDescription() + "," + p.getStoreName() + "~";
                        }
                    }

                    stringWriter.write(lines);
                    stringWriter.flush();
                } else if (method == VIEW_MARKET_METHOD) {
                    String username = stringReader.readLine();
                    Customer customer = findCustomer(username, market);

                    String marketString = "";
                    for (Seller seller : market.sellers) {
                        marketString += seller.printSellerStores();
                    }

                    stringWriter.println(marketString);
                    stringWriter.flush();

                    int learnMore = reader.readInt();
                    if (learnMore == JOptionPane.YES_OPTION) {
                        boolean exists = false;
                        String storeContainProd;
                        do {
                            storeContainProd = stringReader.readLine();

                            for (Seller seller : market.sellers) {
                                for (int j = 0; j < seller.getStores().size(); j++) {
                                    if (seller.getStores().get(j).getStoreName().equals(storeContainProd)) {
                                        exists = true;
                                        break;
                                    }
                                }
                            }


                            writer.writeBoolean(exists);
                            writer.flush();
                        } while (!exists);


                        boolean existsInStore;
                        Store store;
                        String interestProduct;
                        do {
                            existsInStore = false;
                            store = findStoreAllSellers(storeContainProd, market);
                            interestProduct = stringReader.readLine();

                            for (int i = 0; i < store.getProducts().size(); i++) {
                                if (store.getProducts().get(i).getName().equals(interestProduct)) {
                                    existsInStore = true;
                                    break;
                                }
                            }

                            writer.writeBoolean(existsInStore);
                            writer.flush();
                        } while (!existsInStore);

                        Product product = store.findProduct(interestProduct);
                        String productPage = product.printProductPage();
                        String productName = product.getName();

                        stringWriter.println(productPage);
                        stringWriter.flush();
                        stringWriter.println(productName);
                        stringWriter.flush();

                        int addToCart = reader.readInt();
                        if (addToCart == JOptionPane.YES_OPTION) {
                            int quantity = reader.readInt();

                            for (int i = 0; i < quantity; i++) {
                                assert customer != null;
                                customer.addToCart(product);
                            }
                        }
                    }
                } else if (method == SEARCH_MARKET_METHOD) {
                    String searchQuery = stringReader.readLine();

                    String searchResults = "Search Results:~";
                    for (Seller seller : market.sellers) {
                        for (Store store : seller.getStores()) {
                            for (Product product : store.getProducts()) {
                                if (product.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                        product.getDescription().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                        product.getStoreName().toLowerCase().contains(searchQuery.toLowerCase())) {
                                    searchResults += "Product: " + product.getName() + ", Store: " + store.getStoreName() + "~";
                                }
                            }
                        }
                    }
                    stringWriter.println(searchResults);
                    stringWriter.flush();
                } else if (method == VIEW_CART_METHOD) {
                    String username = stringReader.readLine();
                    Customer customer = findCustomer(username, market);

                    assert customer != null;
                    String customerCart = "Shopping Cart:~" + customer.printCart();
                    stringWriter.println(customerCart);
                    stringWriter.flush();

                    int cartAction = reader.readInt();
                    if (cartAction == REMOVE) {
                        ArrayList<Product> productsInCart = customer.getShoppingCart();
                        writer.writeInt(productsInCart.size());
                        writer.flush();

                        for (Product product : productsInCart) {
                            stringWriter.println(product.getName());
                            stringWriter.flush();
                        }

                        String productName = stringReader.readLine();
                        for (int i = 0; i < customer.getShoppingCart().size(); i++) {
                            if (customer.getShoppingCart().get(i).getName().equals(productName)) {
                                Product product = customer.getShoppingCart().get(i);
                                customer.removeFromCart(product);
                                break;
                            }
                        }
                    } else if (cartAction == PURCHASE) {
                        purchaseItems(customer, writer, stringWriter, market);
                    }
                } else if (method == PURCHASE_HIST_METHOD) {
                    String username = stringReader.readLine();
                    Customer customer = findCustomer(username, market);

                    assert customer != null;
                    String purchaseHistory = customer.printPurchaseHistory();
                    stringWriter.println(purchaseHistory);
                    stringWriter.flush();

                    int export = reader.readInt();
                    if (export == JOptionPane.YES_OPTION) {
                        String fileContent = customer.purchasedToFile();
                        stringWriter.println(fileContent);
                        stringWriter.flush();
                    }
                } else if (method == SORT_MARKET_METHOD) {
                    int sortChoice = reader.readInt();

                    if (sortChoice == 1) {
                        String sortedMarket = sortPriceHighLow(market);
                        stringWriter.println(sortedMarket);
                        stringWriter.flush();
                    } else if (sortChoice == 2) {
                        String sortedMarket = sortPriceLowHigh(market);
                        stringWriter.println(sortedMarket);
                        stringWriter.flush();
                    } else if (sortChoice == 3) {
                        String sortedMarket = sortQuantityHighLow(market);
                        stringWriter.println(sortedMarket);
                        stringWriter.flush();
                    } else {
                        String sortedMarket = sortQuantityLowHigh(market);
                        stringWriter.println(sortedMarket);
                        stringWriter.flush();
                    }
                } else if (method == VIEW_DASH_METHOD) {
                    String username = stringReader.readLine();
                    Customer customer = findCustomer(username, market);

                    int sortChoice = reader.readInt();
                    if (sortChoice == 1) {
                        String customerDash = sortMostFrequentlyPurchasedFrom(username, market);
                        stringWriter.println(customerDash);
                        stringWriter.flush();
                    } else {
                        String customerDash = sortTotalProductsSold(username, market);
                        stringWriter.println(customerDash);
                        stringWriter.flush();
                    }
                }
            }
        } catch (Exception e) {
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        //market persistence
        try {
            FileInputStream fin = new FileInputStream("data.ser");
            ObjectInputStream oin = new ObjectInputStream(fin);
            market = (Market) oin.readObject();
        } catch (Exception e) {
            market = new Market();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    FileOutputStream fout = new FileOutputStream("data.ser");
                    ObjectOutputStream oout = new ObjectOutputStream(fout);
                    oout.writeObject(market);
                } catch (Exception e) {
                    return;
                }
            }
        });

        //ServerSocket serverSocket = new ServerSocket(4242);
        while (true) {
            try {
                //new Thread(new Server(serverSocket.accept())).start();
                ServerSocket serverSocket = new ServerSocket(4242);
                Server serverInstance = new Server(serverSocket.accept());
                new Thread(serverInstance).start();
            } catch (IOException e) {
            }
        }
    }

    public static Seller findSeller(String username, Market market) {
        for (Seller seller : market.sellers) {
            if (username.equals(seller.getUsername())) {
                return seller;
            }
        }

        return null;
    }

    public static Customer findCustomer(String username, Market market) {
        for (Customer customer : market.customers) {
            if (username.equals(customer.getUsername())) {
                return customer;
            }
        }

        return null;
    }

    public static Store getStoreDropdown(Seller seller, PrintWriter stringWriter, BufferedReader stringReader) throws Exception {
        ArrayList<Store> stores = seller.getStores();

        for (Store store : stores) {
            stringWriter.println(store.getStoreName());
            stringWriter.flush();
        }

        String storeName = stringReader.readLine();

        return seller.findStore(storeName);
    }

    public static Product getProductDropdown(PrintWriter stringWriter, BufferedReader stringReader, Store store) throws Exception {
        ArrayList<Product> products = store.getProducts();

        for (Product product : products) {
            stringWriter.println(product.getName());
            stringWriter.flush();
        }

        String productName;
        do {
            productName = stringReader.readLine();
        } while (productName == null || productName.isEmpty());

        return store.findProduct(productName);
    }

    public static Store findStoreAllSellers(String storeName, Market market) {
        Store store = null;
        for (Seller seller : market.sellers) {
            for (int j = 0; j < seller.getStores().size(); j++) {
                if (seller.getStores().get(j).getStoreName().equals(storeName)) {
                    store = seller.getStores().get(j);
                    break;
                }
            }
        }
        return store;
    }

    public static void purchaseItems(Customer customer, DataOutputStream writer, PrintWriter stringWriter, Market market) {
        int initialShoppingCartSize = customer.getShoppingCart().size();
        int itemsOutOfStock = 0;
        ArrayList<String> outOfStockItems = new ArrayList<>();

        for (int i = 0; i < initialShoppingCartSize; i++) {
            Product product = customer.getShoppingCart().get(0); //get product
            String storeName = product.getStoreName(); //name of store with product
            Store store = findStoreAllSellers(storeName, market);

            if (product.getQuantity() > 0) { //if product in stock
                customer.removeFromCart(product); //takes the product of the shopping cart
                customer.purchasedItem(product); //add product to customer purchased list

                product.setTotalProductSales(product.getTotalProductSales() + 1);
                product.setQuantity(product.getQuantity() - 1);
                store.storeRecord(product, customer.getUsername());
                store.setTotalProductsSold(store.getTotalProductsSold() + 1);
            } else { //if not in stock
                itemsOutOfStock++;
                outOfStockItems.add(product.getName());
                customer.removeFromCart(product); //remove from cart
            }
        }

        try {
            writer.writeInt(itemsOutOfStock);
            writer.flush();

            for (int i = 0; i < itemsOutOfStock; i++) {
                String errorMessage = "Sorry! " + outOfStockItems.get(i) + " is out of stock!";
                stringWriter.println(errorMessage);
                stringWriter.flush();
            }
        } catch (Exception e) {
            return;
        }
    }

    public static String sortPriceHighLow(Market market) {
        ArrayList<Product> allProducts = new ArrayList<>();

        for (Seller seller : market.sellers) {
            for (int j = 0; j < seller.getStores().size(); j++) {
                allProducts.addAll(seller.getStores().get(j).getProducts()); //get all products for every seller
            }
        }


        for (int i = 0; i < allProducts.size(); i++) { //sort the products so highest price comes first
            for (int j = 0; j < allProducts.size(); j++) {
                if (allProducts.get(i).getPrice() > allProducts.get(j).getPrice()) {
                    Product temp = allProducts.get(j);
                    allProducts.set(j, allProducts.get(i));
                    allProducts.set(i, temp);
                }
            }
        }

        String sortedMarket = "";
        for (Product allProduct : allProducts) {
            sortedMarket += allProduct.getName() + " in " + allProduct.getStoreName() +
                    " is sold at $" + allProduct.getPrice() + "~";
        }

        return sortedMarket;
    }

    public static String sortPriceLowHigh(Market market) {
        ArrayList<Product> allProducts = new ArrayList<>();

        for (Seller seller : market.sellers) {
            for (int j = 0; j < seller.getStores().size(); j++) {
                allProducts.addAll(seller.getStores().get(j).getProducts()); //get all products for every seller
            }
        }


        for (int i = 0; i < allProducts.size(); i++) { //sort the products so lowest price comes first
            for (int j = 0; j < allProducts.size(); j++) {
                if (allProducts.get(i).getPrice() < allProducts.get(j).getPrice()) {
                    Product temp = allProducts.get(j);
                    allProducts.set(j, allProducts.get(i));
                    allProducts.set(i, temp);
                }
            }
        }

        String sortedMarket = "";
        for (Product allProduct : allProducts) {
            sortedMarket += allProduct.getName() + " in " + allProduct.getStoreName() +
                    " is sold at $" + allProduct.getPrice() + "~";
        }

        return sortedMarket;
    }

    public static String sortQuantityHighLow(Market market) {
        ArrayList<Product> allProducts = new ArrayList<>();

        for (Seller seller : market.sellers) {
            for (int j = 0; j < seller.getStores().size(); j++) {
                allProducts.addAll(seller.getStores().get(j).getProducts()); //get all products for every seller
            }
        }


        for (int i = 0; i < allProducts.size(); i++) { //sort the products so highest quantity available comes first
            for (int j = 0; j < allProducts.size(); j++) {
                if (allProducts.get(i).getQuantity() > allProducts.get(j).getQuantity()) {
                    Product temp = allProducts.get(j);
                    allProducts.set(j, allProducts.get(i));
                    allProducts.set(i, temp);
                }
            }
        }

        String sortedMarket = "";
        for (Product allProduct : allProducts) {
            sortedMarket += "There are " + allProduct.getQuantity() + " " +
                    allProduct.getName() + " available for purchase in " + allProduct.getStoreName() + "~";
        }

        return sortedMarket;
    }

    public static String sortQuantityLowHigh(Market market) {
        ArrayList<Product> allProducts = new ArrayList<>();

        for (Seller seller : market.sellers) {
            for (int j = 0; j < seller.getStores().size(); j++) {
                allProducts.addAll(seller.getStores().get(j).getProducts()); //get all products for every seller
            }
        }


        for (int i = 0; i < allProducts.size(); i++) { //sort the products so lowest quantity available comes first
            for (int j = 0; j < allProducts.size(); j++) {
                if (allProducts.get(i).getQuantity() < allProducts.get(j).getQuantity()) {
                    Product temp = allProducts.get(j);
                    allProducts.set(j, allProducts.get(i));
                    allProducts.set(i, temp);
                }
            }
        }

        String sortedMarket = "";
        for (Product allProduct : allProducts) {
            sortedMarket += "There are " + allProduct.getQuantity() + " " +
                    allProduct.getName() + " available for purchase in " + allProduct.getStoreName() + "~";
        }

        return sortedMarket;
    }

    public static String sortMostFrequentlyPurchasedFrom(String customerUsername, Market market) {
        ArrayList<Store> stores = new ArrayList<>();

        for (Seller seller : market.sellers) {
            stores.addAll(seller.getStores());
        }
        int[] purchaseTimes = new int[stores.size()];

        for (int i = 0; i < stores.size(); i++) {
            purchaseTimes[i] = stores.get(i).timesCustomerPurchasedFromStore(customerUsername);
        }

        for (int i = 0; i < stores.size(); i++) { //sort so stores most frequently purchased from print first
            for (int j = 0; j < stores.size(); j++) {
                if (purchaseTimes[i] > purchaseTimes[j]) {
                    Store temp = stores.get(j);
                    int tempTimes = purchaseTimes[j];
                    stores.set(j, stores.get(i));
                    purchaseTimes[j] = purchaseTimes[i];
                    stores.set(i, temp);
                    purchaseTimes[i] = tempTimes;
                }
            }
        }

        return printCustomerDash(customerUsername, stores);
    }

    public static String printCustomerDash(String customerUsername, ArrayList<Store> stores) {
        String customerDash = "";
        for (Store store : stores) {
            customerDash += store.printCustomerDashboard(customerUsername);
        }

        return customerDash;
    }

    public static String sortTotalProductsSold(String customerUsername, Market market) {
        ArrayList<Store> stores = new ArrayList<>();

        for (Seller seller : market.sellers) {
            stores.addAll(seller.getStores());
        }
        for (int i = 0; i < stores.size(); i++) { //sort so highest total products sold comes first
            for (int j = 0; j < stores.size(); j++) {
                if (stores.get(i).getTotalProductsSold() > stores.get(j).getTotalProductsSold()) {
                    Store temp = stores.get(j);
                    stores.set(j, stores.get(i));
                    stores.set(i, temp);
                }
            }
        }

        return printCustomerDash(customerUsername, stores);
    }
}
