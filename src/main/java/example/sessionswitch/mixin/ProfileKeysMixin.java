package example.sessionswitch.mixin;

import example.sessionswitch.SessionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The chat-signing keys belong to the account the game started with. While a different
 * session is active, report "no keys" so the game doesn't sign chat with the wrong account's key.
 */
@Mixin(MinecraftClient.class)
public abstract class ProfileKeysMixin {
    @Inject(method = "getProfileKeys", at = @At("HEAD"), cancellable = true)
    private void sessionswitch$noKeysWhileSwapped(CallbackInfoReturnable<ProfileKeys> cir) {
        if (SessionManager.isSwapped()) {
            cir.setReturnValue(ProfileKeys.MISSING);
        }
    }
}
