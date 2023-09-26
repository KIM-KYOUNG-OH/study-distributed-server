package networking;

import com.sun.deploy.net.HttpRequest;
import model.Result;
import model.SerializationUtils;
import sun.net.www.http.HttpClient;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class WebClient {
    private HttpClient httpClient;

    public WebClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public CompletableFuture<Result> sendTask(String url, byte[] requestPayload) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))
                .uri(URI.create(url))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(HttpResponse::body)
                .thenApply(responseBody -> (Result) SerializationUtils.deserialize(responseBody));
    }
}
