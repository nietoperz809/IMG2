package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "362";
    private static final String BUILD_DATE = "12/21/2022 10:51:04 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
