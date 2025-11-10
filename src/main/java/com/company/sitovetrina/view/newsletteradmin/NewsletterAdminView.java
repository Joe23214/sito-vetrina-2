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
import io.jmix.email.EmailInfo;
import io.jmix.email.Emailer;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.upload.FileStorageUploadField;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

        FileRef attachment = attachmentField.getValue();
        int success = 0;

        for (Newsletter n : iscritti) {
            try {
                // ✅ Costruzione manuale di EmailInfo (in Jmix 2.6 non c’è builder)
                EmailInfo emailInfo = new EmailInfo(
                        n.getEmail(),           // destinatario
                        subject,                // oggetto
                        "<h2>" + title + "</h2><p>" + body + "</p>",  // corpo HTML
                        "text/html",            // tipo contenuto
                        attachment              // allegato opzionale
                );

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
        attachmentField.clear(); // ✅ non clearFileList()
    }
}