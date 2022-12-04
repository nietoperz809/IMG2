package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "76";
    private static final String BUILD_DATE = "12/04/2022 01:25:14 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
