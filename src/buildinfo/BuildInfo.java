package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "878";
    private static final String BUILD_DATE = "09/22/2023 02:28:01 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
