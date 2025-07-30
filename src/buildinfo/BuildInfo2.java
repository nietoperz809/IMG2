// Machine generated file *DO NOT EDIT!*
package buildinfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class BuildInfo2 {
    public static final String BUILD_NUMBER = "3573";
    public static final String BUILD_DATE = "07/29/2025 at 08:36 PM";
    public static final String GIT_REV = "" + getCommitCount();

    private static int getCommitCount() {
        ProcessBuilder builder = new ProcessBuilder("git", "rev-list", "HEAD", "--count");
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            boolean finished = process.waitFor(3, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                throw new RuntimeException("Timeout on running git");
            }
            // read git rev value
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && line.matches("\\d+")) {
                    return Integer.parseInt(line.trim());
                } else {
                    throw new RuntimeException("git fail: " + line);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("failed to run GIT", e);
        }
    }
}

