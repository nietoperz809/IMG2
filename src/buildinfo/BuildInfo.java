package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1614";
    private static final String BUILD_DATE = "10/16/2024 02:38:45 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
