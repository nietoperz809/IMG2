package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1156";
    private static final String BUILD_DATE = "11/28/2023 09:32:28 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
