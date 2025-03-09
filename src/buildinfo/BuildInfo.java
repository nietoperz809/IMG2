package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2662";
    private static final String BUILD_DATE = "03/09/2025 05:24:14 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
