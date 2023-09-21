package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "872";
    private static final String BUILD_DATE = "09/21/2023 10:47:34 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
