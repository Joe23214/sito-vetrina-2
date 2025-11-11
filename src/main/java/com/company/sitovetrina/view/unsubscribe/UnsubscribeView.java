package com.company.sitovetrina.view.unsubscribe;

import com.company.sitovetrina.entity.Newsletter;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import io.jmix.core.DataManager;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.*;
import java.util.Base64;
import java.util.Optional;
import java.io.IOException;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route(value = "unsubscribe", layout = MainView.class)
@ViewController("UnsubscribeView")
@ViewDescriptor(path = "unsubscribe-view.xml")
@AnonymousAllowed
public class UnsubscribeView extends StandardView implements BeforeEnterObserver {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private Notifications notifications;
    @Autowired
    private Emailer emailer;

    @ViewComponent
    private Div infoLabel;
    @ViewComponent
    private Button confirmButton;

    private String email;
    @Value("${site.files.path}")
    private String filesPath;
    private Optional<Path> getLogoPath() {
        Path dir = Paths.get(filesPath);
        String[] extensions = {".png", ".jpg", ".jpeg", ".gif", ".webp"};

        for (String suffix : new String[]{"-new", "-old", ""}) {
            for (String ext : extensions) {
                Path p = dir.resolve("logo" + suffix + ext);
                if (Files.exists(p)) {
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }

    private String getLogoBase64() {
        try {
            Optional<Path> logoPathOpt = getLogoPath();
            if (logoPathOpt.isEmpty()) {
                return ""; // nessun logo trovato
            }
            Path logoPath = logoPathOpt.get();
            byte[] bytes = Files.readAllBytes(logoPath);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String extension = "";

            String name = logoPath.getFileName().toString();
            int dot = name.lastIndexOf('.');
            if (dot > 0) {
                extension = name.substring(dot + 1);
            }

            return "data:image/" + extension + ";base64," + base64;
        } catch (IOException e) {
            System.err.println("Errore lettura logo: " + e.getMessage());
            return "";
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (infoLabel == null) return; // sicurezza aggiuntiva

        Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
        if (params.containsKey("email") && !params.get("email").isEmpty()) {
            email = params.get("email").get(0);
        } else {
            email = null;
        }

        // Legge il parametro 'email' dalla query string
        //Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
        email = params.getOrDefault("email", List.of()).stream().findFirst().orElse(null);

        if (email == null || email.isBlank()) {
            // Nessuna email: messaggio chiaro e pulsante disabilitato
            infoLabel.setText("Indirizzo email non specificato. Accedi tramite il link corretto dalla newsletter.");
            confirmButton.setVisible(false);
            return;
        }

        // Email presente: controlla se esiste nel DB
        Optional<Newsletter> existing = dataManager.load(Newsletter.class)
                .query("select n from Newsletter n where n.email = :email")
                .parameter("email", email)
                .optional();

        if (existing.isEmpty()) {
            infoLabel.setText("L’indirizzo email non risulta iscritto alla newsletter.");
            confirmButton.setEnabled(false);
        } else {
            infoLabel.setText("Vuoi davvero disiscriverti dalla newsletter?");
            confirmButton.setEnabled(true);
        }
    }

    @Subscribe("confirmButton")
    public void onConfirmButtonClick(ClickEvent<Button> event) {
        if (email == null || email.isBlank()) {
            notifications.create("Email non valida.").withType(Notifications.Type.WARNING).show();
            return;
        }

        Optional<Newsletter> existing = dataManager.load(Newsletter.class)
                .query("select n from Newsletter n where n.email = :email")
                .parameter("email", email)
                .optional();

        if (existing.isEmpty()) {
            notifications.create("Email non trovata.").withType(Notifications.Type.WARNING).show();
            infoLabel.setText("L’indirizzo email non risulta iscritto.");
            confirmButton.setEnabled(false);
            return;
        }

        // Rimuove l'iscrizione
        dataManager.remove(existing.get());

        notifications.create("Disiscrizione completata con successo.")
                .withType(Notifications.Type.SUCCESS)
                .show();

        sendUnsubscribeConfirmation(email);

        infoLabel.setText("Ti sei disiscritto con successo dalla newsletter.");
        confirmButton.setEnabled(false);
    }

    private void sendUnsubscribeConfirmation(String email) {
        try {
            String html = generateUnsubscribeEmailHtml(email);
            emailer.sendEmail(
                    EmailInfoBuilder.create()
                            .setAddresses(email)
                            .setSubject("Conferma disiscrizione dalla Newsletter")
                            .setBody(html)
                            .setBodyContentType("text/html")
                            .build()
            );
        } catch (Exception e) {
            System.err.println("Errore invio email di conferma disiscrizione: " + e.getMessage());
        }
    }

    private String generateUnsubscribeEmailHtml(String email) {
        String logoBase64 = getLogoBase64();
        return """
        <html>
        <body style="margin:0; padding:0; background-color:#f3f4f6; font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
            <table role="presentation" cellpadding="0" cellspacing="0" width="100%%" style="background-color:#f3f4f6; padding:40px 0;">
                <tr>
                    <td align="center">
                        <table role="presentation" cellpadding="0" cellspacing="0" width="600"
                               style="background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.08);">
                            <tr>
                                <td align="center" style="padding:40px 20px 10px 20px;">
                                   <img src="%s" alt="Logo" width="160" style="display:block; margin:auto; border:0;">
                                </td>
                            </tr>
                            <tr>
                                <td align="center" style="padding:0 40px 10px 40px;">
                                    <h1 style="font-size:24px; font-weight:600; color:#222222; margin:30px 0 10px 0;">
                                        Ti sei disiscritto con successo
                                    </h1>
                                </td>
                            </tr>
                            <tr>
                                <td align="left" style="padding:0 40px 20px 40px;">
                                    <p style="font-size:16px; line-height:1.6; color:#444444;">
                                        Ciao <strong>%s</strong>,<br><br>
                                        la tua email è stata rimossa dalla nostra newsletter.<br>
                                        Ci dispiace vederti andare! Se cambierai idea, potrai iscriverti nuovamente dal nostro sito.
                                    </p>
                                </td>
                            </tr>
                            <tr>
                                <td align="center" style="padding:20px 40px 40px 40px;">
                                    <a href="https://www.tuosito.it"
                                       style="background-color:#a48b5f; color:#ffffff; text-decoration:none; padding:12px 28px; border-radius:6px; font-weight:600;">
                                       Torna al sito
                                    </a>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """.formatted(email);
    }
}
