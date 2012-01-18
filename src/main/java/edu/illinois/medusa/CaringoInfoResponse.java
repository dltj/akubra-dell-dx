package edu.illinois.medusa;

import com.caringo.client.ScspResponse;

/**
 * Wrapper for response to info request to Caringo Storage
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class CaringoInfoResponse extends CaringoAbstractResponse {

    /**
     * Construct from an ScspResponse
     *
     * @param response An Scsp Response to wrap
     */
    protected CaringoInfoResponse(ScspResponse response) {
        super(response);
    }

    /**
     * Length of object as reported in info response
     * @return Length of object in bytes
     */
    public long contentLength() {
       String contentLength = response.getResponseHeaders().getHeaderValues("Content-Length").get(0);
       return Long.parseLong(contentLength);
    }

}
