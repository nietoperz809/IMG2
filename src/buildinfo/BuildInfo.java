package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "3182";
    private static final String BUILD_DATE = "04/13/2025 09:26:35 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
