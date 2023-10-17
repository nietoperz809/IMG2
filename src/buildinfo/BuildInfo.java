package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1150";
    private static final String BUILD_DATE = "10/18/2023 01:37:38 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
