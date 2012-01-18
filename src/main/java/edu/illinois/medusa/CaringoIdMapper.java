package edu.illinois.medusa;

import org.akubraproject.map.IdMapper;

import java.net.URI;

/**
 * This is a completely trivial implementation of the Akubra IdMapper, which is to say that it makes
 * no changes to the IDs. This is sufficient for use with Fedora, which (as far as I can tell) uses no
 * IDs that need any quoting. Moreover at this point I feel that maintaining the Fedora IDs as is is more
 * important than being totally general for Caringo. Both are achievable, but perhaps not trivially (e.g.
 * I think URLencoding/decoding would make all strings okay for Caringo, but it would quote things that
 * I wouldn't necessarily want quoted).
 *
 * At some point we may revisit this class and make it do both things, but that time is not right now.
 *
 * @author Howard Ding - hding2@illinois.edu
 */

public class CaringoIdMapper implements IdMapper {

    public URI getExternalId(URI internalId) throws NullPointerException {
        if (internalId == null)
            throw new NullPointerException();
        else
            return internalId;
    }

    public URI getInternalId(URI externalId) throws NullPointerException {
        if (externalId == null)
            throw new NullPointerException();
        else
            return externalId;
    }

    public String getInternalPrefix(String externalPrefix) throws NullPointerException {
        if (externalPrefix == null)
            throw new NullPointerException();
        else
            return externalPrefix;
    }
}
