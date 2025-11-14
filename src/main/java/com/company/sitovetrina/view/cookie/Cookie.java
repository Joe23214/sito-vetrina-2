package com.company.sitovetrina.view.cookie;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "cookie", layout = MainView.class)
@ViewController(id = "Cookie")
@ViewDescriptor(path = "cookie.xml")
public class Cookie extends StandardView {

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private Div introduzioneDiv;
    @ViewComponent
    private Div tecniciDiv;
    @ViewComponent
    private Div terzePartiDiv;
    @ViewComponent
    private Div gestioneDiv;
    @ViewComponent
    private Div dirittiDiv;
    @ViewComponent
    private Div aziendaDiv;
    @ViewComponent
    private Div contattiDiv;

    @Subscribe
    public void onInit(InitEvent event) {

        List<Configsitovetrina> list = dataManager.load(Configsitovetrina.class).all().list();
        Configsitovetrina config = list.isEmpty() ? null : list.get(0);

        introduzioneDiv.getElement().setProperty("innerHTML", """
            <h3>1. Cosa sono i cookie</h3>
            <p>I cookie sono piccoli file di testo che i siti web visitati inviano al dispositivo dell’utente, 
            dove vengono memorizzati per essere poi ritrasmessi agli stessi siti alle visite successive. 
            Servono a garantire il corretto funzionamento del sito e a migliorare l’esperienza di navigazione.</p>
        """);

        tecniciDiv.getElement().setProperty("innerHTML", """
            <h3>2. Cookie Tecnici (sempre attivi)</h3>
            <p>Questi cookie sono necessari al funzionamento del sito e non richiedono il consenso dell’utente.</p>
            <ul>
                <li>Funzionamento del sito</li>
                <li>Sicurezza</li>
                <li>Mantenimento della sessione</li>
                <li>Preferenze essenziali</li>
            </ul>
            <p>Durata: variabile (sessione / fino a 1 anno)</p>
        """);

        terzePartiDiv.getElement().setProperty("innerHTML", """
            <h3>3. Cookie di Terze Parti (Google Maps)</h3>
            <p>Il sito utilizza Google Maps per mostrare la posizione dell’azienda. 
            Google può installare cookie per memorizzare preferenze, garantire sicurezza e migliorare l’esperienza utente. 
            Questi cookie vengono installati solo con il consenso dell’utente.</p>

            <p><strong>Durate tipiche dei cookie Google Maps:</strong></p>
            <table border="1" cellspacing="0" cellpadding="6">
                <tr><th>Nome Cookie</th><th>Durata</th><th>Finalità</th></tr>
                <tr><td>NID</td><td>6 mesi</td><td>Preferenze e sicurezza</td></tr>
                <tr><td>AEC</td><td>6 mesi</td><td>Prevenzione spam e abuso</td></tr>
                <tr><td>1P_JAR</td><td>30 giorni</td><td>Analisi interazioni</td></tr>
                <tr><td>CONSENT</td><td>2 anni</td><td>Memorizzazione stato consenso</td></tr>
            </table>

            <p><strong>Provider:</strong> Google LLC</p>
            <p><strong>Privacy Policy:</strong> https://policies.google.com/privacy</p>
            <p><strong>Luogo del trattamento:</strong> USA</p>
            <p><strong>Base giuridica:</strong> consenso</p>
        """);

        gestioneDiv.getElement().setProperty("innerHTML", """
            <h3>4. Come gestire o revocare il consenso</h3>
            <p>L’utente può accettare, rifiutare o modificare le preferenze sui cookie in qualsiasi momento
            visitando la pagina <strong>“Preferenze Cookie”</strong> (menu o footer).</p>
            <p>La mappa di Google verrà caricata solo se i cookie di terze parti vengono accettati.</p>
        """);

        dirittiDiv.getElement().setProperty("innerHTML", """
            <h3>5. Diritti dell’utente</h3>
            <p>L'utente può esercitare i seguenti diritti:</p>
            <ul>
                <li>Accesso ai dati</li>
                <li>Rettifica</li>
                <li>Cancellazione</li>
                <li>Limitazione</li>
                <li>Opposizione</li>
                <li>Reclamo al Garante Privacy</li>
            </ul>
        """);

        if (config != null) {
            aziendaDiv.getElement().setProperty("innerHTML", """
                <h3>6. Titolare del trattamento</h3>
                <p><strong>%s</strong><br>
                P.IVA: %s<br>
                Sede: %s, %s %s</p>
            """.formatted(
                    config.getRagioneSociale(),
                    config.getPiva(),
                    config.getIndirizzo(),
                    config.getCap(),
                    config.getCitta()
            ));

            contattiDiv.getElement().setProperty("innerHTML", """
                <h3>7. Contatti</h3>
                <p>Email: %s<br>
                Telefono: %s</p>
            """.formatted(
                    config.getEmailContatti(),
                    config.getTelefono()
            ));
        }
    }
}
