package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "849";
    private static final String BUILD_DATE = "09/21/2023 06:15:42 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
