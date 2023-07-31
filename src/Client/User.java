package Client;

import java.util.ArrayList;

public class User {
    public String fullName;
    public int n;
    public int publicKey;
    public int privateKey;

    public User() {

    }

    public User(String fullName, int n, int publicKey, int privateKey) {
        this.fullName = fullName;
        this.n = n;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}