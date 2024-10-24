package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1714";
    private static final String BUILD_DATE = "10/24/2024 05:29:40 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
