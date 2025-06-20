package common;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Channelcopy {
    public static void perform(Path source,
                               Path destination,
                               long maxChunkSize,
                               CopyCallback cp) throws IOException {
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
                cp.func(transferred);
            }
        }
    }

    public interface CopyCallback {
        void func (long transferred);
    }
}

// 10 905 190 400