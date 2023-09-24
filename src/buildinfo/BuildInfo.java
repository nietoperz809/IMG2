package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "957";
    private static final String BUILD_DATE = "09/24/2023 03:34:04 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
