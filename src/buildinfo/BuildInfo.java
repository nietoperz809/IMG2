package buildinfo;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2606";
    private static final String BUILD_DATE = "03/04/2025 11:28:48 AM";

    public static final String buildInfo = "ImageBase, Build: " + BUILD_NUMBER + " -- " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
