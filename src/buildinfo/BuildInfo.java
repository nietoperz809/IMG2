package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "40";
    private static final String BUILD_DATE = "11/22/2022 04:15:02 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
