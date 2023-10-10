package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1076";
    private static final String BUILD_DATE = "10/10/2023 05:24:26 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
