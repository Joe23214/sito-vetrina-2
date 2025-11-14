package com.company.sitovetrina.view.cookiepreferences;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "cookie-preferences", layout = MainView.class)
@ViewController(id = "CookiePreferences")
@ViewDescriptor(path = "cookie-preferences.xml")
public class CookiePreferences extends StandardView {

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private Div prefDiv;

    @ViewComponent
    private Div aziendaDiv;

    @ViewComponent
    private Div contattiDiv;

    @Subscribe
    public void onInit(InitEvent event) {
        List<Configsitovetrina> list = dataManager.load(Configsitovetrina.class).all().list();
        if (!list.isEmpty()) {
            Configsitovetrina config = list.get(0);

            prefDiv.setText("Imposta qui le tue preferenze sui cookie. Puoi disabilitare quelli non essenziali.");

            aziendaDiv.setText(config.getRagioneSociale() + " - P.IVA: " + config.getPiva());
            contattiDiv.setText("Indirizzo: " + config.getIndirizzo() + ", " + config.getCap() + " " + config.getCitta() +
                    " | Email: " + config.getEmailContatti() +
                    " | Telefono: " + config.getTelefono());
        }
    }
}
