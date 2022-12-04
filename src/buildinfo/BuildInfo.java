package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "87";
    private static final String BUILD_DATE = "12/04/2022 04:11:05 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
