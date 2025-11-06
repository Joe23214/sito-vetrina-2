package com.company.sitovetrina.view.main;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.view.home.Home;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.applayout.JmixAppLayout;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;

@Route("")
@ViewController("MainView")
@ViewDescriptor("main-view.xml")
public class MainView extends StandardMainView {
    @ViewComponent
    private Div navBarPrinc;
    @Autowired
    private ViewNavigators viewNavigators;
    @ViewComponent
    private Div footerContainer;
    private HorizontalLayout footer;
    @Autowired
    private DataManager dataManager;
    @Subscribe
    public void onInit(final InitEvent event) {
        // Ottieni il JmixAppLayout principale
        JmixAppLayout appLayout = getContent();
        // Carica la configurazione con id = 1, altrimenti usa default
        Configsitovetrina config;
        try {
            config = dataManager.load(Configsitovetrina.class).id(1).optional().orElse(null);
        } catch (Exception e) {
            config = null;
        }

        // Valori di default
        String nomeSito = config != null && config.getNomeSito() != null ? config.getNomeSito() : "";
        String ragioneSociale = config != null && config.getRagioneSociale() != null ? config.getRagioneSociale() : "";
        String piva = config != null && config.getPiva() != null ? config.getPiva() : "";
        String indirizzo = config != null && config.getIndirizzo() != null ? config.getIndirizzo() : "";
        String cap = config != null && config.getCap() != null ? config.getCap() : "";
        String citta = config != null && config.getCitta() != null ? config.getCitta() : "";
        String telefono = config != null && config.getTelefono() != null ? config.getTelefono() : "";
        String email = config != null && config.getEmailContatti() != null ? config.getEmailContatti() : "";
        String descrizioneSito = config != null && config.getDescrizioneSito() != null ? config.getDescrizioneSito() : "";
        String testoFooter = config != null && config.getTestoFooter() != null ? config.getTestoFooter() : "";
        String linkFacebook = config != null && config.getLinkFacebook() != null ? config.getLinkFacebook() : "";
        String linkInstagram = config != null && config.getLinkInsta() != null ? config.getLinkInsta() : "";
        String linkTikTok = config != null && config.getLinkTikTok() != null ? config.getLinkTikTok() : "";

        // Footer principale
        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidthFull();
        footerLayout.getStyle()
                .set("background-color", "#6495ED")
                .set("color", "#ffffff")
                .set("padding", "40px 60px")
                .set("text-align", "left")
                .set("position", "fixed")
                .set("bottom", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("z-index", "10");
        footerLayout.setSpacing(true);

        // --- Colonna 1: Riferimenti ---
        VerticalLayout col1 = new VerticalLayout();
        col1.setSpacing(false);
        col1.setPadding(false);

        H3 col1Title = new H3("Riferimenti");
        col1Title.getStyle().set("color", "#ffffff");

        col1.add(
                col1Title,
                new Paragraph(nomeSito),
                new Paragraph(ragioneSociale),
                new Paragraph("P.IVA: " + piva),
                new Paragraph(indirizzo + " " + cap + ", " + citta)
        );

        // --- Colonna 2: Contatti ---
        VerticalLayout col2 = new VerticalLayout();
        col2.setSpacing(false);
        col2.setPadding(false);

        H3 col2Title = new H3("Contatti");
        col2Title.getStyle().set("color", "#ffffff");

        col2.add(
                col2Title,
                new Paragraph("Telefono: " + telefono),
                new Paragraph("Email: " + email),
                new Paragraph(descrizioneSito),
                new Paragraph(testoFooter)
        );

        // --- Colonna 3: Social ---
        VerticalLayout col3 = new VerticalLayout();
        col3.setSpacing(false);
        col3.setPadding(false);

        H3 col3Title = new H3("Seguici");
        col3Title.getStyle().set("color", "#ffffff");

        HorizontalLayout socialIcons = new HorizontalLayout();
        socialIcons.setSpacing(true);

        // Facebook
        if (!linkFacebook.isEmpty()) {
            Image fbIcon = new Image("https://i.postimg.cc/wvKhz8r6/icons8-facebook-50.png", "Facebook");
            fbIcon.setWidth("32px");
            fbIcon.setHeight("32px");
            fbIcon.getStyle().set("filter", "invert(1)");
            fbIcon.addClickListener(e -> UI.getCurrent().getPage().open(linkFacebook));
            socialIcons.add(fbIcon);
        }

        // Instagram
        if (!linkInstagram.isEmpty()) {
            Image igIcon = new Image("https://i.postimg.cc/RV0JjZ07/icons8-instagram-48.png", "Instagram");
            igIcon.setWidth("32px");
            igIcon.setHeight("32px");
            igIcon.getStyle().set("filter", "invert(1)");
            igIcon.addClickListener(e -> UI.getCurrent().getPage().open(linkInstagram));
            socialIcons.add(igIcon);
        }

        // TikTok
        if (!linkTikTok.isEmpty()) {
            Image ttIcon = new Image("https://i.postimg.cc/nVYPwVhy/icons8-tic-toc-50.png", "TikTok");
            ttIcon.setWidth("32px");
            ttIcon.setHeight("32px");
            ttIcon.getStyle().set("filter", "invert(1)");
            ttIcon.addClickListener(e -> UI.getCurrent().getPage().open(linkTikTok));
            socialIcons.add(ttIcon);
        }

        col3.add(col3Title, socialIcons);

        // Aggiungi tutte le colonne al footer principale
        footerLayout.add(col1, col2, col3);

        // Imposta layout responsive
        footerLayout.setFlexGrow(1, col1);
        footerLayout.setFlexGrow(1, col2);
        footerLayout.setFlexGrow(0, col3);

        // Aggiungi il footer alla fine del layout principale
        appLayout.addToNavbar(false, footerLayout);
    }

}
