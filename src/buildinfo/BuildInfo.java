package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "349";
    private static final String BUILD_DATE = "12/18/2022 10:54:33 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
