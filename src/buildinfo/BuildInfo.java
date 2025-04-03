package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2968";
    private static final String BUILD_DATE = "04/03/2025 07:19:14 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
