package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "399";
    private static final String BUILD_DATE = "01/11/2023 02:09:50 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
