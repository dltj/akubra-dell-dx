package edu.illinois.medusa;

/**
 * Global information about the plugin. Used to populate some Dell requested headers on objects.
 *
 * @author Howard Ding - hding2@illinois.edu
 */
public class AkubraPlugin {

    /**
     * Major version of the plugin
     */
    public static final String MAJOR_VERSION = "1";
    /**
     * Minor version of the plugin
     */
    public static final String MINOR_VERSION = "0";
    /**
     * Revision number of the plugin
     */
    public static final String REVISION = "0";

    /**
     * String describing the plugin as the actual creator of an object in storage
     */
    public static final String dellCreator = "Akubra";

    /**
     *
     * @return - full string concatenating version information, formatted per Dell guidelines.
     */
    public static String dellCreatorVersion() {
        return MAJOR_VERSION + ";" + MINOR_VERSION + ";" + REVISION;
    }
}
