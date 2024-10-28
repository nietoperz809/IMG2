package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1770";
    private static final String BUILD_DATE = "10/28/2024 06:40:38 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
