import javax.swing.*;
import java.util.ArrayList;

/**
 * Online Marketplace
 *
 * This class represents a Seller in the online marketplace. It extends the User class and
 * provides additional functionalities such as adding and removing stores, viewing sales,
 * and printing the seller dashboard.
 *
 */
public class Seller extends User {

    private final int ALPHABETICAL_SORT = 0;
    private final int BEST_SELLING_SORT = 1;
    private ArrayList<Store> stores;

    public Seller(String username, String password) {
        super(username, password);
        stores = new ArrayList<Store>();
    }

    public void addStore(Store store) {
        this.stores.add(store);
    }

    public void removeStore(Store store) {
        this.stores.remove(store);
    }

    public ArrayList<Store> getStores() {
        return stores;
    }

    public String viewSales() {
        String saleHistory = "";
        for (Store store : stores) {
            saleHistory += store.printSaleHistory();
        }

        return saleHistory;
    }

    public Store findStore(String storeName) {
        Store store = null;
        for (Store value : stores) {
            if (storeName.equals(value.getStoreName())) {
                store = value;
            }
        }

        return store;
    }

    public String printSellerStores() {
        String storesString = "";
        for (Store store : stores) {
            storesString += store.printStore();
        }

        return storesString;
    }

    //prints the seller dashboard, sorted alphabetically or numerically, if desired
    public String printSellerDashboard(int sortMethod) {
        String dashboard = "";
        if (sortMethod == ALPHABETICAL_SORT) {
            ArrayList<Store> sortedStores = sortAlphabetically();
            for (int i = 0; i < getStores().size(); i++) {
                dashboard += sortedStores.get(i).printProdSaleDashboard();
                dashboard += sortedStores.get(i).printBuyerStatDashboard();
            }
        } else if (sortMethod == BEST_SELLING_SORT) {
            ArrayList<Store> sortedStores = sortBestSelling();
            for (int i = 0; i < getStores().size(); i++) {
                dashboard += sortedStores.get(i).printProdSaleDashboard();
                dashboard += sortedStores.get(i).printBuyerStatDashboard();
            }

        } else {
            for (int i = 0; i < getStores().size(); i++) {
                dashboard += getStores().get(i).printProdSaleDashboard();
                dashboard += getStores().get(i).printBuyerStatDashboard();
            }
        }

        return dashboard;
    }

    //sorts seller dashboard numerically by stores with most products sold
    public ArrayList<Store> sortBestSelling() {
        ArrayList<Store> sortedStores = getStores();

        for (int i = 0; i < sortedStores.size(); i++) {
            for (int j = 0; j < sortedStores.size(); j++) {
                if (sortedStores.get(i).getTotalProductsSold() > sortedStores.get(j).getTotalProductsSold()) {
                    Store temp = sortedStores.get(j);
                    sortedStores.set(j, sortedStores.get(i));
                    sortedStores.set(i, temp);
                }
            }
        }

        return sortedStores;
    }

    //sorts sellers dashboard alphabetically by store name
    public ArrayList<Store> sortAlphabetically() {
        ArrayList<Store> sortedStores = getStores();

        for (int i = 0; i < sortedStores.size(); i++) {
            for (int j = 0; j < sortedStores.size(); j++) {
                if (sortedStores.get(j).getStoreName().compareToIgnoreCase(sortedStores.get(i).getStoreName()) > 0) {
                    Store temp = sortedStores.get(j);
                    sortedStores.set(j, sortedStores.get(i));
                    sortedStores.set(i, temp);
                }
            }
        }

        return sortedStores;
    }
}
