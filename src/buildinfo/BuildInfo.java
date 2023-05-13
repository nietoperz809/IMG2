package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "580";
    private static final String BUILD_DATE = "05/13/2023 01:16:54 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
