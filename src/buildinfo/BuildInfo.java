package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1473";
    private static final String BUILD_DATE = "10/06/2024 10:04:52 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
