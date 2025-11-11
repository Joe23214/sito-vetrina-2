package com.company.sitovetrina.view.newsletteradmin;

import com.company.sitovetrina.entity.Newsletter;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorageLocator;
import io.jmix.email.EmailAttachment;
import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.upload.FileStorageUploadField;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Route(value = "newsletter-admin", layout = MainView.class)
@ViewController("NewsletterAdminView")
@ViewDescriptor(path = "newsletter-admin-view.xml")
public class NewsletterAdminView extends StandardView {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private Emailer emailer;
    @Autowired
    private Notifications notifications;
    @Autowired
    private FileStorageLocator fileStorageLocator;

    @ViewComponent
    private TextField subjectField;
    @ViewComponent
    private TextField titleField;
    @ViewComponent
    private TextArea bodyField;
    @ViewComponent
    private FileStorageUploadField attachmentField;
    @ViewComponent
    private Button sendButton;

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

    @Subscribe("sendButton")
    public void onSendButtonClick(ClickEvent<Button> event) {
        String subject = subjectField.getValue();
        String body = bodyField.getValue();
        String title = titleField.getValue();

        if (subject == null || subject.isBlank() || body == null || body.isBlank()) {
            notifications.create("Compila oggetto e testo email.")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        List<Newsletter> iscritti = dataManager.load(Newsletter.class).all().list();
        if (iscritti.isEmpty()) {
            notifications.create("Nessun iscritto trovato.")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        FileRef attachmentRef = attachmentField.getValue();
        int success = 0;

        for (Newsletter n : iscritti) {
            try {
                String htmlBody = generateEmailHtml(title, body);
                List<EmailAttachment> attachments = new ArrayList<>();

                if (attachmentRef != null) {
                    try (InputStream input = fileStorageLocator.getDefault().openStream(attachmentRef)) {
                        byte[] data = input.readAllBytes();
                        attachments.add(new EmailAttachment(
                                data,
                                attachmentRef.getFileName(),
                                "application/octet-stream"
                        ));
                    } catch (IOException e) {
                        System.err.println("Errore lettura allegato: " + e.getMessage());
                    }
                }

                // ✅ Usa EmailInfoBuilder (ufficiale in Jmix 2.6)
                EmailInfo emailInfo = EmailInfoBuilder.create()
                        .setAddresses(n.getEmail())
                        .setSubject(subject)
                        .setBody(htmlBody)
                        .setBodyContentType("text/html")
                        .setAttachments(attachments)
                        .build();

                emailer.sendEmail(emailInfo);
                success++;

            } catch (Exception e) {
                System.err.println("Errore invio a " + n.getEmail() + ": " + e.getMessage());
            }
        }

        notifications.create("Email inviate con successo a " + success + " iscritti.")
                .withType(Notifications.Type.SUCCESS)
                .show();

        subjectField.clear();
        titleField.clear();
        bodyField.clear();
        attachmentField.clear();
    }

    private String generateEmailHtml(String title, String body) {
        String logoBase64 = getLogoBase64();
        return """
    <html>
    <body style="margin:0; padding:0; background-color:#f3f4f6; font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
        <table role="presentation" cellpadding="0" cellspacing="0" width="100%%" style="background-color:#f3f4f6; padding:40px 0;">
            <tr>
                <td align="center">
                    <table role="presentation" cellpadding="0" cellspacing="0" width="600" style="background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.08);">
                        <tr>
                            <td align="center" style="padding:40px 20px 10px 20px;">
                                <!-- LOGO -->
                                 <img src="%s" alt="Logo" width="160" style="display:block; margin:auto; border:0;">
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="padding:0 40px 10px 40px;">
                                <h1 style="font-size:24px; font-weight:600; color:#222222; margin:30px 0 10px 0;">%s</h1>
                            </td>
                        </tr>
                        <tr>
                            <td align="left" style="padding:0 40px 30px 40px;">
                                <p style="font-size:16px; line-height:1.6; color:#444444; margin:0;">%s</p>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="padding:20px 40px 40px 40px; border-top:1px solid #e5e7eb;">
                                <p style="font-size:13px; color:#999999; margin:0;">
                                    © 2025 <strong>Sito Vetrina</strong> · Tutti i diritti riservati<br>
                                    <a href="https://www.tuosito.it" style="color:#a48b5f; text-decoration:none;">www.tuosito.it</a>
                                </p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
         <p style="font-size:13px; color:#999999; text-align:center; margin-top:30px;">
            Se non vuoi più ricevere queste email,
            <a href="https://www.tuosito.it/unsubscribe?email=%s"
               style="color:#a48b5f; text-decoration:none;">clicca qui per disiscriverti</a>.
        </p>
    </body>
    </html>
    """.formatted(title, body);
    }

}
