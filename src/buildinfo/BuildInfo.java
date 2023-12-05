package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1268";
    private static final String BUILD_DATE = "12/05/2023 06:05:13 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
