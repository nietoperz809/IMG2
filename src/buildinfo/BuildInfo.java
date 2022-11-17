package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "6";
    private static final String BUILD_DATE = "11/17/2022 09:03:12 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
