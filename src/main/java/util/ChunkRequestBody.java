package util;

import exception.UploadException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.SocketException;

/**
 * Custom RequestBody to handle reading from a section of a file rather than the entire file.
 */
public class ChunkRequestBody extends RequestBody {
    private String filepath;
    private long offset;
    private int chunkSize;
    private MediaType mediaType;

    public ChunkRequestBody(String filepath, MediaType mediaType, long offset, int chunkSize) {
        this.filepath = filepath;
        this.mediaType = mediaType;
        this.offset = offset;
        this.chunkSize = chunkSize;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return this.mediaType;
    }

    @Override public long contentLength() {
        return chunkSize;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        RandomAccessFile source = new RandomAccessFile(filepath, "r");
        byte[] bytes = new byte[Upload.CHUNK_SIZE];

        source.seek(offset);
        source.read(bytes, 0, chunkSize);
        source.close();

        try {
            sink.write(bytes, 0, chunkSize);
            sink.close();
        } catch (SocketException e){
            throw new UploadException();
        }
    }
}
