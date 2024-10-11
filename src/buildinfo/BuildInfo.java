package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1569";
    private static final String BUILD_DATE = "10/11/2024 05:45:18 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
