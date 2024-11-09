package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1925";
    private static final String BUILD_DATE = "11/09/2024 09:50:54 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
