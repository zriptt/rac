package example.sessionswitch;

import example.sessionswitch.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

/** Holds the original session in memory only so it can be restored. Nothing is written to disk. */
public final class SessionManager {
    private static Session original;

    private SessionManager() {}

    public static boolean isSwapped() {
        return original != null;
    }

    public static void apply(MinecraftClient client, Session session) {
        if (original == null) {
            original = client.getSession();
        }
        ((MinecraftClientAccessor) client).sessionswitch$setSession(session);
    }

    public static void restore(MinecraftClient client) {
        if (original != null) {
            ((MinecraftClientAccessor) client).sessionswitch$setSession(original);
            original = null;
        }
    }
}
