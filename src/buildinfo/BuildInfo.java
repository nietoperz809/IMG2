package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2004";
    private static final String BUILD_DATE = "11/14/2024 05:24:30 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
