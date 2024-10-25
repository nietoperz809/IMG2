package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1718";
    private static final String BUILD_DATE = "10/25/2024 06:54:44 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
