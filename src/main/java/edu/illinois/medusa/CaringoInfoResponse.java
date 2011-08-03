package edu.illinois.medusa;

import com.caringo.client.ScspResponse;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/18/11
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoInfoResponse extends CaringoAbstractResponse {

    protected CaringoInfoResponse(ScspResponse response) {
        super(response);
    }

    public long contentLength() {
       String contentLength = response.getResponseHeaders().getHeaderValues("Content-Length").get(0);
       return Long.parseLong(contentLength);
    }

}
