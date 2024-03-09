package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1352";
    private static final String BUILD_DATE = "03/09/2024 06:20:11 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
