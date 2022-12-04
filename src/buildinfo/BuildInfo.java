package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "57";
    private static final String BUILD_DATE = "12/04/2022 06:18:58 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
