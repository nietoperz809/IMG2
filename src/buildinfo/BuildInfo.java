package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1043";
    private static final String BUILD_DATE = "10/04/2023 05:10:41 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
