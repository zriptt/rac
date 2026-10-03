package example.sessionswitch;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** The only network call in this mod: ask Mojang who owns a token. */
public final class MojangApi {
    private static final URI PROFILE = URI.create("https://api.minecraftservices.com/minecraft/profile");
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public record Profile(String name, UUID uuid) {}

    private MojangApi() {}

    public static CompletableFuture<Profile> fetchProfile(String token) {
        HttpRequest request = HttpRequest.newBuilder(PROFILE)
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            if (response.statusCode() != 200) {
                throw new IllegalStateException("HTTP " + response.statusCode());
            }
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String name = json.get("name").getAsString();
            String id = json.get("id").getAsString();
            return new Profile(name, parseUuid(id));
        });
    }

    private static UUID parseUuid(String id) {
        if (id.contains("-")) {
            return UUID.fromString(id);
        }
        return UUID.fromString(id.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"));
    }
}
