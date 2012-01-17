package edu.illinois.medusa;

import com.caringo.client.ScspResponse;

/**
 * Wraps the ScspResponse returned from an interaction with a Caringo server, adding some convenience methods
 * along with access to the ScspResponse itself if needed.
 *
 * @author Howard Ding <hding2@illinois.edu>
 */

public abstract class CaringoAbstractResponse {

    /**
     * The response from the Caringo server
     */
    protected ScspResponse response;

    /**
     * Construct from an ScspResponse
     * @param response The Scsp Response to be wrapped
     */
    protected CaringoAbstractResponse(ScspResponse response) {
        this.response = response;
    }

    /**
     * Get the HTTP status of the response
     * @return The integer status code of the Caringo response
     */
    public int status() {
        return response.getHttpStatusCode();
    }

    /**
     * Get whether the response returned ok (200)
     * @return Whether the status was ok (200).
     */
    public boolean ok() {
        return this.status() == 200;
    }

    /**
     * Get whether the response returned not found (404)
     * @return Whether the status was not found (404)
     */
    public boolean notFound() {
        return this.status() == 404;
    }

    /**
     * Get whether the response returned created (201)
     * @return Whether the status was created (201)
     */
    public boolean created() {
        return this.status() == 201;
    }

    /**
     * Get the wrapped response
     * @return The wrapped ScspResponse
     */
    public ScspResponse scspResponse() {
        return this.response;
    }
}
