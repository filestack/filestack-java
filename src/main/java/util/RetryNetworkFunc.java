package util;

import retrofit2.Response;

import java.io.IOException;

public abstract class RetryNetworkFunc<T> {
    private final int maxNetworkRetries;
    private final int maxServerRetries;
    private final int delayBase;

    private int networkRetries;
    private int serverRetries;

    public RetryNetworkFunc(int maxNetworkRetries, int maxServerRetries, int delayBase) {
        this.maxNetworkRetries = maxNetworkRetries;
        this.maxServerRetries = maxServerRetries;
        this.delayBase = delayBase;
    }

    public T call() throws IOException {
        Response response = run();
        return process(response);
    }

    Response run() throws IOException {
        Response response;

        try {
            response = work();
        } catch (IOException e) {
            response = retryNetwork();
        }

        if (response.code() != 200)
            response = retryServer(response.code());

        return response;
    }

    abstract Response work() throws IOException;

    Response retryNetwork() throws IOException {
        if (networkRetries >= maxNetworkRetries)
            throw new IOException("Upload failed: Network unusable");

        if (delayBase > 0)
            try {
                Thread.sleep((long) Math.pow(delayBase, networkRetries) * 1000);
            } catch (InterruptedException e) {
                networkRetries++;
            }

        networkRetries++;
        return run();
    }

    Response retryServer(int code)  throws IOException {
        if (code == 206 || code == 400 || code == 403 || serverRetries >= maxServerRetries)
            throw new IOException("Upload failed: " + code);

        if (delayBase > 0)
            try {
                Thread.sleep((long) Math.pow(delayBase, serverRetries) * 1000);
            } catch (InterruptedException e) {
                serverRetries++;
            }

        serverRetries++;
        return run();
    }

    @SuppressWarnings("unchecked")
    T process(Response response) {
        return (T) response.body();
    }

    public int getNetworkRetries() {
        return networkRetries;
    }

    public int getServerRetries() {
        return serverRetries;
    }
}
