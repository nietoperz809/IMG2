package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1689";
    private static final String BUILD_DATE = "10/23/2024 09:25:42 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
