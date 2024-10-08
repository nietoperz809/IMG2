package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1491";
    private static final String BUILD_DATE = "10/08/2024 05:46:02 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
