package com.company.sitovetrina.view.privacy;

import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
import com.company.sitovetrina.entity.Configsitovetrina;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "privacy", layout = MainView.class)
@ViewController(id = "Privacy")
@ViewDescriptor(path = "privacy.xml")
public class Privacy extends StandardView {

    @ViewComponent private Div introduzioneDiv;
    @ViewComponent private Div titolareDiv;
    @ViewComponent private Div finalitaDiv;
    @ViewComponent private Div newsletterDiv;
    @ViewComponent private Div cookiesDiv;
    @ViewComponent private Div terzePartiDiv;
    @ViewComponent private Div sicurezzaDiv;
    @ViewComponent private Div dirittiDiv;
    @ViewComponent private Div contattiDiv;

    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onInit(InitEvent event) {

        List<Configsitovetrina> list = dataManager.load(Configsitovetrina.class).all().list();
        Configsitovetrina config = list.isEmpty() ? null : list.get(0);

        introduzioneDiv.getElement().setProperty("innerHTML", """
            <h3>1. Informazioni generali</h3>
            <p>La presente Privacy Policy descrive le modalità di trattamento dei dati personali degli utenti 
            che visitano questo sito web, in conformità al Regolamento UE 679/2016 (GDPR) e alla normativa italiana vigente.</p>
        """);

        if (config != null) {
            titolareDiv.getElement().setProperty("innerHTML", """
                <h3>2. Titolare del trattamento</h3>
                <p><strong>%s</strong><br>
                P.IVA: %s<br>
                Sede: %s, %s %s<br>
                Email: %s<br>
                Telefono: %s</p>
            """.formatted(
                    config.getRagioneSociale(),
                    config.getPiva(),
                    config.getIndirizzo(),
                    config.getCap(),
                    config.getCitta(),
                    config.getEmailContatti(),
                    config.getTelefono()
            ));
        }

        finalitaDiv.getElement().setProperty("innerHTML", """
            <h3>3. Finalità del trattamento e basi giuridiche</h3>
            <ul>
                <li>Funzionamento tecnico del sito → interesse legittimo</li>
                <li>Contatto tramite form → esecuzione di misure precontrattuali</li>
                <li>Newsletter → consenso espresso dell’utente</li>
                <li>Cookie tecnici → interesse legittimo</li>
                <li>Cookie terze parti (Google Maps) → consenso dell’utente</li>
            </ul>
        """);

        newsletterDiv.getElement().setProperty("innerHTML", """
            <h3>4. Trattamento dei dati per la newsletter</h3>
            <p>L'indirizzo email viene raccolto esclusivamente per l’invio della newsletter informativa dell’azienda.
            I dati:</p>
            <ul>
                <li>vengono salvati nel database interno del sito</li>
                <li>non vengono comunicati a terzi</li>
                <li>sono conservati fino alla revoca del consenso</li>
                <li>possono essere cancellati in qualsiasi momento tramite link di disiscrizione o contattando il Titolare</li>
            </ul>
        """);

        cookiesDiv.getElement().setProperty("innerHTML", """
            <h3>5. Cookie</h3>
            <p>Questo sito utilizza cookie tecnici necessari al funzionamento e, previo consenso, cookie di terze parti 
            come Google Maps. Per dettagli completi consulta la <a href="/cookie">Cookie Policy</a>.</p>
        """);

        terzePartiDiv.getElement().setProperty("innerHTML", """
            <h3>6. Servizi di terze parti</h3>
            <p>Alcuni servizi come Google Maps possono comportare trasferimenti di dati verso Paesi extra UE (es. USA).
            Il trasferimento avviene sulla base del consenso dell’utente e delle garanzie adottate da Google LLC.</p>
        """);

        sicurezzaDiv.getElement().setProperty("innerHTML", """
            <h3>7. Sicurezza</h3>
            <p>I dati personali sono trattati con misure tecniche e organizzative adeguate 
            a prevenire accessi non autorizzati, perdita o diffusione dei dati.</p>
        """);

        dirittiDiv.getElement().setProperty("innerHTML", """
            <h3>8. Diritti dell’utente</h3>
            <p>L’utente può esercitare i seguenti diritti:</p>
            <ul>
                <li>Accesso ai dati</li>
                <li>Rettifica</li>
                <li>Cancellazione (diritto all'oblio)</li>
                <li>Limitazione</li>
                <li>Portabilità</li>
                <li>Opposizione</li>
                <li>Reclamo al Garante Privacy</li>
            </ul>
        """);

        if (config != null) {
            contattiDiv.getElement().setProperty("innerHTML", """
                <h3>9. Contatti per esercitare i diritti</h3>
                <p>Per qualsiasi richiesta relativa ai tuoi dati personali, puoi scrivere a:<br>
                <strong>%s</strong></p>
            """.formatted(config.getEmailContatti()));
        }
    }
}
