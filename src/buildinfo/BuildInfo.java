package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2930";
    private static final String BUILD_DATE = "04/02/2025 09:40:43 PM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
