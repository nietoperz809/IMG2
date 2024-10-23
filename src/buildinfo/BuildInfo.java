package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1710";
    private static final String BUILD_DATE = "10/23/2024 11:47:17 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
