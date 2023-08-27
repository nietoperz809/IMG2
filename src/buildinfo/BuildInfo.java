package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "734";
    private static final String BUILD_DATE = "08/27/2023 02:17:41 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
