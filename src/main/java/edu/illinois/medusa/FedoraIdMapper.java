package edu.illinois.medusa;

import org.akubraproject.map.IdMapper;

import java.net.URI;

/**
 * This is a completely trivial implementation of the Akubra IdMapper, which is to say that it makes
 * no changes to the IDs. This is sufficient for use with Fedora, which (as far as I can tell) uses no
 * IDs that need any quoting. Moreover at this point I feel that maintaining the Fedora IDs as is is more
 * important than being totally general for Caringo.
 *
 * @author Howard Ding - hding2@illinois.edu
 */

public class FedoraIdMapper implements IdMapper {

    /**
     * Return the external id corresponding to the provided internal id
     *
     * @param internalId Internal Id
     * @return External Id
     * @throws NullPointerException If internal id is null
     */
    public URI getExternalId(URI internalId) throws NullPointerException {
        if (internalId == null)
            throw new NullPointerException();
        else
            return unquoteId(internalId);
    }

    /**
     * Return the internal id corresponding to the provided external id
     *
     * @param externalId External Id
     * @return Internal Id
     * @throws NullPointerException If external id is null
     */
    public URI getInternalId(URI externalId) throws NullPointerException {
        if (externalId == null)
            throw new NullPointerException();
        else
            return quoteId(externalId);
    }

    public String getInternalPrefix(String externalPrefix) throws NullPointerException {
        if (externalPrefix == null)
            throw new NullPointerException();
        else
            return externalPrefix;
    }

    /**
     * Quote the provided id. Trivial for this IdMapper.
     *
     * @param id Id to quote
     * @return Quoted Id
     */
    private URI quoteId(URI id) {
        return id;
    }

    /**
     * Unquote the provided id. Trivial for this IdMapper.
     *
     * @param id Id to unquote
     * @return Unquoted Id
     */
    private URI unquoteId(URI id) {
        return id;
    }
}
