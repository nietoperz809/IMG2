package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2089";
    private static final String BUILD_DATE = "01/19/2025 04:47:12 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
