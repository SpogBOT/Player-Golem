package net.spogbot.playergolem.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.spogbot.playergolem.PlayerSkinCache;

@Environment(EnvType.CLIENT)
public class PlayergolemCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("playergolem")
                            .then(ClientCommandManager.literal("reload")
                                    .executes(ctx -> {
                                        PlayerSkinCache.invalidate(null);
                                        return 1;
                                    })
                                    .then(ClientCommandManager.argument("player", StringArgumentType.word())
                                            .executes(ctx -> {
                                                String name = StringArgumentType.getString(ctx, "player");
                                                PlayerSkinCache.invalidate(name);
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }

    private static void sendFeedback(String message) {
        var player = MinecraftClient.getInstance().player;
        if (player != null) player.sendMessage(Text.literal(message), false);
    }
}