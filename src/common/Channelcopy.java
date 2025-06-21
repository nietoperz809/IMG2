package common;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Channelcopy {
    /**
     * Copy Callback
     * @param <In1> Number of bytes transferred so far
     * @param <In2> Size of whole file
     * @param <Out> false to continue, true to stop
     */
    @FunctionalInterface
    public interface _CP<In1, In2, Out> {
        Out apply(In1 in1, In2 in2);
    }

    public static void performCopy(Path source,
                                   Path destination,
                                   long maxChunkSize,
                                   _CP<Long, Long, Boolean> cp) throws IOException {
        try (FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(destination,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.WRITE)) {
            long transferred = 0;
            long size = sourceChannel.size();
            while (transferred < size) {
                // Calculate the size of the next chunk
                long chunkSize = Math.min(maxChunkSize, size - transferred);
                // Transfer the chunk
                transferred += sourceChannel.transferTo(transferred, chunkSize, destChannel);
                if (cp.apply(transferred, size))  // true breaks the copy loop
                    return;
            }
        }
    }
}

// 10 905 190 400