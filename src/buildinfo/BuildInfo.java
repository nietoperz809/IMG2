package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "476";
    private static final String BUILD_DATE = "04/17/2023 03:36:21 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
