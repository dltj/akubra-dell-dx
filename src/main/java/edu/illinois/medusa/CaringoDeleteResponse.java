package edu.illinois.medusa;

import com.caringo.client.ScspResponse;

/**
 * Wrapper for response to a delete request to Caringo storage
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoDeleteResponse extends CaringoAbstractResponse {
    protected CaringoDeleteResponse(ScspResponse response) {
        super(response);
    }
}
