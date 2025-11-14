package com.company.sitovetrina.view.privacy;


import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import com.company.sitovetrina.entity.Configsitovetrina;
import io.jmix.core.DataManager;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "privacy", layout = MainView.class)
@ViewController(id = "Privacy")
@ViewDescriptor(path = "privacy.xml")
public class Privacy extends StandardView {
    @ViewComponent
    private Div aziendaDiv;

    @ViewComponent
    private Div contattiDiv;

    @ViewComponent
    private Div newsletterDiv;

    @ViewComponent
    private Div cookiesDiv;

    @ViewComponent
    private Div dirittiDiv;
    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onInit(InitEvent event) {
        // Carico l'unica riga della configurazione
        List<Configsitovetrina> list = dataManager.load(Configsitovetrina.class).all().list();
        if (!list.isEmpty()) {
            Configsitovetrina config = list.get(0);

            aziendaDiv.setText(config.getRagioneSociale() + " - P.IVA: " + config.getPiva());
            contattiDiv.setText("Indirizzo: " + config.getIndirizzo() + ", " + config.getCap() + " " + config.getCitta() +
                    " | Email: " + config.getEmailContatti() +
                    " | Telefono: " + config.getTelefono());

            newsletterDiv.setText("I dati inseriti nel campo email vengono salvati nel database interno del sito " +
                    "e utilizzati esclusivamente per l’invio della newsletter aziendale.");

            cookiesDiv.setText("Il sito utilizza cookie tecnici necessari al funzionamento. Alcuni servizi esterni, " +
                    "come Google Maps, possono installare cookie di terze parti solo dopo consenso dell’utente.");

            dirittiDiv.setText("L’utente può richiedere in qualsiasi momento la cancellazione o modifica dei propri dati " +
                    "scrivendo a: " + config.getEmailContatti());
        }
    }
}