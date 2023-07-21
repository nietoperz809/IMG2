package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "597";
    private static final String BUILD_DATE = "07/21/2023 11:47:07 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
