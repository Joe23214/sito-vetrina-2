package com.company.sitovetrina.view.cookiepreferences;

import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.checkbox.Switch;
import io.jmix.flowui.view.*;

@Route(value = "cookie-preferences", layout = MainView.class)
@ViewController(id = "CookiePreferences")
@ViewDescriptor(path = "cookie-preferences.xml")
public class CookiePreferences extends StandardView {

    @ViewComponent
    private Switch switchThirdParty;  // ← ADESSO ESISTE!

    @Subscribe
    public void onInit(InitEvent event) {
        // Carica il valore precedente dal localStorage
        getElement().executeJs("""
            if (localStorage.getItem('thirdPartyCookies') === 'true') {
                $0.checked = true;
            } else {
                $0.checked = false;
            }
        """, switchThirdParty.getElement());
    }

    @Subscribe("btnSavePrefs")
    public void onSavePrefsClick(ClickEvent<Button> event) {

        boolean allowThird = switchThirdParty.getValue();

        // Salva su localStorage + redirect alla home
        getElement().executeJs("""
            localStorage.setItem('cookiesAccepted', 'true');
            localStorage.setItem('thirdPartyCookies', $0);
            window.location.href = '/home';
        """, allowThird);

        Notification.show("Preferenze salvate");
    }
}
