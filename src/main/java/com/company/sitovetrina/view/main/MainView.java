package com.company.sitovetrina.view.main;

import com.company.sitovetrina.view.login.LoginView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.kit.component.main.UserIndicator;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ViewController("MainView")
@ViewDescriptor("main-view.xml")
public class MainView extends StandardMainView {

    @Autowired
    private ViewNavigators viewNavigators;

    @ViewComponent
    private Button logoutButton;

    @ViewComponent
    private Button loginButton;
    @ViewComponent
    UserIndicator userIndicator;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Utente loggato se Authentication non è anonima e autenticata
        boolean loggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        logoutButton.setVisible(loggedIn);
       loginButton.setVisible(!loggedIn);
       userIndicator.setVisible(loggedIn);
    }





    @Subscribe("loginButton")
    public void onLoginButtonClick(ClickEvent<Button> event) {
        viewNavigators.view(this, LoginView.class).navigate();
    }

}
