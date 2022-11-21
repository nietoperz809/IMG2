package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "18";
    private static final String BUILD_DATE = "11/21/2022 10:31:08 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
