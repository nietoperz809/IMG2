package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "817";
    private static final String BUILD_DATE = "09/18/2023 07:17:16 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
