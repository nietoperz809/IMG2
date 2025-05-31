package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3383";
    private static final String BUILD_DATE = "05/31/2025 10:03:19 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
