package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1119";
    private static final String BUILD_DATE = "10/13/2023 02:32:26 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
