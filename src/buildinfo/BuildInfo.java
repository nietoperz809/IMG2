package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "673";
    private static final String BUILD_DATE = "08/25/2023 11:37:03 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
