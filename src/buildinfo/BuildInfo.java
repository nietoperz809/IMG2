package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "254";
    private static final String BUILD_DATE = "12/10/2022 03:20:35 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
