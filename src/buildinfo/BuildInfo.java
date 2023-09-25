package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1016";
    private static final String BUILD_DATE = "09/25/2023 09:53:22 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
