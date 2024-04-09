import java.io.*;
/**
 * User
 *
 * This class represents a generic User in the online marketplace, providing a base for the Seller and Customer classes.
 *
 *
 */
public class User implements Serializable {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
