package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "641";
    private static final String BUILD_DATE = "08/05/2023 04:27:58 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
