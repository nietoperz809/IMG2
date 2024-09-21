package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1405";
    private static final String BUILD_DATE = "09/21/2024 01:52:31 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
