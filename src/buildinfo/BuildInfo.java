package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "926";
    private static final String BUILD_DATE = "09/23/2023 07:24:14 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
