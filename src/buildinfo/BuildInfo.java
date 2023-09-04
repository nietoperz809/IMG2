package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "744";
    private static final String BUILD_DATE = "09/04/2023 01:56:27 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
