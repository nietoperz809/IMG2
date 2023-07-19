package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "594";
    private static final String BUILD_DATE = "07/19/2023 03:27:21 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
