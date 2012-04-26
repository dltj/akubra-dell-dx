package edu.illinois.medusa;

/**
 * Created with IntelliJ IDEA.
 * User: hading
 * Date: 4/26/12
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class AkubraPlugin {
    public static final String MAJOR_VERSION = "1";
    public static final String MINOR_VERSION = "0";
    public static final String REVISION = "0";

    public static final String dellCreator = "Akubra";

    public static String dellCreatorVersion() {
        return MAJOR_VERSION + ";" + MINOR_VERSION + ";" + REVISION;
    }
}
