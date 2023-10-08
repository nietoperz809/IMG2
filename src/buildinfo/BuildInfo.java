package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1064";
    private static final String BUILD_DATE = "10/08/2023 09:20:14 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
