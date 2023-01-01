package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "390";
    private static final String BUILD_DATE = "01/01/2023 07:47:00 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
