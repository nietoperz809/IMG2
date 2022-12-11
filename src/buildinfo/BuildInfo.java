package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "295";
    private static final String BUILD_DATE = "12/11/2022 06:37:02 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
