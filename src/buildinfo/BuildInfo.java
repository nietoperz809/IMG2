package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "701";
    private static final String BUILD_DATE = "08/26/2023 06:41:58 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
