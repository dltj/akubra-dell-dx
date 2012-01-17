package edu.illinois.medusa;

import com.caringo.client.ScspAuthentication;

/**
 * Simple class to hold authentication information to Caringo server - user, password, and security realm.
 *
 * @author Howard Ding - hding2@illinois.edu.
 */
public class CaringoConfigAuthentication {

    /**
     * User for Caringo authentication
     */
    protected String user;
    /**
     * Password for Caringo authentication
     */
    protected String password;
    /**
     * Security realm for Caringo authentication
     */
    protected String realm;

    /**
     * Constructor - just store arguments
     * @param user
     * @param password
     * @param realm
     */
    protected CaringoConfigAuthentication(String user, String password, String realm) {
        this.user = user;
        this.password = password;
        this.realm = realm;
    }

    /**
     * Create authentication object to be used with Caringo requests
     * @return ScspAuthentication reflecting configuration in this object.
     */
    protected ScspAuthentication scspAuth() {
        return new ScspAuthentication(this.user, this.password, this.realm, "");
    }
}
