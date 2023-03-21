package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "414";
    private static final String BUILD_DATE = "03/21/2023 04:49:07 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
