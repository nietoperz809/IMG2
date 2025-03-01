package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2502";
    private static final String BUILD_DATE = "03/01/2025 06:46:16 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
