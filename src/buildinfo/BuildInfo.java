package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1960";
    private static final String BUILD_DATE = "11/13/2024 09:00:34 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
