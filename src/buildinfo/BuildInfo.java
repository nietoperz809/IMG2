package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "335";
    private static final String BUILD_DATE = "12/13/2022 03:39:15 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
