package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1774";
    private static final String BUILD_DATE = "10/28/2024 05:10:38 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
