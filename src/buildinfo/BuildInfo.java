package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2410";
    private static final String BUILD_DATE = "02/13/2025 09:14:59 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
