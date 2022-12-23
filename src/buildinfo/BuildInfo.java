package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "374";
    private static final String BUILD_DATE = "12/23/2022 12:19:33 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
