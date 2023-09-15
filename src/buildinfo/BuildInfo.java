package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "793";
    private static final String BUILD_DATE = "09/15/2023 07:59:12 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
