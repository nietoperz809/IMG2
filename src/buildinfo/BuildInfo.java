package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1885";
    private static final String BUILD_DATE = "11/03/2024 07:06:12 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
