package example.sessionswitch;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;

import java.util.Optional;

public class LoginScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget tokenField;
    private ButtonWidget loginButton;
    private Text status = Text.empty();
    private int statusColor = 0xFFFFFF;

    public LoginScreen(Screen parent) {
        super(Text.literal("Session Switch"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        tokenField = new TextFieldWidget(this.textRenderer, cx - 150, cy - 10, 300, 20, Text.literal("Session ID"));
        tokenField.setMaxLength(8192);
        tokenField.setPlaceholder(Text.literal("Paste your session ID (access token)"));
        this.addDrawableChild(tokenField);
        this.setInitialFocus(tokenField);

        loginButton = ButtonWidget.builder(Text.literal("Log in"), b -> login())
                .dimensions(cx - 150, cy + 20, 145, 20).build();
        this.addDrawableChild(loginButton);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Restore original"), b -> restore())
                .dimensions(cx + 5, cy + 20, 145, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close())
                .dimensions(cx - 50, cy + 45, 100, 20).build());
    }

    private void login() {
        String token = tokenField.getText().trim();
        if (token.regionMatches(true, 0, "Bearer ", 0, 7)) {
            token = token.substring(7).trim();
        }
        if (token.isEmpty()) {
            setStatus("Paste a session ID first.", 0xFF5555);
            return;
        }

        MinecraftClient mc = this.client;
        final String finalToken = token;
        loginButton.active = false;
        setStatus("Checking with Mojang...", 0xFFFF55);

        MojangApi.fetchProfile(finalToken).whenComplete((profile, error) -> mc.execute(() -> {
            loginButton.active = true;
            if (error != null || profile == null) {
                setStatus("Invalid or expired session ID.", 0xFF5555);
                return;
            }
            Session session = new Session(profile.name(), profile.uuid(), finalToken,
                    Optional.empty(), Optional.empty(), Session.AccountType.MSA);
            SessionManager.apply(mc, session);
            tokenField.setText("");
            setStatus("Logged in as " + profile.name(), 0x55FF55);
        }));
    }

    private void restore() {
        SessionManager.restore(this.client);
        setStatus("Original session restored.", 0x55FF55);
    }

    private void setStatus(String message, int color) {
        this.status = Text.literal(message);
        this.statusColor = color;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int cx = this.width / 2;
        int cy = this.height / 2;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, cx, cy - 60, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Current account: " + this.client.getSession().getUsername()), cx, cy - 35, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, status, cx, cy + 75, statusColor);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
