package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1059";
    private static final String BUILD_DATE = "10/07/2023 05:37:12 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
