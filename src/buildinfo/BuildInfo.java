package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2018";
    private static final String BUILD_DATE = "11/14/2024 06:35:10 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
