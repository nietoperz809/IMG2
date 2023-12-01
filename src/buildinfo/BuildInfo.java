package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1230";
    private static final String BUILD_DATE = "12/01/2023 11:51:37 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
