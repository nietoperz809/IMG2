package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1237";
    private static final String BUILD_DATE = "12/02/2023 05:37:02 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
