package pro.gravit.launcher.client.gui.scenes.login.methods;

import javafx.scene.control.TextField;
import pro.gravit.launcher.client.gui.JavaFXApplication;
import pro.gravit.launcher.client.gui.helper.LookupHelper;
import pro.gravit.launcher.client.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.client.gui.impl.ContextHelper;
import pro.gravit.launcher.client.gui.scenes.login.LoginScene;
import pro.gravit.launcher.request.auth.AuthRequest;
import pro.gravit.launcher.request.auth.details.AuthPasswordDetails;

import java.util.concurrent.CompletableFuture;

public class LoginAndPasswordAuthMethod extends AbstractAuthMethod<AuthPasswordDetails> {
    private final LoginAndPasswordOverlay overlay;
    private final JavaFXApplication application;
    private final LoginScene.LoginSceneAccessor accessor;

    public LoginAndPasswordAuthMethod(LoginScene.LoginSceneAccessor accessor) {
        this.accessor = accessor;
        this.application = accessor.getApplication();
        this.overlay = new LoginAndPasswordOverlay(application);
    }

    @Override
    public void prepare() {
    }

    @Override
    public void reset() {
        overlay.reset();
    }

    @Override
    public CompletableFuture<Void> show(AuthPasswordDetails details) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            //accessor.showOverlay(overlay, (e) -> future.complete(null));
            ContextHelper.runInFxThreadStatic(() -> {
                accessor.showContent(overlay);
                future.complete(null);
            });
        } catch (Exception e) {
            accessor.errorHandle(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<LoginScene.LoginAndPasswordResult> auth(AuthPasswordDetails details) {
        overlay.future = new CompletableFuture<>();
        String login = overlay.login.getText();
        AuthRequest.AuthPasswordInterface password;
        if (overlay.password.getText().isEmpty() && overlay.password.getPromptText().equals(application.getTranslation(
                "runtime.scenes.login.password.saved"))) {
            password = application.runtimeSettings.password;
            return CompletableFuture.completedFuture(new LoginScene.LoginAndPasswordResult(login, password));
        }
        return overlay.future;
    }

    @Override
    public void onAuthClicked() {
        overlay.future.complete(overlay.getResult());
    }

    @Override
    public void onUserCancel() {
        overlay.future.completeExceptionally(LoginAndPasswordOverlay.USER_AUTH_CANCELED_EXCEPTION);
    }

    @Override
    public CompletableFuture<Void> hide() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public boolean isOverlay() {
        return false;
    }

    public class LoginAndPasswordOverlay extends AbstractVisualComponent {
        private static final UserAuthCanceledException USER_AUTH_CANCELED_EXCEPTION = new UserAuthCanceledException();
        private TextField login;
        private TextField password;
        private CompletableFuture<LoginScene.LoginAndPasswordResult> future;

        public LoginAndPasswordOverlay(JavaFXApplication application) {
            super("scenes/login/methods/loginpassword.fxml", application);
        }

        @Override
        public String getName() {
            return "loginandpassword";
        }

        public LoginScene.LoginAndPasswordResult getResult() {
            String rawLogin = login.getText();
            String rawPassword = password.getText();
            return new LoginScene.LoginAndPasswordResult(rawLogin, accessor.getAuthService().makePassword(rawPassword));
        }

        @Override
        protected void doInit() {
            login = LookupHelper.lookup(layout, "#login");
            password = LookupHelper.lookup(layout, "#password");

            login.textProperty().addListener(l -> accessor.getAuthButton().setActive(!login.getText().isEmpty()));

            if (application.runtimeSettings.login != null) {
                login.setText(application.runtimeSettings.login);
                accessor.getAuthButton().setActive(true);
            } else {
                accessor.getAuthButton().setActive(false);
            }
            if (application.runtimeSettings.password != null) {
                password.getStyleClass().add("hasSaved");
                password.setPromptText(application.getTranslation("runtime.scenes.login.password.saved"));
            }
        }

        @Override
        protected void doPostInit() throws Exception {

        }


        @Override
        public void reset() {
            if (password == null) return;
            password.getStyleClass().removeAll("hasSaved");
            password.setPromptText(application.getTranslation("runtime.scenes.login.password"));
            password.setText("");
            login.setText("");
        }

        @Override
        public void disable() {

        }

        @Override
        public void enable() {

        }
    }
}
