package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2614";
    private static final String BUILD_DATE = "03/06/2025 05:18:02 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
