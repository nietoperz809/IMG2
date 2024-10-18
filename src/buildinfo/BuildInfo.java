package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1639";
    private static final String BUILD_DATE = "10/18/2024 02:38:39 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
