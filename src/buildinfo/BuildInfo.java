package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1845";
    private static final String BUILD_DATE = "11/01/2024 08:09:46 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
