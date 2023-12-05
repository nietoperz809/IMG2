package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1254";
    private static final String BUILD_DATE = "12/05/2023 07:12:15 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
