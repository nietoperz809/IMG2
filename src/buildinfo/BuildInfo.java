package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1893";
    private static final String BUILD_DATE = "11/04/2024 02:06:19 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
