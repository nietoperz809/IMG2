package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "285";
    private static final String BUILD_DATE = "12/11/2022 01:23:28 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
