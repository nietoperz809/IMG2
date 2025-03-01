package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2511";
    private static final String BUILD_DATE = "03/01/2025 11:08:33 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
