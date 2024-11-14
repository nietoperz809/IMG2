package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1993";
    private static final String BUILD_DATE = "11/14/2024 01:33:42 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
