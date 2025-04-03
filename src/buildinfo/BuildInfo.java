package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2939";
    private static final String BUILD_DATE = "04/03/2025 03:52:34 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
