package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1591";
    private static final String BUILD_DATE = "10/14/2024 07:11:23 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
