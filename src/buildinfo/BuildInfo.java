package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1882";
    private static final String BUILD_DATE = "11/02/2024 09:29:21 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
