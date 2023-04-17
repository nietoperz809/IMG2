package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "504";
    private static final String BUILD_DATE = "04/17/2023 07:29:49 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
