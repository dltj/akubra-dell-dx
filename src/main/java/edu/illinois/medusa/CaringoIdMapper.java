package edu.illinois.medusa;

import org.akubraproject.map.IdMapper;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: hading
 * Date: 8/3/11
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */

/* for now this is a completely trivial implementation */
public class CaringoIdMapper implements IdMapper {

    @Override
    public URI getExternalId(URI internalId) throws NullPointerException {
        if (internalId == null)
            throw new NullPointerException();
        else
            return internalId;
    }

    @Override
    public URI getInternalId(URI externalId) throws NullPointerException {
        if (externalId == null)
            throw new NullPointerException();
        else
            return externalId;
    }

    @Override
    public String getInternalPrefix(String externalPrefix) throws NullPointerException {
        if (externalPrefix == null)
            throw new NullPointerException();
        else
            return externalPrefix;
    }
}
