package com.company.sitovetrina.view.home;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import io.jmix.core.DataManager;
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

    @Subscribe
    public void onInit(InitEvent event) {
        initNavbarScrollEffect();
        Optional<Configsitovetrina> cfgOpt = dataManager.load(Configsitovetrina.class).id(1).optional();
        cfgOpt.ifPresent(cfg -> {
            if (cfg.getAboutTesto() != null) {
                aboutTitle.setText(cfg.getAboutTitolo());
                aboutText.setText(cfg.getAboutTesto());
                homeTitle.setText(cfg.getNomeSito());
                descrText.setText(cfg.getDescrizioneSito());
            }
        });

        // immagini statiche
        setStreamResourceIfExists(heroImage, "hero-new");
        setStreamResourceIfExists(foto1, "foto1-new");
        setStreamResourceIfExists(foto2, "foto2-new");
        setStreamResourceIfExists(foto3, "foto3-new");
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
        getElement().executeJs("""
        const carousel = document.querySelector('.carousel');
        const slides = Array.from(carousel.querySelectorAll('.carousel-image'));
        const nextBtn = document.getElementById('nextBtn');
        const prevBtn = document.getElementById('prevBtn');
        let index = 0;
        let timer = null;
        let isPaused = false;
        let startX = 0;
        let diffX = 0;

        // Creazione indicatori dinamici
        const indicators = document.createElement('div');
        indicators.className = 'carousel-indicators';
        slides.forEach((_, i) => {
            const dot = document.createElement('span');
            dot.className = 'carousel-dot';
            dot.addEventListener('click', () => goToSlide(i));
            indicators.appendChild(dot);
        });
        carousel.appendChild(indicators);

        function show(i) {
            slides.forEach((img, n) => {
                img.classList.remove('active');
                img.style.zIndex = n === i ? 1 : 0;
            });
            const current = slides[i];
            current.classList.add('active');
            // Transizione cinematica (leggero parallax)
            current.animate([
                { transform: 'scale(1.04) translateY(12px)', opacity: 0 },
                { transform: 'scale(1.0) translateY(0)', opacity: 1 }
            ], { duration: 900, easing: 'cubic-bezier(.16,.84,.29,1)', fill: 'forwards' });

            // Aggiorna indicatori
            indicators.querySelectorAll('.carousel-dot').forEach((d, n) =>
                d.classList.toggle('active', n === i)
            );
        }

        function goToSlide(i) {
            index = (i + slides.length) % slides.length;
            show(index);
        }

        function next() { goToSlide(index + 1); }
        function prev() { goToSlide(index - 1); }

        function startAuto() {
            if (timer) clearInterval(timer);
            timer = setInterval(() => { if (!isPaused) next(); }, 6000);
        }

        // Controlli manuali
        nextBtn.onclick = next;
        prevBtn.onclick = prev;

        // Hover: pausa autoplay
        carousel.addEventListener('mouseenter', () => isPaused = true);
        carousel.addEventListener('mouseleave', () => isPaused = false);

        // Swipe mobile
        carousel.addEventListener('touchstart', e => startX = e.touches[0].clientX);
        carousel.addEventListener('touchmove', e => diffX = e.touches[0].clientX - startX);
        carousel.addEventListener('touchend', () => {
            if (Math.abs(diffX) > 50) diffX < 0 ? next() : prev();
            diffX = 0;
        });

        // Navigazione tastiera
        window.addEventListener('keydown', e => {
            if (e.key === 'ArrowRight') next();
            if (e.key === 'ArrowLeft') prev();
        });

        // Avvio
        show(index);
        startAuto();
    """);
    }

    private void initNavbarScrollEffect() {
        getElement().executeJs("""
        const header = document.getElementById('header');
        if (!header) return;

        // La navbar è visibile fin dall'inizio
        header.style.opacity = '1';
        header.classList.remove('scrolled');

        let lastScrollY = 0;
        let ticking = false;

        function updateNavbar() {
            const currentY = window.scrollY || window.pageYOffset;
            const goingDown = currentY > lastScrollY;

            if (currentY > 80 && goingDown) {
                // Scorrimento verso il basso: compatta la navbar
                header.classList.add('scrolled');
            } else if (currentY <= 20) {
                // Torna in cima: ripristina navbar grande
                header.classList.remove('scrolled');
            }

            lastScrollY = currentY;
            ticking = false;
        }

        window.addEventListener('scroll', () => {
            if (!ticking) {
                window.requestAnimationFrame(updateNavbar);
                ticking = true;
            }
        });
    """);
    }





}
