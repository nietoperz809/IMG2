package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1939";
    private static final String BUILD_DATE = "11/11/2024 07:42:17 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
