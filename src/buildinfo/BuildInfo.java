package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "1370";
    private static final String BUILD_DATE = "03/30/2024 09:55:25 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
