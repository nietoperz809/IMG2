package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "633";
    private static final String BUILD_DATE = "07/31/2023 11:31:12 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
