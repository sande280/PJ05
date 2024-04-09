import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.*;
import javax.swing.*;

/**
 * Market
 *
 * This class represents a Market in the online marketplace, which contains a list of stores.
 *
 *
 */
public class Market implements Serializable {

    public ArrayList<Seller> sellers = new ArrayList<Seller>(); //list of sellers in the market
    public ArrayList<Customer> customers = new ArrayList<Customer>(); //list of customers in market
    
}