package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1744";
    private static final String BUILD_DATE = "10/26/2024 11:23:25 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
