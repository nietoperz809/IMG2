package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3030";
    private static final String BUILD_DATE = "04/05/2025 05:52:37 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
