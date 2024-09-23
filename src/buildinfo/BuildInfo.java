package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1437";
    private static final String BUILD_DATE = "09/23/2024 09:07:25 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
