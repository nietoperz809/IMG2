package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2032";
    private static final String BUILD_DATE = "11/20/2024 01:53:39 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
