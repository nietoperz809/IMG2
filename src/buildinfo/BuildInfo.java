package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2011";
    private static final String BUILD_DATE = "11/14/2024 09:21:05 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
