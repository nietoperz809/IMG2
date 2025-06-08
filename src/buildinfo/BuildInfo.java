package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3484";
    private static final String BUILD_DATE = "06/08/2025 03:19:14 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
