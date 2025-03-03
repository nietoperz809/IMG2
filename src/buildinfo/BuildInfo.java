package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2598";
    private static final String BUILD_DATE = "03/03/2025 08:46:51 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
