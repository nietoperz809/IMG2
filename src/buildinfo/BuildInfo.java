package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1657";
    private static final String BUILD_DATE = "10/20/2024 04:44:52 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
