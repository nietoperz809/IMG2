package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3305";
    private static final String BUILD_DATE = "04/27/2025 07:35:42 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
