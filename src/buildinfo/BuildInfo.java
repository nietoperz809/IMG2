package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "660";
    private static final String BUILD_DATE = "08/15/2023 05:36:00 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
