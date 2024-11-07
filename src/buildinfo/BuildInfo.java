package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1906";
    private static final String BUILD_DATE = "11/07/2024 11:58:57 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
