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
    private Div tipologieDiv;

    @ViewComponent
    private Div gestioneDiv;

    @Subscribe
    public void onInit(InitEvent event) {
        List<Configsitovetrina> list = dataManager.load(Configsitovetrina.class).all().list();
        if (!list.isEmpty()) {
            Configsitovetrina config = list.get(0);

            introduzioneDiv.setText("Il sito utilizza cookie tecnici per migliorare l’esperienza dell’utente.");
            tipologieDiv.setText("Tipologie di cookie: tecnici, analitici e di terze parti (es. Google Maps).");
            gestioneDiv.setText("L’utente può gestire le preferenze sui cookie attraverso le impostazioni del browser.");

            // aggiungo info aziendali come in privacy
            aziendaDiv.setText(config.getRagioneSociale() + " - P.IVA: " + config.getPiva());
            contattiDiv.setText("Indirizzo: " + config.getIndirizzo() + ", " + config.getCap() + " " + config.getCitta() +
                    " | Email: " + config.getEmailContatti() +
                    " | Telefono: " + config.getTelefono());
        }
    }
}