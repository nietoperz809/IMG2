package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "436";
    private static final String BUILD_DATE = "04/15/2023 01:42:39 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
