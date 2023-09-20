package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "823";
    private static final String BUILD_DATE = "09/20/2023 05:21:19 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
