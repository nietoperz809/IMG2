package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2114";
    private static final String BUILD_DATE = "01/21/2025 11:05:25 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
