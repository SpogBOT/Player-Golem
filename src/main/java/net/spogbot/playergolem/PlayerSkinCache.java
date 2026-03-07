package net.spogbot.playergolem;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerSkinCache {
    private static final ConcurrentHashMap<String, CompletableFuture<Optional<Identifier>>> CACHE = new ConcurrentHashMap<>();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static CompletableFuture<Optional<Identifier>> getOrLoad(String name) {
        if (name == null || name.trim().isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        String key = name.trim().toLowerCase();
        return CACHE.computeIfAbsent(key, PlayerSkinCache::loadSkinAsync);
    }

    private static CompletableFuture<Optional<Identifier>> loadSkinAsync(String name) {
        return fetchSkinUrl(name)
                .thenCompose(urlOpt -> urlOpt.isPresent()
                        ? downloadAndRegisterTexture(name, urlOpt.get())
                        : CompletableFuture.completedFuture(Optional.<Identifier>empty()))
                .exceptionally(e -> {
                    return Optional.empty();
                });
    }

    private static CompletableFuture<Optional<String>> fetchSkinUrl(String name) {
        return fetchUuid(name).thenCompose(uuidOpt -> {
            if (uuidOpt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());
            String uuidStr = uuidOpt.get().toString().replace("-", "");

            return CompletableFuture.supplyAsync(() -> {
                try {
                    String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuidStr;
                    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                            .timeout(Duration.ofSeconds(10))
                            .GET()
                            .build();

                    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() != 200) return Optional.empty();

                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    JsonArray properties = json.getAsJsonArray("properties");

                    for (var elem : properties) {
                        JsonObject prop = elem.getAsJsonObject();
                        if ("textures".equals(prop.get("name").getAsString())) {
                            String base64 = prop.get("value").getAsString();
                            String decoded = new String(Base64.getDecoder().decode(base64));
                            JsonObject texturesJson = JsonParser.parseString(decoded).getAsJsonObject()
                                    .getAsJsonObject("textures");

                            String skinUrl = texturesJson.getAsJsonObject("SKIN").get("url").getAsString();
                            return Optional.of(skinUrl);
                        }
                    }
                } catch (Exception ignored) {}
                return Optional.empty();
            }, Util.getMainWorkerExecutor());
        });
    }

    private static CompletableFuture<Optional<java.util.UUID>> fetchUuid(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name))
                        .timeout(Duration.ofSeconds(8))
                        .GET()
                        .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    String id = json.get("id").getAsString();
                    java.util.UUID uuid = new java.util.UUID(
                            Long.parseUnsignedLong(id.substring(0, 16), 16),
                            Long.parseUnsignedLong(id.substring(16), 16)
                    );
                    return Optional.of(uuid);
                }
            } catch (Exception ignored) {}
            return Optional.empty();
        }, Util.getMainWorkerExecutor());
    }

    private static CompletableFuture<Optional<Identifier>> downloadAndRegisterTexture(String name, String skinUrl) {
        Identifier textureId = Identifier.of("copperfriends", "skins/" + name.toLowerCase().replaceAll("[^a-z0-9/._-]", "_"));

        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder(URI.create(skinUrl))
                        .timeout(Duration.ofSeconds(15))
                        .GET()
                        .build();

                HttpResponse<byte[]> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() != 200) {
                    return Optional.<Identifier>empty();
                }

                NativeImage image = NativeImage.read(response.body());
                CompletableFuture<Optional<Identifier>> registerFuture = new CompletableFuture<>();

                MinecraftClient.getInstance().execute(() -> {
                    try {
                        NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> textureId.toString(), image);
                        MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);
                        registerFuture.complete(Optional.of(textureId));
                    } catch (Exception e) {
                        e.printStackTrace();
                        registerFuture.complete(Optional.empty());
                    }
                });

                return registerFuture.join();
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }, Util.getMainWorkerExecutor());
    }

    public static void clearCache() {
        CACHE.clear();
    }

    public static void invalidate(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            CACHE.clear();
        } else {
            String key = playerName.trim().toLowerCase();
            if (CACHE.remove(key) != null) {
            } else {
            }
        }
    }
}