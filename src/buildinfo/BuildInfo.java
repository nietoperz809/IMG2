package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "563";
    private static final String BUILD_DATE = "05/01/2023 06:54:58 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
