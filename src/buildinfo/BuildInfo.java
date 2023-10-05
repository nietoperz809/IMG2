package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1046";
    private static final String BUILD_DATE = "10/05/2023 11:12:15 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
