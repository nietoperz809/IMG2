package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "814";
    private static final String BUILD_DATE = "09/17/2023 09:24:20 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
