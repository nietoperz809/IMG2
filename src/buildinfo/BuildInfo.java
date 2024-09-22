package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1415";
    private static final String BUILD_DATE = "09/22/2024 09:23:41 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
