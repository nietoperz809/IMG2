package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1943";
    private static final String BUILD_DATE = "11/11/2024 08:24:28 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
