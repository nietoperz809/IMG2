package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1304";
    private static final String BUILD_DATE = "01/29/2024 08:14:16 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
