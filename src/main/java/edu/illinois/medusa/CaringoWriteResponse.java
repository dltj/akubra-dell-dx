package edu.illinois.medusa;

import com.caringo.client.ScspResponse;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 7/18/11
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaringoWriteResponse extends CaringoAbstractResponse {

    protected CaringoWriteResponse(ScspResponse response) {
        super(response);
    }

}
