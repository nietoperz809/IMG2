package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1409";
    private static final String BUILD_DATE = "09/21/2024 03:53:04 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
