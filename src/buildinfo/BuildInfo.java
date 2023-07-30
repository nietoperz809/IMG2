package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "628";
    private static final String BUILD_DATE = "07/30/2023 11:22:50 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
