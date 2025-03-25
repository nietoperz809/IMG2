package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2882";
    private static final String BUILD_DATE = "03/25/2025 10:12:43 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
