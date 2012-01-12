package edu.illinois.medusa;

import com.caringo.client.ScspAuthentication;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 1/12/12
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoConfigAuthentication {

    protected String user;
    protected String password;
    protected String realm;

    protected CaringoConfigAuthentication(String user, String password, String realm) {
        this.user = user;
        this.password = password;
        this.realm = realm;
    }

    protected ScspAuthentication scspAuth() {
        return new ScspAuthentication(this.user, this.password, this.realm, "");
    }
}
