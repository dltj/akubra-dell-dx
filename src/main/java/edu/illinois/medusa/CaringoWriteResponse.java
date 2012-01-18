package edu.illinois.medusa;

import com.caringo.client.ScspResponse;

/**
 * Wrapper for response from write to Caringo storage
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoWriteResponse extends CaringoAbstractResponse {

    /**
     * Construct, wrapping the response to a write request
     * @param response ScspResponse to be wrapped
     */
    protected CaringoWriteResponse(ScspResponse response) {
        super(response);
    }

}
