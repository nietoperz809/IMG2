package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3191";
    private static final String BUILD_DATE = "04/14/2025 05:27:08 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
