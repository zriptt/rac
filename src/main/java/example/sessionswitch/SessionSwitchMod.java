package example.sessionswitch;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SessionSwitchMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof MultiplayerScreen) {
                Screens.getButtons(screen).add(
                        ButtonWidget.builder(Text.literal("Session Login"),
                                        b -> client.setScreen(new LoginScreen(screen)))
                                .dimensions(5, 5, 100, 20)
                                .build());
            }
        });
    }
}
