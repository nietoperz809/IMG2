package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "262";
    private static final String BUILD_DATE = "12/10/2022 08:58:13 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
