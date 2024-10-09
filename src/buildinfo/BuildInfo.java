package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1517";
    private static final String BUILD_DATE = "10/09/2024 02:37:08 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
