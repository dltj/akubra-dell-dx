package edu.illinois.medusa;

import com.caringo.client.ScspResponse;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/18/11
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CaringoAbstractResponse {


    protected ScspResponse response;

    protected CaringoAbstractResponse(ScspResponse response) {
        this.response = response;
    }

    public int status() {
        return response.getHttpStatusCode();
    }

    public boolean ok() {
        return this.status() == 200;
    }

    public boolean notFound() {
        return this.status() == 404;
    }

    public boolean created() {
        return this.status() == 201;
    }

}
