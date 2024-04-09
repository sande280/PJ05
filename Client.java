import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Client
 *
 * This class represents the client in the online marketplace, which displays the GUI and connects to the server
 *
 *
 */
public class Client {

    private final static int CLOSED = -1;
    private final static int SELLER = 1;
    private final static int CUSTOMER = 0;
    private final static int REGISTER = 1;
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

    static ObjectInputStream ois;
    static ObjectOutputStream oos;
    public static Object readObject() throws Exception {
        return ois.readObject();
    }
    public static void writeObject(Object o) throws Exception {
        oos.writeObject(o);
        oos.flush();
    }
    public static void main(String[] args) throws Exception {
        /*Market market;
        Socket serverSocket = new Socket("localhost", 4242);
        oos = new ObjectOutputStream(serverSocket.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(serverSocket.getInputStream());
         */

        //while (true) {
            //update
            //writeObject("");
            //market = (Market) readObject();

            try {
                Socket socket = new Socket("localhost", 4242); //connect to the server

                DataInputStream reader = new DataInputStream(socket.getInputStream());
                DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                BufferedReader stringReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter stringWriter = new PrintWriter(socket.getOutputStream());

                String connectionCheck = stringReader.readLine(); //check if connection was successful
                if (connectionCheck.equals("connected!")) { //indicate successful connection
                    JOptionPane.showMessageDialog(null, "Successfully connected to the market!",
                            "Successful Connection!", JOptionPane.INFORMATION_MESSAGE);
                }

                while (true) {
                    //ask user if they want to log in or register
                    int loginRegister;
                    do {
                        Object[] logRegOptions = {"Login", "Register"};
                        loginRegister = JOptionPane.showOptionDialog(null,
                                "What would you like to do?", "Welcome!", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, logRegOptions, null);
                        checkInvalidOptionError(loginRegister);
                    } while (loginRegister == CLOSED);

                    //ask user if they are a seller or customer
                    int customerSeller;
                    do {
                        Object[] cusSellOptions = {"Customer", "Seller"};
                        customerSeller = JOptionPane.showOptionDialog(null,
                                "Please select your role in the marketplace:", "Enter Role",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                                null, cusSellOptions, null);
                        checkInvalidOptionError(customerSeller);
                    } while (customerSeller == CLOSED);

                    //ask user for username
                    String username;
                    do {
                        username = JOptionPane.showInputDialog(null, "Enter username: ",
                                "Username", JOptionPane.INFORMATION_MESSAGE);
                        checkBlankStringError(username, "Username");
                    } while (username == null || username.isEmpty());

                    //ask user for password
                    String password;
                    do {
                        password = JOptionPane.showInputDialog(null, "Enter password: ",
                                "Password", JOptionPane.INFORMATION_MESSAGE);
                        checkBlankStringError(password, "Password");
                    } while (password == null || password.isEmpty());


                    boolean userExists = false;
                    if (customerSeller == SELLER) {
                        writer.writeInt(FIND_SELLER_METHOD);
                        writer.flush();
                        stringWriter.println(username);
                        stringWriter.flush();
                        userExists = reader.readBoolean(); //check if user exists
                    } else {
                        writer.writeInt(FIND_CUSTOMER_METHOD);
                        writer.flush();
                        stringWriter.println(username);
                        stringWriter.flush();
                        userExists = reader.readBoolean(); //check if user exists
                    }

                    if (userExists && loginRegister == REGISTER) { //if registering and user exists
                        showErrorMessage("User already exists! Please try another username.");
                        continue;
                    }

                    if (!userExists && loginRegister != REGISTER) { //if logging in with invalid username
                        showErrorMessage("User not found! Please register or check your entered username.");
                        continue;
                    }

                    //check password
                    if (userExists) {
                        if (customerSeller == SELLER) {
                            writer.writeInt(FIND_SELLER_PASSWORD_METHOD);
                            writer.flush();
                            stringWriter.println(username);
                            stringWriter.flush();
                            if (!password.equals(stringReader.readLine())) { //if password is not the seller password
                                showErrorMessage("Incorrect username and password combination!");
                                continue;
                            }
                        } else {
                            writer.writeInt(FIND_CUSTOMER_PASSWORD_METHOD);
                            writer.flush();
                            stringWriter.println(username);
                            stringWriter.flush();
                            if (!password.equals(stringReader.readLine())) { //if password is not the customer password
                                showErrorMessage("Incorrect username and password combination!");
                                continue;
                            }
                        }
                    }

                    if (loginRegister == REGISTER && customerSeller == SELLER) {
                        writer.writeInt(REGISTER_SELLER_METHOD);
                        writer.flush();
                        stringWriter.println(username);
                        stringWriter.flush();
                        stringWriter.println(password);
                        stringWriter.flush();
                    }

                    if (loginRegister == REGISTER && customerSeller == CUSTOMER) {
                        writer.writeInt(REGISTER_CUSTOMER_METHOD);
                        writer.flush();
                        stringWriter.println(username);
                        stringWriter.flush();
                        stringWriter.println(password);
                        stringWriter.flush();
                    }

                    //TODO not really a to do just wanted a space between login and rest of program
                    //start of user actions
                    if (customerSeller == SELLER) { //if seller
                        do {
                            int action = sellerStoreAction(); //ask user what to do
                            action += 7;

                            if (action == CREATE_STORE_METHOD) {
                                writer.writeInt(CREATE_STORE_METHOD);
                                stringWriter.println(username);
                                stringWriter.flush();
                                createStore(stringWriter, reader); //create store for seller
                            } else if (action == MODIFY_STORE_METHOD) {
                                modifyStore(reader, writer, stringWriter, stringReader, username); //modify products in a store
                            } else if (action == VIEW_STORE_STATS_METHOD) {
                                writer.writeInt(VIEW_STORE_STATS_METHOD);
                                stringWriter.println(username);
                                stringWriter.flush();

                                String saleHistory = stringReader.readLine();
                                saleHistory = replaceTilda(saleHistory);
                                JOptionPane.showMessageDialog(null, saleHistory, "Sale History",
                                        JOptionPane.INFORMATION_MESSAGE); //view the sale history for a store
                            } else if (action == VIEW_PRODS_IN_CART_METHOD) {
                                writer.writeInt(VIEW_PRODS_IN_CART_METHOD);
                                writer.flush();

                                String prodInCart = stringReader.readLine();
                                prodInCart = replaceTilda(prodInCart);
                                JOptionPane.showMessageDialog(null, prodInCart,
                                        "Items in Carts", JOptionPane.INFORMATION_MESSAGE);
                            } else if (action == VIEW_DASHBOARD_METHOD) {
                                writer.writeInt(VIEW_DASHBOARD_METHOD);
                                writer.flush();

                                stringWriter.println(username);
                                stringWriter.flush();
                                sortPrintSellerDashboard(writer, stringReader);
                            } else if (action == EXPORT_CSV_METHOD) {
                                writer.writeInt(EXPORT_CSV_METHOD);
                                writer.flush();

                                stringWriter.println(username);
                                stringWriter.flush();
                                exportProductToCSV(stringReader);
                            } else { //logout
                                break;
                            }
                        } while (true);
                    } else { //if customer
                        do {
                            int action = getCustomerAction();
                            writer.writeInt(action);
                            writer.flush();

                            if (action == VIEW_MARKET_METHOD) { //view the marketplace
                                stringWriter.println(username);
                                stringWriter.flush();
                                String marketString = stringReader.readLine();
                                marketString = replaceTilda(marketString);

                                JOptionPane.showMessageDialog(null, marketString, "Stores Information",
                                        JOptionPane.INFORMATION_MESSAGE);

                                int learnMore = learnMore();
                                writer.writeInt(learnMore);
                                writer.flush();

                                if (learnMore == JOptionPane.YES_OPTION) {
                                    String storeContainProd;
                                    boolean exists;
                                    do {
                                        storeContainProd = JOptionPane.showInputDialog(null,
                                                "What store is the product of interest in?", "Enter Store",
                                                JOptionPane.QUESTION_MESSAGE);

                                        stringWriter.println(storeContainProd);
                                        stringWriter.flush();

                                        exists = reader.readBoolean();

                                        if (!exists) {
                                            showErrorMessage("Please enter a valid store name!");
                                        }
                                    } while (!exists);

                                    String interestProduct;
                                    boolean existsInStore;
                                    do {
                                        interestProduct = JOptionPane.showInputDialog(null,
                                                "What product would you like to learn more about?", "Enter Product",
                                                JOptionPane.QUESTION_MESSAGE);

                                        stringWriter.println(interestProduct);
                                        stringWriter.flush();

                                        existsInStore = reader.readBoolean();
                                        if (!existsInStore) {
                                            showErrorMessage("This product is not in the store!");
                                        }
                                    } while (!existsInStore);

                                    String productPage = stringReader.readLine();
                                    productPage = replaceTilda(productPage);
                                    String productName = stringReader.readLine();
                                    productName = replaceTilda(productName);
                                    JOptionPane.showMessageDialog(null, productPage,
                                            productName + " Product Page", JOptionPane.INFORMATION_MESSAGE);

                                    int addToCart = JOptionPane.showConfirmDialog(null,
                                            "Would you like to add this product to your shopping cart?",
                                            "Add to Cart?", JOptionPane.YES_NO_OPTION);
                                    writer.writeInt(addToCart);
                                    if (addToCart == JOptionPane.YES_OPTION) {
                                        addToShoppingCart(writer);
                                    }
                                }
                            } else if (action == SEARCH_MARKET_METHOD) {
                                searchMarketplace(stringWriter, stringReader);
                            } else if (action == VIEW_CART_METHOD) { //view shopping cart
                                stringWriter.println(username);
                                stringWriter.flush();

                                String customerCart = stringReader.readLine();
                                customerCart = replaceTilda(customerCart);
                                JOptionPane.showMessageDialog(null, customerCart,
                                        "Items in Your Shopping Cart", JOptionPane.INFORMATION_MESSAGE);

                                int cartAction = getCartAction();
                                writer.writeInt(cartAction);
                                writer.flush();

                                if (cartAction == REMOVE) { //remove items from cart
                                    removeCartItem(reader, stringReader, stringWriter);
                                } else if (cartAction == PURCHASE) { //purchase items in cart
                                    int itemsOutOfStock = reader.readInt();
                                    for (int i = 0; i < itemsOutOfStock; i++) {
                                        String errorMessage = stringReader.readLine();
                                        errorMessage = replaceTilda(errorMessage);
                                        showErrorMessage(errorMessage);
                                    }
                                }
                            } else if (action == PURCHASE_HIST_METHOD) { //see purchase history
                                stringWriter.println(username);
                                stringWriter.flush();
                                printCustomerPurchaseHistory(stringReader, writer);
                            } else if (action == SORT_MARKET_METHOD) {
                                sortMarket(writer, stringReader);
                            } else if (action == VIEW_DASH_METHOD) {
                                stringWriter.println(username);
                                stringWriter.flush();
                                viewAndSortDashboard(writer, stringReader);
                            } else { //logout
                                break;
                            }
                        } while (true);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error! Cannot connect to the market!",
                        "Connection Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
    }

    public static void checkInvalidOptionError(int value) {
        if (value == CLOSED) {
            JOptionPane.showMessageDialog(null, "Please choose a valid option!",
                    "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void checkBlankStringError(String string, String item) {
        if (string == null || string.isEmpty()) {
            JOptionPane.showMessageDialog(null, item + " cannot be blank or empty!",
                    "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, "Error! " + message,
                "Error!", JOptionPane.ERROR_MESSAGE);
    }

    public static int sellerStoreAction() {
        int action;

        String[] options = {"Create Store", "Modify Store", "View Store Statistics", "View Products in Carts",
                "View Dashboard", "Export Products to CSV File", "Logout"};
        action = chooseFromDropdown("What would you like to do?", "Choose an Option", options);

        return action;
    }

    public static int chooseFromDropdown(String message, String title, String[] options) {
        String choice;
        int choiceIndex = CLOSED;

        do {
            choice = (String) JOptionPane.showInputDialog(null,
                    message, title, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (choice == null) {
                checkInvalidOptionError(choiceIndex);
            } else {
                for (int i = 0; i < options.length; i++) {
                    if (choice.equals(options[i])) {
                        choiceIndex = i;
                    }
                }
            }

        } while (choiceIndex == CLOSED);

        return choiceIndex;
    }

    public static void createStore(PrintWriter stringWriter, DataInputStream reader) {
        String storeName;
        boolean alreadyExists = false;
        try {
            do {
                alreadyExists = false;
                storeName = JOptionPane.showInputDialog(null,
                        "What would you like to name the store?", "Enter Store Name",
                        JOptionPane.QUESTION_MESSAGE);

                if (storeName == null || storeName.isEmpty()) {
                    checkBlankStringError(storeName, "Store name");
                } else {
                    stringWriter.println(storeName);
                    stringWriter.flush();
                    alreadyExists = reader.readBoolean();

                    if (alreadyExists) { //if not unique store name
                        showErrorMessage("Another store in the market has this name!\nPlease create a unique name!");
                    }
                }
            } while (storeName == null || storeName.isEmpty() || alreadyExists);
        } catch (Exception e) {
            showErrorMessage("Could not create store!");
            return;
        }
    }

    public static void modifyStore(DataInputStream reader, DataOutputStream writer, PrintWriter stringWriter, BufferedReader stringReader, String username) {
        int modifyStore;

        String[] options = {"Add Products to Store", "Modify Products in Store",
                "Remove Products From Store", "Return to Homepage"};
        modifyStore = chooseFromDropdown("What would you like to do?", "Select an Action", options) + 1;

        int ADD_PROD = 1;
        int MOD_PROD = 2;
        try {
            if (modifyStore == ADD_PROD) {
                writer.writeInt(ADD_PRODUCT_METHOD);
                writer.flush();
                stringWriter.println(username);
                stringWriter.flush();
                addProduct(reader, stringReader, stringWriter, writer); //adds a product to the store
            } else if (modifyStore == MOD_PROD) {
                writer.writeInt(MODIFY_PRODUCT_METHOD);
                writer.flush();
                stringWriter.println(username);
                stringWriter.flush();
                modifyProduct(reader, writer, stringReader, stringWriter);
            } else if (modifyStore == 3) {
                writer.writeInt(REMOVE_PRODUCT_METHOD);
                writer.flush();
                stringWriter.println(username);
                stringWriter.flush();
                removeProduct(reader, stringReader, stringWriter); //removes product from store
            }
        } catch (Exception e) {
            showErrorMessage("Cannot modify product!");
            return;
        }
    }

    public static void addProduct(DataInputStream reader, BufferedReader stringReader, PrintWriter stringWriter, DataOutputStream writer) {
        String prodName;
        int quantity;
        String description;
        double price;
        boolean dupProdName = false;
        int alrIn;

        try {
            int numberStores = reader.readInt();
            if (numberStores == 0) {
                showErrorMessage("You do not have any stores!");
                return;
            }

            getStoreDropdown("What store would you like to add a product to?", "Choose Store Name", numberStores, stringReader, stringWriter);

            int throughCSV;
            do {
                throughCSV = JOptionPane.showConfirmDialog(null,
                        "Would you like to add products through a CSV file?", "Select an Option",
                        JOptionPane.YES_NO_OPTION);
                checkInvalidOptionError(throughCSV);
            } while (throughCSV != JOptionPane.YES_OPTION && throughCSV != JOptionPane.NO_OPTION);

            writer.writeInt(throughCSV);
            writer.flush();

            if (throughCSV == JOptionPane.NO_OPTION) {
                do {
                    dupProdName = false;
                    prodName = JOptionPane.showInputDialog(null,
                            "What is the name of the product you want to add?", "Enter Product Name",
                            JOptionPane.QUESTION_MESSAGE);
                    if (prodName == null || prodName.isEmpty()) {
                        checkBlankStringError(prodName, "Product name");
                    } else {
                        stringWriter.println(prodName);
                        stringWriter.flush();

                        dupProdName = reader.readBoolean();

                        if (dupProdName) {
                            showErrorMessage("A product in that store already has that name!");
                        }
                    }
                } while (prodName == null || prodName.isEmpty() || dupProdName);

                do {
                    try {
                        String quantityString = JOptionPane.showInputDialog(null,
                                "How many products would you like to put on the market?",
                                "Enter Number of Products", JOptionPane.QUESTION_MESSAGE);

                        quantity = Integer.parseInt(quantityString);
                    } catch (Exception e) {
                        quantity = -1;
                    }

                    if (quantity < 1) {
                        showErrorMessage("Please enter a valid integer!\nAt least one product must be put on the market!");
                    }
                } while (quantity < 1);

                writer.writeInt(quantity);
                writer.flush();

                do {
                    description = JOptionPane.showInputDialog(null,
                            "What is the product description?", "Enter Product Description",
                            JOptionPane.QUESTION_MESSAGE);

                    if (description == null || description.isEmpty()) {
                        checkBlankStringError(description, "Product description");
                    }
                } while (description == null || description.isEmpty());

                stringWriter.println(description);
                stringWriter.flush();

                do {
                    try {
                        String priceString = JOptionPane.showInputDialog(null,
                                "For what price would you like to list the product?", "Enter Product Price",
                                JOptionPane.QUESTION_MESSAGE);

                        price = Double.parseDouble(priceString);
                    } catch (Exception e) {
                        price = -1;
                    }

                    if (price <= 0) {
                        showErrorMessage("Price must be a number greater than 0!");
                    }
                } while (price <= 0);

                writer.writeDouble(price);
                writer.flush();

                alrIn = reader.readInt();

                if (alrIn < 0) {
                    showErrorMessage("Product already exists in store!");
                }
            } else {
                String csvFilePath;
                do {
                    csvFilePath = JOptionPane.showInputDialog(null,
                            "Ensure the file is in the following format:\n" +
                                    "Name,Quantity,Price,Description,Store Name\n\n" +
                                    "What is the CSV File Path?", "Enter CSV File Path", JOptionPane.QUESTION_MESSAGE);
                    checkBlankStringError(csvFilePath, "CSV File Path");
                } while (csvFilePath == null || csvFilePath.isEmpty());
                stringWriter.println(csvFilePath);
                stringWriter.flush();

                int error = reader.readInt();
                if (error == -1) {
                    JOptionPane.showMessageDialog(null, "Error reading products from file!",
                            "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            showErrorMessage("Cannot add product!");
            return;
        }
    }

    public static void getStoreDropdown(String message, String title, int numStores, BufferedReader stringReader, PrintWriter stringWriter) {
        String[] storeNames = new String[numStores];
        String pickedStoreName;

        try {
            for (int i = 0; i < numStores; i++) {
                storeNames[i] = stringReader.readLine();
            }

            do {
                pickedStoreName = (String) JOptionPane.showInputDialog(null, message, title,
                        JOptionPane.QUESTION_MESSAGE, null, storeNames, storeNames[0]);

                if (pickedStoreName == null) {
                    checkInvalidOptionError(CLOSED);
                } else {
                    stringWriter.println(pickedStoreName);
                    stringWriter.flush();
                }

            } while (pickedStoreName == null);
        } catch (Exception e) {
            showErrorMessage("Cannot find store!");
            return;
        }
    }

    public static void modifyProduct(DataInputStream reader, DataOutputStream writer, BufferedReader stringReader, PrintWriter stringWriter) {
        int modification;
        String name;
        String description;
        int quantity;
        double price;
        boolean dupProdName = false;

        try {
            int numStores = reader.readInt();
            if (numStores == 0) {
                showErrorMessage("You do not have any stores!");
                return;
            }

            getStoreDropdown("In what store would you like to modify a product?", "Choose a Store", numStores, stringReader, stringWriter);

            int numProd = reader.readInt();
            if (numProd == 0) {
                showErrorMessage("You do not have any products in this store!");
                return;
            }

            getProductDropdown("What product would you like to modify?", "Choose a Product", numProd, stringReader, stringWriter);

            int MOD_NAME = 1;
            int MOD_DESCR = 2;
            int MOD_QUANTITY = 3;
            int MOD_PRICE = 4;

            String[] options = {"Product Name", "Product Description", "Quantity Available for Sale", "Sale Price"};
            modification = chooseFromDropdown("What would you like to change?",
                    "Select an Attribute to Change", options) + 1;

            writer.writeInt(modification);
            writer.flush();

            if (modification == MOD_NAME) {
                do {
                    name = JOptionPane.showInputDialog(null, "What is the new name of the product?",
                            "Enter the New Product Name", JOptionPane.QUESTION_MESSAGE);
                    checkBlankStringError(name, "Product name");

                    if (!(name == null || name.isEmpty())) {
                        stringWriter.println(name);
                        stringWriter.flush();

                        dupProdName = reader.readBoolean();

                        if (dupProdName) {
                            showErrorMessage("A product in that store already has that name!");
                        }
                    }
                } while (name == null || name.isEmpty() || dupProdName);
            } else if (modification == MOD_DESCR) {
                do {
                    description = JOptionPane.showInputDialog(null,
                            "What is the new product description?", "Enter Product Description",
                            JOptionPane.QUESTION_MESSAGE);
                    checkBlankStringError(description, "Product description");
                } while (description == null || description.isEmpty());
                stringWriter.println(description);
                stringWriter.flush();
            } else if (modification == MOD_QUANTITY) {
                do {
                    try {
                        String quantityString = JOptionPane.showInputDialog(null,
                                "How many would you like to put on the market?", "Enter Number of Products",
                                JOptionPane.QUESTION_MESSAGE);

                        quantity = Integer.parseInt(quantityString);
                    } catch (Exception e) {
                        quantity = 0;
                        showErrorMessage("Please enter a positive integer value greater than 0!");
                    }
                } while (quantity < 1);
                writer.writeInt(quantity);
                writer.flush();
            } else {
                String priceString;
                do {
                    try {
                        priceString = JOptionPane.showInputDialog(null, "What is the new sale price?",
                                "Enter Sale Price", JOptionPane.QUESTION_MESSAGE);
                        price = Double.parseDouble(priceString);

                        if (price <= 0) {
                            showErrorMessage("Price must be greater than 0!");
                        }
                    } catch (Exception e) {
                        price = -1;
                        showErrorMessage("Price must be a numeric value greater than 0!");
                    }
                } while (price <= 0);
                writer.writeDouble(price);
                writer.flush();
            }
        } catch (Exception e) {
            showErrorMessage("Cannot modify product!");
            return;
        }
    }

    public static void getProductDropdown(String message, String title, int numProd, BufferedReader stringReader, PrintWriter stringWriter) {
        String pickedProductName;
        String[] productNames = new String[numProd];

        try {
            for (int i = 0; i < numProd; i++) {
                productNames[i] = stringReader.readLine();
            }

            do {
                pickedProductName = (String) JOptionPane.showInputDialog(null, message, title,
                        JOptionPane.QUESTION_MESSAGE, null, productNames, productNames[0]);

                if (pickedProductName == null) {
                    checkInvalidOptionError(CLOSED);
                } else {
                    stringWriter.println(pickedProductName);
                    stringWriter.flush();
                }

            } while (pickedProductName == null);
        } catch (Exception e) {
            showErrorMessage("Cannot find product!");
            return;
        }
    }

    public static void removeProduct(DataInputStream reader, BufferedReader stringReader, PrintWriter stringWriter) {
        try {
            int numStores = reader.readInt();
            if (numStores == 0) {
                showErrorMessage("You do not have any stores!");
                return;
            }

            getStoreDropdown("From what store would you like to remove a product?", "Choose a Store", numStores, stringReader, stringWriter);

            int numProd = reader.readInt();
            if (numProd == 0) {
                showErrorMessage("You do not have any products in this store!");
                return;
            }

            getProductDropdown("What is the name of the product you would like to remove?", "Choose Product to Remove", numProd, stringReader, stringWriter);

            int error = reader.readInt();
            if (error == -1) {
                JOptionPane.showMessageDialog(null, "Error! Product does not exist in the store!",
                        "Error!", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Product removed!", "Removed!",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            showErrorMessage("Cannot remove product!");
            return;
        }
    }

    public static void sortPrintSellerDashboard(DataOutputStream writer, BufferedReader stringReader) {
        int sort;

        try {
            do {
                sort = JOptionPane.showConfirmDialog(null, "Would you like to sort the dashboard?",
                        "Sort Dashboard", JOptionPane.YES_NO_OPTION);

                checkInvalidOptionError(sort);
            } while (sort == CLOSED);

            if (sort == JOptionPane.YES_OPTION) { //if the user wants to sort the dashboard

                String[] options = {"Alphabetically", "By Store Sales"};
                sort = chooseFromDropdown("How would you like to sort the dashboard?", "Sorting Method",
                        options);
            } else { //if the user does not want to sort
                sort = -1;
            }
            writer.writeInt(sort);
            writer.flush();

            String dashboard = stringReader.readLine();
            dashboard = replaceTilda(dashboard);
            JOptionPane.showMessageDialog(null, dashboard, "Seller Dashboard",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showErrorMessage("Cannot sort the dashboard!");
            return;
        }
    }

    public static void exportProductToCSV(BufferedReader stringReader) {
        String path;

        do {
            path = JOptionPane.showInputDialog(null, "What file would you like to export to?",
                    "Enter a File", JOptionPane.QUESTION_MESSAGE);
            checkBlankStringError(path, "File path");
        } while (path == null || path.isEmpty());

        try {
            FileWriter fileWriter = new FileWriter(path);

            fileWriter.append("Name,Quantity,Price,Description,Store Name");
            fileWriter.append("\n");

            String lines = stringReader.readLine();
            lines = replaceTilda(lines);
            fileWriter.append(lines);
            fileWriter.append("\n");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Cannot write products to file!");
        }
    }

    public static int getCustomerAction() {
        int action;

        String[] options = {"View the Marketplace", "Search the Marketplace", "View Shopping Cart",
                "See Purchase History", "Sort Marketplace", "View Dashboard", "Logout"};
        action = chooseFromDropdown("What would you like to do?", "Select an Action", options) + 16;

        return action;
    }

    public static int learnMore() {
        int learnMore;
        do {
            learnMore = JOptionPane.showConfirmDialog(null,
                    "Do you want to learn more about a product?", "Enter Product Page",
                    JOptionPane.YES_NO_OPTION);

            checkInvalidOptionError(learnMore);
        } while (learnMore == CLOSED);

        return learnMore;
    }

    public static void addToShoppingCart(DataOutputStream writer) {
        String quantityString;
        int quantity;

        do {
            try {
                quantityString = JOptionPane.showInputDialog(null,
                        "How many would you like to add to your cart?", "Enter Number of Products",
                        JOptionPane.QUESTION_MESSAGE);

                quantity = Integer.parseInt(quantityString);

                if (quantity > 0) {
                    writer.writeInt(quantity);
                    writer.flush();

                    JOptionPane.showMessageDialog(null, quantity +
                                    " items successfully added to cart!", "Added to Cart",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showErrorMessage("Enter an integer number greater than 0 of products to add to cart!");
                }
            } catch (Exception e) {
                quantity = -1;
                showErrorMessage("Enter an integer number greater than 0 of products to add to cart!");
            }
        } while (quantity < 1);
    }

    public static void searchMarketplace(PrintWriter stringWriter, BufferedReader stringReader) {
        String searchQuery;
        do {
            searchQuery = JOptionPane.showInputDialog(null, "What would you like to search?",
                    "Enter Your Search", JOptionPane.QUESTION_MESSAGE);
            checkBlankStringError(searchQuery, "Search");
        } while (searchQuery == null || searchQuery.isEmpty());

        try {
            stringWriter.println(searchQuery);
            stringWriter.flush();

            String searchResults = stringReader.readLine();
            searchResults = replaceTilda(searchResults);

            JOptionPane.showMessageDialog(null, searchResults, "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showErrorMessage("Cannot search!");
        }
    }

    public static int getCartAction() {
        int cartAction;

        String[] options = {"Remove Items From Cart", "Purchase Items in Cart", "Return"};
        cartAction = chooseFromDropdown("What would you like to do?", "Choose a Cart Action", options);

        return cartAction;
    }

    public static void removeCartItem(DataInputStream reader, BufferedReader stringReader, PrintWriter stringWriter) {
        try {
            int numProducts = reader.readInt();
            String[] cartProductNames = new String[numProducts];

            for (int i = 0; i < numProducts; i++) {
                cartProductNames[i] = stringReader.readLine();
            }

            int choiceIndex = chooseFromDropdown("What item would you like to remove from the shopping cart?",
                    "Pick Item to Remove", cartProductNames);
            String productName = cartProductNames[choiceIndex];

            stringWriter.println(productName);
            stringWriter.flush();
        } catch (Exception e) {
            showErrorMessage("Cannot remove item from cart!");
            return;
        }
    }

    public static void printCustomerPurchaseHistory(BufferedReader stringReader, DataOutputStream writer) {
        try {
            int export;
            String purchaseHistory = stringReader.readLine();
            purchaseHistory = replaceTilda(purchaseHistory);
            JOptionPane.showMessageDialog(null, purchaseHistory, "Purchase History",
                    JOptionPane.INFORMATION_MESSAGE);

            do {
                export = JOptionPane.showConfirmDialog(null,
                        "Would you like to export your purchase history to a file?",
                        "Export Purchase History", JOptionPane.YES_NO_OPTION);

                checkInvalidOptionError(export);
            } while (export == CLOSED);

            writer.writeInt(export);
            writer.flush();

            if (export == JOptionPane.YES_OPTION) {
                try {
                    String fileContent = stringReader.readLine();
                    fileContent = replaceTilda(fileContent);
                    try {
                        try (FileWriter fileWriter = new FileWriter("purchaseHistory.txt")) {
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            PrintWriter printWriter = new PrintWriter(bufferedWriter);
                            printWriter.println(fileContent);
                            printWriter.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    showErrorMessage("Cannot export purchase history into a file!");
                }
            }
        } catch (Exception e) {
            showErrorMessage("Cannot get purchase history!");
        }
    }

    public static void sortMarket(DataOutputStream writer, BufferedReader stringReader) {
        ArrayList<Product> sortedProducts = new ArrayList<>();
        String[] options = {"Product Price High to Low", "Product Price Low to High",
                "Available Products High to Low", "Available Products Low to High"};
        int choice = chooseFromDropdown("How would you like to sort the marketplace?",
                "Choose Sorting Method", options) + 1;

        try {
            writer.writeInt(choice);
            writer.flush();

            if (choice == 1) {
                String sortedMarket = stringReader.readLine();
                sortedMarket = replaceTilda(sortedMarket);
                JOptionPane.showMessageDialog(null, sortedMarket, "Product Price - High to Low",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (choice == 2) {
                String sortedMarket = stringReader.readLine();
                sortedMarket = replaceTilda(sortedMarket);
                JOptionPane.showMessageDialog(null, sortedMarket, "Product Price - Low to High",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (choice == 3) {
                String sortedMarket = stringReader.readLine();
                sortedMarket = replaceTilda(sortedMarket);
                JOptionPane.showMessageDialog(null, sortedMarket, "Available Products - High to Low",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                String sortedMarket = stringReader.readLine();
                sortedMarket = replaceTilda(sortedMarket);
                JOptionPane.showMessageDialog(null, sortedMarket, "Available Products - Low to High",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            showErrorMessage("Cannot sort market!");
            return;
        }
    }

    public static void viewAndSortDashboard(DataOutputStream writer, BufferedReader stringReader) {
        String[] options = {"Stores you most Frequently Purchase From", "Stores with the Most Sales"};
        int choice = chooseFromDropdown("How would you like to sort the dashboard?", "Pick Sorting Method",
                options) + 1;

        try {
            writer.writeInt(choice);
            writer.flush();

            if (choice == 1) {
                String customerDash = stringReader.readLine();
                customerDash = replaceTilda(customerDash);
                JOptionPane.showMessageDialog(null, customerDash, "Most Frequent Store Purchases",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                String customerDash = stringReader.readLine();
                customerDash = replaceTilda(customerDash);
                JOptionPane.showMessageDialog(null, customerDash, "Stores with Most Sales",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            showErrorMessage("Cannot sort the dashboard!");
        }

    }

    public static String replaceTilda(String string) {
        int tildaLoc;
        String stringLine;
        String modString = "";
        int stringLength;

        if (string.length() != 0) {
            do {
                tildaLoc = string.indexOf('~');
                stringLength = string.length();

                if (tildaLoc != -1 && tildaLoc < stringLength - 1) {
                    stringLine = string.substring(0, tildaLoc);
                    modString += stringLine + "\n";
                    string = string.substring(tildaLoc + 1);
                } else {
                    modString += string;
                }
            } while (tildaLoc != -1 && tildaLoc < stringLength - 1);

            if (modString.charAt(modString.length() - 1) == '~') {
                modString = modString.substring(0, modString.length() - 1);
            }
        }

        return modString;
    }
}
