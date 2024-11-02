package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1867";
    private static final String BUILD_DATE = "11/02/2024 09:09:06 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
