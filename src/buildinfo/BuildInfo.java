package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "425";
    private static final String BUILD_DATE = "04/09/2023 06:43:47 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
