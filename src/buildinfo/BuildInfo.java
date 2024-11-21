package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2040";
    private static final String BUILD_DATE = "11/21/2024 04:34:00 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
