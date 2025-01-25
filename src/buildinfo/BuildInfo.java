package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2281";
    private static final String BUILD_DATE = "01/25/2025 09:50:56 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
