package com.company.sitovetrina.view.home;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.entity.Newsletter;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
@AnonymousAllowed
@Route(value = "home", layout = MainView.class)
@ViewController(id = "Home")
@ViewDescriptor(path = "home.xml")
public class Home extends StandardView {

    @ViewComponent
    private Image heroImage;
    @ViewComponent("mediaSection")
    private Div mediaGifContainer;
    @ViewComponent
    private Image foto1;
    @ViewComponent
    private Image foto2;
    @ViewComponent
    private Image foto3;
    @ViewComponent
    private Image foto4;
    @ViewComponent
    private Image logo;
    @ViewComponent
    private Div aboutText;
    @ViewComponent
    private Image carouselFoto1;
    @ViewComponent
    private Image carouselFoto2;
    @ViewComponent
    private Image carouselFoto3;
    @ViewComponent
    private H1 homeTitle;
    @Value("${site.files.path}")
    private String filesPath;
    @ViewComponent
    private Image mediaGif;
    @Autowired
    private DataManager dataManager;
    @ViewComponent
    private H3 aboutTitle;
    @ViewComponent
    private Div descrText;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private TextField newsletterEmail;
    @ViewComponent
    private Button newsletterButton;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @ViewComponent
    private H3 aboutTitle2;
    @ViewComponent
    private Div aboutText2;

   /* @Subscribe("newsletterButton")
    public void onNewsletterButtonClick(ClickEvent<Button> event) {*/
       /* String email = newsletterEmail.getValue();

        if (email == null || email.isBlank()) {
            notifications.create("Inserisci una email valida!")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        // simuliamo l’iscrizione
        notifications.create("Adesso sei iscritta/o alla newsletter!")
                .withType(Notifications.Type.SUCCESS)
                .show();

        newsletterEmail.clear();*/
        @Subscribe("newsletterButton")
        public void onNewsletterButtonClick(ClickEvent<Button> event) {
            String email = newsletterEmail.getValue();

            // --- Verifica che sia un indirizzo email valido ---
            if (email == null || email.isBlank()) {
                notifications.create("Inserisci una email!")
                        .withType(Notifications.Type.WARNING)
                        .show();
                return;
            }

            // Regex base per email
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                notifications.create("Formato email non valido!")
                        .withType(Notifications.Type.WARNING)
                        .show();
                return;
            }

            // --- Controllo se già presente ---
            Optional<Newsletter> existing = dataManager.load(Newsletter.class)
                    .query("select n from Newsletter n where n.email = :email")
                    .parameter("email", email)
                    .optional();

            if (existing.isPresent()) {
                notifications.create("Questa email è già iscritta alla newsletter.")
                        .withType(Notifications.Type.WARNING)
                        .show();
                return;
            }

            // --- Salvataggio nuova iscrizione ---
            Newsletter n = dataManager.create(Newsletter.class);
            n.setEmail(email);
            dataManager.save(n);

            notifications.create("Iscrizione completata con successo!")
                    .withType(Notifications.Type.SUCCESS)
                    .show();

            newsletterEmail.clear();
        }


    @Subscribe
    public void onInit(InitEvent event) {
        Optional<Configsitovetrina> cfgOpt = dataManager.load(Configsitovetrina.class)
                .all()
                .optional();

        cfgOpt.ifPresent(cfg -> {
            if (cfg.getAboutTesto() != null) {
                aboutTitle.setText(cfg.getAboutTitolo());
                aboutText.setText(cfg.getAboutTesto());
                homeTitle.setText(cfg.getNomeSito());
                descrText.setText(cfg.getDescrizioneSito());
                aboutTitle2.setText(cfg.getAboutTitolo2());
                aboutText2.setText(cfg.getAboutTesto2());
            }
        });


        // immagini statiche
        setStreamResourceIfExists(heroImage, "hero-new");
        setStreamResourceIfExists(foto1, "foto1-new");
        setStreamResourceIfExists(foto2, "foto2-new");
        setStreamResourceIfExists(foto3, "foto3-new");
        setStreamResourceIfExists(foto4, "foto4-new");
        setStreamResourceIfExists(logo, "logo-new");
        setStreamResourceIfExists(carouselFoto1, "fotocarosello1-new");
        setStreamResourceIfExists(carouselFoto2, "fotocarosello2-new");
        setStreamResourceIfExists(carouselFoto3, "fotocarosello3-new");

        // GIF animata
        setGifDirect("mediaGif"); // baseName senza suffisso -new



        // Margine inferiore
        getContent().getStyle().set("margin-bottom", "40vh");
    }

    private void setStreamResourceIfExists(Image img, String baseName) {
        Path dir = Paths.get(filesPath);
        String[] extensions = {".png", ".jpg", ".jpeg", ".gif", ".webp"};
        Path found = null;
        try {
            for (String suffix : new String[]{"-new", "-old", ""}) {
                for (String ext : extensions) {
                    Path p = dir.resolve(baseName + suffix + ext);
                    if (Files.exists(p)) { found = p; break; }
                }
                if (found != null) break;
            }

            if (found != null) {
                Path finalFoundPath = found;
                StreamResource res = new StreamResource(found.getFileName().toString(),
                        () -> {
                            try {
                                return Files.newInputStream(finalFoundPath);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                String mimeType = Files.probeContentType(found);
                if (mimeType == null && found.toString().toLowerCase().endsWith(".gif"))
                    mimeType = "image/gif";
                if (mimeType != null) res.setContentType(mimeType);

                img.setSrc(res);
                img.setAlt(found.getFileName().toString());
                img.getElement().executeJs("this.src=this.src + '?v=' + Date.now();");
            }
        } catch (IOException e) {
            Notification.show("Errore caricamento immagine: " + e.getMessage(), 4000, Notification.Position.TOP_CENTER);
        }
    }

    private void setGifDirect(String baseName) {
        Path dir = Paths.get(filesPath);
        String[] extensions = {".gif"};
        Path found = null;

        for (String suffix : new String[]{"-new", "-old", ""}) {
            for (String ext : extensions) {
                Path p = dir.resolve(baseName + suffix + ext);
                if (Files.exists(p)) {
                    found = p;
                    break;
                }
            }
            if (found != null) break;
        }

        if (found != null) {
            Path finalFound = found;
            StreamResource res = new StreamResource(found.getFileName().toString(),
                    () -> {
                        try {
                            return Files.newInputStream(finalFound);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            res.setContentType("image/gif");

            mediaGif.setSrc(res);
            mediaGif.setAlt("GIF Home");
            mediaGif.setWidth("100%");
            mediaGif.setHeight("auto");
            mediaGif.getStyle()
                    .set("min-height", "380px")
                    .set("object-fit", "contain");
        } else {
            Notification.show("GIF non trovata: " + baseName, 4000, Notification.Position.TOP_CENTER);
        }
    }





    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        Configsitovetrina config = dataManager.load(Configsitovetrina.class).id(1).optional().orElse(null);
        if (config == null) return;
        boolean loggedIn = currentAuthentication.getUser().getAuthorities().stream()
                .noneMatch(auth -> "anonymous-role".equals(auth.getAuthority()));



        // --- POPOLAMENTO FOOTER ---
        getElement().executeJs("""
        const nome = $0;
        const ragione = $1;
        const indirizzo = $2;
        const tel = $3;
        const email = $4;
        const socialDiv = document.getElementById('footerSocial');
        document.getElementById('footerNomeSito').innerText = nome;
        document.getElementById('footerRagioneSociale').innerText = ragione;
        document.getElementById('footerIndirizzo').innerText = indirizzo;
        document.getElementById('footerTelefono').innerText = tel;
        document.getElementById('footerEmail').innerText = email;

        socialDiv.innerHTML = '';
        if ($5) socialDiv.innerHTML += `<img src="https://i.postimg.cc/wvKhz8r6/icons8-facebook-50.png" style="cursor:pointer" onclick="window.open('$5','_blank')"/>`;
        if ($6) socialDiv.innerHTML += `<img src="https://i.postimg.cc/RV0JjZ07/icons8-instagram-48.png" style="cursor:pointer" onclick="window.open('$6','_blank')"/>`;
        if ($7) socialDiv.innerHTML += `<img src="https://i.postimg.cc/nVYPwVhy/icons8-tic-toc-50.png" style="cursor:pointer" onclick="window.open('$7','_blank')"/>`;
    """,
                config.getNomeSito(),
                config.getRagioneSociale(),
                config.getIndirizzo(),
                config.getTelefono(),
                config.getEmailContatti(),
                config.getLinkFacebook(),
                config.getLinkInsta(),
                config.getLinkTikTok()
        );

        // --- CAROSELLO CINEMATICO ---
        getElement().executeJs("""
        const carousel = document.querySelector('.carousel');
        if (!carousel) return;

        const slides = Array.from(carousel.querySelectorAll('.carousel-image'));
        if (slides.length === 0) return;

        let index = 0;
        let timer = null;
        let isPaused = false;

        // --- Stili di base ---
        slides.forEach(s => {
            s.style.position = 'absolute';
            s.style.top = '0';
            s.style.left = '0';
            s.style.width = '100%';
            s.style.height = '100%';
            s.style.objectFit = 'cover';
            s.style.opacity = '0';
            s.style.transition = 'opacity 1.2s ease';
            s.style.zIndex = '0';
        });

        // --- Creazione indicatori ---
        const dots = document.createElement('div');
        dots.className = 'carousel-dots';
        slides.forEach((_, i) => {
            const dot = document.createElement('span');
            dot.className = 'carousel-dot';
            dot.addEventListener('click', () => goTo(i));
            dots.appendChild(dot);
        });
        carousel.appendChild(dots);

        // --- Pulsanti freccia ---
        const left = document.createElement('div');
        const right = document.createElement('div');
        left.className = 'carousel-btn left';
        right.className = 'carousel-btn right';
        left.innerHTML = '❮';
        right.innerHTML = '❯';
        left.onclick = prev;
        right.onclick = next;
        carousel.appendChild(left);
        carousel.appendChild(right);

        // --- Effetto di transizione cinematico ---
        function cinematicShow(current, next) {
            if (!current || !next) return;
            current.animate([
                { transform: 'scale(1) translateY(0px)', opacity: 1 },
                { transform: 'scale(1.1) translateY(30px)', opacity: 0 }
            ], { duration: 1200, easing: 'cubic-bezier(.4,0,.2,1)', fill: 'forwards' });

            next.animate([
                { transform: 'scale(0.95) translateY(-20px)', opacity: 0 },
                { transform: 'scale(1) translateY(0px)', opacity: 1 }
            ], { duration: 1200, easing: 'cubic-bezier(.4,0,.2,1)', fill: 'forwards' });
        }

        // --- Mostra slide ---
        function show(i) {
            const prevSlide = slides[index];
            const nextSlide = slides[i];
            slides.forEach(s => s.style.zIndex = '0');
            prevSlide.style.zIndex = '1';
            nextSlide.style.zIndex = '2';
            cinematicShow(prevSlide, nextSlide);
            slides.forEach((s, n) => s.style.opacity = n === i ? '1' : '0');
            dots.querySelectorAll('.carousel-dot').forEach((d, n) =>
                d.classList.toggle('active', n === i)
            );
        }

        function goTo(i) {
            index = (i + slides.length) % slides.length;
            show(index);
        }
        function next() { goTo(index + 1); }
        function prev() { goTo(index - 1); }

        // --- AutoPlay ---
        function startAuto() {
            if (timer) clearInterval(timer);
            timer = setInterval(() => { if (!isPaused) next(); }, 3000);
        }

        carousel.addEventListener('mouseenter', () => isPaused = true);
        carousel.addEventListener('mouseleave', () => isPaused = false);

        // --- Swipe mobile ---
        let startX = 0, diffX = 0;
        carousel.addEventListener('touchstart', e => startX = e.touches[0].clientX);
        carousel.addEventListener('touchmove', e => diffX = e.touches[0].clientX - startX);
        carousel.addEventListener('touchend', () => {
            if (Math.abs(diffX) > 50) diffX < 0 ? next() : prev();
            diffX = 0;
        });

        // --- Tastiera ---
        window.addEventListener('keydown', e => {
            if (e.key === 'ArrowRight') next();
            if (e.key === 'ArrowLeft') prev();
        });

        // --- Avvio ---
        slides[0].style.opacity = '1';
        slides[0].style.zIndex = '2';
        dots.firstChild.classList.add('active');
        startAuto();
    """);

    getElement().executeJs("""
    // --- ANIMAZIONE FADE-IN ON SCROLL ---
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.15 });

    document.querySelectorAll('.fade-in-section, .gallery-item').forEach(el => {
        observer.observe(el);
    });
""");

    }
}