package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "931";
    private static final String BUILD_DATE = "09/23/2023 08:02:42 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
