package com.company.sitovetrina.view.home;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.entity.Newsletter;
import com.company.sitovetrina.view.main.MainView;
import com.company.sitovetrina.view.servizi.Servizi;
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
import io.jmix.email.EmailAttachment;
import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;
@AnonymousAllowed
@Route(value = "home", layout = MainView.class)
@ViewController(id = "Home")
@ViewDescriptor(path = "home.xml")
public class Home extends StandardView {
    @ViewComponent
    private Div cookieBanner;

    @ViewComponent
    private Button acceptCookiesButton;

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
    private Image carouselFoto4;
    @ViewComponent
    private Image carouselFoto5;
    @ViewComponent
    private Image carouselFoto6;
    @ViewComponent
    private Image carouselFoto7;

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
    @Autowired
    private Emailer emailer;
    @Autowired
    private ViewNavigators viewNavigators;

    @Subscribe(id = "serviceButton", subject = "clickListener")
    public void onServiceButtonClick(final ClickEvent<JmixButton> event) {
        viewNavigators.view(this, Servizi.class).navigate();
    }


    private Optional<Path> getLogoPath() {
        Path dir = Paths.get(filesPath);
        String[] extensions = {".png", ".jpg", ".jpeg", ".gif", ".webp"};

        for (String suffix : new String[]{"-new", "-old", ""}) {
            for (String ext : extensions) {
                Path p = dir.resolve("logo" + suffix + ext);
                if (Files.exists(p)) {
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Restituisce l’immagine del logo come stringa Base64 data:image/... (per embedding inline).
     */
    private String getLogoBase64() {
        try {
            Optional<Path> logoPathOpt = getLogoPath();
            if (logoPathOpt.isEmpty()) {
                return ""; // nessun logo trovato
            }
            Path logoPath = logoPathOpt.get();
            byte[] bytes = Files.readAllBytes(logoPath);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String extension = "";

            String name = logoPath.getFileName().toString();
            int dot = name.lastIndexOf('.');
            if (dot > 0) {
                extension = name.substring(dot + 1);
            }

            return "data:image/" + extension + ";base64," + base64;
        } catch (IOException e) {
            System.err.println("Errore lettura logo: " + e.getMessage());
            return "";
        }
    }

    /**
     * Crea il corpo HTML dell’email, includendo il logo inline in Base64.
     */
    private String generateConfirmationEmailHtml(String email, String logoBase64) {
        return """
    <html>
    <body style="margin:0; padding:0; background-color:#f3f4f6; font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
        <table role="presentation" cellpadding="0" cellspacing="0" width="100%%"
               style="background-color:#f3f4f6; padding:40px 0;">
            <tr>
                <td align="center">
                    <table role="presentation" cellpadding="0" cellspacing="0" width="600"
                           style="background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.08);">
                        <!-- Logo -->
                        <tr>
                            <td align="center" style="padding:40px 20px 10px 20px;">
                                <img src="%s" alt="Logo" width="160"
                                     style="display:block; margin:auto; border:0;">
                            </td>
                        </tr>
                        <!-- Titolo -->
                        <tr>
                            <td align="center" style="padding:0 40px 10px 40px;">
                                <h1 style="font-size:24px; font-weight:600; color:#222222; margin:30px 0 10px 0;">
                                    Benvenuto nella nostra Newsletter
                                </h1>
                            </td>
                        </tr>
                        <!-- Corpo -->
                        <tr>
                            <td align="left" style="padding:0 40px 20px 40px;">
                                <p style="font-size:16px; line-height:1.6; color:#444444;">
                                    Ciao <strong>%s</strong>,<br><br>
                                    grazie per esserti iscritto alla nostra newsletter!
                                    Da oggi riceverai in anteprima notizie, offerte esclusive e aggiornamenti dal nostro sito.
                                </p>
                            </td>
                        </tr>
                        <!-- Bottone -->
                        <tr>
                            <td align="center" style="padding:20px 40px 40px 40px;">
                                <a href="https://www.tuosito.it"
                                   style="background-color:#d42a3b; color:#000000; text-decoration:none;
                                          padding:12px 28px; border-radius:6px; font-weight:600; display:inline-block;">
                                    Visita il nostro sito
                                </a>
                            </td>
                        </tr>
                        <!-- Footer -->
                        <tr>
                            <td align="center" style="padding:20px 40px 40px 40px; border-top:1px solid #e5e7eb;">
                                <p style="font-size:13px; color:#999999; margin:0;">
                                    © 2025 <strong>Sito Vetrina</strong> · Tutti i diritti riservati<br>
                                    <a href="https://www.tuosito.it/privacy"
                                       style="color:#a48b5f; text-decoration:none;">Privacy Policy</a>
                                </p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        <p style="font-size:13px; color:#999999; text-align:center; margin-top:30px;">
            Se non vuoi più ricevere queste email,
            <a href="https://www.tuosito.it/unsubscribe?email=%s"
               style="color:#a48b5f; text-decoration:none;">clicca qui per disiscriverti</a>.
        </p>
    </body>
    </html>
    """.formatted(logoBase64, email, email);
    }


    @Subscribe("newsletterButton")
    public void onNewsletterButtonClick(ClickEvent<Button> event) {
        String email = newsletterEmail.getValue();

        // --- Validazione email ---
        if (email == null || email.isBlank()) {
            notifications.create("Inserisci una email!")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            notifications.create("Formato email non valido!")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        // --- Controllo se già iscritta ---
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

        // --- Invio mail di conferma ---
        try {
            String logoBase64 = getLogoBase64();
            String body = generateConfirmationEmailHtml(email, logoBase64);

            EmailInfo confirmationEmail = EmailInfoBuilder.create()
                    .setAddresses(email)
                    .setSubject("🎉 Benvenuto nella Newsletter di Sito Vetrina!")
                    .setBody(body)
                    .setBodyContentType("text/html")
                    .build();

            emailer.sendEmail(confirmationEmail);

            notifications.create("Iscrizione completata! Controlla la tua email per la conferma.")
                    .withType(Notifications.Type.SUCCESS)
                    .show();

        } catch (Exception e) {
            System.err.println("Errore invio mail di conferma: " + e.getMessage());
            notifications.create("Iscrizione salvata, ma errore nell'invio email.")
                    .withType(Notifications.Type.WARNING)
                    .show();
        }

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
        setStreamResourceIfExists(carouselFoto4, "fotocarosello4-new");
        setStreamResourceIfExists(carouselFoto5, "fotocarosello5-new");
        setStreamResourceIfExists(carouselFoto6, "fotocarosello6-new");
        setStreamResourceIfExists(carouselFoto7, "fotocarosello7-new");

        // GIF animata
        setGifDirect("mediaGif"); // baseName senza suffisso -new



        // Margine inferiore
        getContent().getStyle().set("margin-bottom", "40vh");

        getElement().executeJs("""
        if (!window.localStorage.getItem('cookiesAccepted')) {
            document.getElementById('cookieBanner').style.display = 'flex';
        }
    """);
    }

    @Subscribe("acceptCookiesButton")
    public void onAcceptCookiesClick(ClickEvent<Button> event) {
        cookieBanner.setVisible(false);
        getElement().executeJs("window.localStorage.setItem('cookiesAccepted', 'true');");
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

 /* @Subscribe
  public void onBeforeShow(BeforeShowEvent event) {
      Configsitovetrina config = dataManager.load(Configsitovetrina.class).id(1).optional().orElse(null);
      if (config == null) return;

      boolean loggedIn = currentAuthentication.getUser().getAuthorities().stream()
              .noneMatch(auth -> "anonymous-role".equals(auth.getAuthority()));

      Configsitovetrina cfg = config; // già caricato
      if (cfg != null) {
          String orari = cfg.getOrari();
          String linkMaps = cfg.getLinkMaps();
          String indirizzo = cfg.getIndirizzo();
          String whatsapp = cfg.getLinkWhatsap(); // numero internazionale senza '+', es: 393401234567

          // --- Orari ---
          if (orari != null && !orari.isBlank()) {
              String[] righe = orari.split("\\r?\\n");
              StringBuilder htmlOrari = new StringBuilder("<ul style='list-style:none; padding:0; margin:0;'>");

              // Giorni della settimana (in maiuscolo per il match)
              String[] giorni = {"LUNEDI", "MARTEDI", "MERCOLEDI", "GIOVEDI", "VENERDI", "SABATO", "DOMENICA"};

              for (String riga : righe) {
                  String testo = riga.trim();
                  String giornoColorato = testo;

                  // Cerca il giorno nella riga e lo colora
                  for (String giorno : giorni) {
                      if (testo.toUpperCase().startsWith(giorno)) {
                          // Evidenzia il giorno in rosso (accent color)
                          String parteGiorno = testo.substring(0, giorno.length());
                          String parteResto = testo.substring(giorno.length()).trim();

                          giornoColorato = "<span style='color: var(--accent-glow); font-weight:700;'>" + parteGiorno + "</span>"
                                  + " <span style='color:#FFFFFF;'>" + parteResto + "</span>";
                          break;
                      }
                  }

                  htmlOrari.append("<li style='padding:4px 0; font-weight:500;'>").append(giornoColorato).append("</li>");
              }

              htmlOrari.append("</ul>");
              getElement().executeJs("document.getElementById('orariList').innerHTML = $0;", htmlOrari.toString());
          }

          if (linkMaps != null && !linkMaps.isBlank()) {
              // Rimuove il contenuto statico e inserisce l’HTML del backend
              getElement().executeJs("""
        const container = document.querySelector('.luogo-card .map-container');
        if (container) {
            container.innerHTML = $0; // inserisce direttamente l’iframe da backend
        }
        const addrEl = document.getElementById('addressText');
        if (addrEl) addrEl.innerText = $1 || '';
    """, linkMaps, indirizzo);
          }

          // --- POPOLAMENTO FOOTER + SOCIAL + WHATSAPP ---
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
            if ($5) socialDiv.innerHTML += `<img src="https://i.postimg.cc/wvKhz8r6/icons8-facebook-50.png" style="cursor:pointer; margin-right:8px;" onclick="window.open('$5','_blank')"/>`;
            if ($6) socialDiv.innerHTML += `<img src="https://i.postimg.cc/RV0JjZ07/icons8-instagram-48.png" style="cursor:pointer; margin-right:8px;" onclick="window.open('$6','_blank')"/>`;
            if ($7) socialDiv.innerHTML += `<img src="https://i.postimg.cc/nVYPwVhy/icons8-tic-toc-50.png" style="cursor:pointer; margin-right:8px;" onclick="window.open('$7','_blank')"/>`;
            if ($8) socialDiv.innerHTML += `<img src="https://cdn.jsdelivr.net/gh/simple-icons/simple-icons/icons/whatsapp.svg" style="cursor:pointer; margin-right:8px;" onclick="location.href='https://wa.me/${$8}'"/>`;

            // --- Badge WhatsApp fisso in basso a destra ---
            if ($8) {
            if (!document.getElementById('whatsappBadge')) {
            let badge = document.createElement('a');
                badge.href = 'https://wa.me/' + $8;
                badge.target = '_blank';
                badge.id = 'whatsappBadge';
                badge.style.position = 'fixed';
                badge.style.bottom = '20px';
                badge.style.right = '20px';
                badge.style.width = '56px';
                badge.style.height = '56px';
                badge.style.borderRadius = '50%';
                badge.style.backgroundColor = '#25D366';
                badge.style.display = 'flex';
                badge.style.justifyContent = 'center';
                badge.style.alignItems = 'center';
                badge.style.boxShadow = '0 4px 12px rgba(0,0,0,0.2)';
                badge.style.cursor = 'pointer';
                badge.style.zIndex = '9999';
               badge.innerHTML = `<img src="https://cdn.jsdelivr.net/gh/simple-icons/simple-icons/icons/whatsapp.svg" style="width:28px; height:28px; filter: invert(1);">`;
                document.body.appendChild(badge);
            }                
            }
        """,
                  config.getNomeSito(),
                  config.getRagioneSociale(),
                  config.getIndirizzo(),
                  config.getTelefono(),
                  config.getEmailContatti(),
                  config.getLinkFacebook(),
                  config.getLinkInsta(),
                  config.getLinkTikTok(),
                  whatsapp
          );
      }

      // --- CAROSELLO CINEMATICO ---
      getElement().executeJs("""
        const carousel = document.querySelector('.carousel');
        if (!carousel) return;

        const slides = Array.from(carousel.querySelectorAll('.carousel-image'));
        if (slides.length === 0) return;

        let index = 0;
        let timer = null;
        let isPaused = false;

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

        const dots = document.createElement('div');
        dots.className = 'carousel-dots';
        slides.forEach((_, i) => {
            const dot = document.createElement('span');
            dot.className = 'carousel-dot';
            dot.addEventListener('click', () => goTo(i));
            dots.appendChild(dot);
        });
        carousel.appendChild(dots);

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

        function startAuto() {
            if (timer) clearInterval(timer);
            timer = setInterval(() => { if (!isPaused) next(); }, 3000);
        }

        carousel.addEventListener('mouseenter', () => isPaused = true);
        carousel.addEventListener('mouseleave', () => isPaused = false);

        let startX = 0, diffX = 0;
        carousel.addEventListener('touchstart', e => startX = e.touches[0].clientX);
        carousel.addEventListener('touchmove', e => diffX = e.touches[0].clientX - startX);
        carousel.addEventListener('touchend', () => {
            if (Math.abs(diffX) > 50) diffX < 0 ? next() : prev();
            diffX = 0;
        });

        window.addEventListener('keydown', e => {
            if (e.key === 'ArrowRight') next();
            if (e.key === 'ArrowLeft') prev();
        });

        slides[0].style.opacity = '1';
        slides[0].style.zIndex = '2';
        dots.firstChild.classList.add('active');
        startAuto();
    """);

      // --- ANIMAZIONE FADE-IN ON SCROLL ---
      getElement().executeJs("""
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
  }*/
 @Subscribe
 public void onBeforeShow(BeforeShowEvent event) {
     Configsitovetrina config = dataManager.load(Configsitovetrina.class).id(1).optional().orElse(null);
     if (config == null) return;

     boolean loggedIn = currentAuthentication.getUser().getAuthorities().stream()
             .noneMatch(auth -> "anonymous-role".equals(auth.getAuthority()));

     String orari = config.getOrari();
     String linkMaps = config.getLinkMaps();
     String indirizzo = config.getIndirizzo();
     String whatsapp = config.getLinkWhatsap(); // numero internazionale senza '+'

     // --- ORARI ---
     if (orari != null && !orari.isBlank()) {
         String[] righe = orari.split("\\r?\\n");
         StringBuilder htmlOrari = new StringBuilder("<ul style='list-style:none; padding:0; margin:0;'>");
         String[] giorni = {"LUNEDI", "MARTEDI", "MERCOLEDI", "GIOVEDI", "VENERDI", "SABATO", "DOMENICA"};

         for (String riga : righe) {
             String testo = riga.trim();
             String giornoColorato = testo;

             for (String giorno : giorni) {
                 if (testo.toUpperCase().startsWith(giorno)) {
                     String parteGiorno = testo.substring(0, giorno.length());
                     String parteResto = testo.substring(giorno.length()).trim();
                     giornoColorato = "<span style='color: var(--accent-glow); font-weight:700;'>" + parteGiorno + "</span>"
                             + " <span style='color:#FFFFFF;'>" + parteResto + "</span>";
                     break;
                 }
             }

             htmlOrari.append("<li style='padding:4px 0; font-weight:500;'>").append(giornoColorato).append("</li>");
         }
         htmlOrari.append("</ul>");
         getElement().executeJs("document.getElementById('orariList').innerHTML = $0;", htmlOrari.toString());
     }

     // --- MAPPA ---
     if (linkMaps != null && !linkMaps.isBlank()) {
         getElement().executeJs("""
            const container = document.querySelector('.luogo-card .map-container');
            if (container) container.innerHTML = $0;
            const addrEl = document.getElementById('addressText');
            if (addrEl) addrEl.innerText = $1 || '';
        """, linkMaps, indirizzo);
     }

     // --- FOOTER + SOCIAL + WHATSAPP ---
     getElement().executeJs("""
        const nome = $0;
        const ragione = $1;
        const indirizzo = $2;
        const tel = $3;
        const email = $4;
        const linkFacebook = $5;
        const linkInstagram = $6;
        const linkTikTok = $7;
        const whatsapp = $8;

        document.getElementById('footerNomeSito').innerText = nome;
        document.getElementById('footerRagioneSociale').innerText = ragione;
        document.getElementById('footerIndirizzo').innerText = indirizzo;
        document.getElementById('footerTelefono').innerText = tel;
        document.getElementById('footerEmail').innerText = email;

        const socialDiv = document.getElementById('footerSocial');
        socialDiv.innerHTML = '';

        if (linkFacebook) {
            const fbImg = document.createElement('img');
            fbImg.src = 'https://i.postimg.cc/wvKhz8r6/icons8-facebook-50.png';
            fbImg.style.cursor = 'pointer';
            fbImg.style.marginRight = '8px';
            fbImg.addEventListener('click', () => window.open(linkFacebook, '_blank'));
            socialDiv.appendChild(fbImg);
        }

        if (linkInstagram) {
            const instaImg = document.createElement('img');
            instaImg.src = 'https://i.postimg.cc/RV0JjZ07/icons8-instagram-48.png';
            instaImg.style.cursor = 'pointer';
            instaImg.style.marginRight = '8px';
            instaImg.addEventListener('click', () => window.open(linkInstagram, '_blank'));
            socialDiv.appendChild(instaImg);
        }

        if (linkTikTok) {
            const tikImg = document.createElement('img');
            tikImg.src = 'https://i.postimg.cc/nVYPwVhy/icons8-tic-toc-50.png';
            tikImg.style.cursor = 'pointer';
            tikImg.style.marginRight = '8px';
            tikImg.addEventListener('click', () => window.open(linkTikTok, '_blank'));
            socialDiv.appendChild(tikImg);
        }

        if (whatsapp) {
            const waImg = document.createElement('img');
            waImg.src = 'https://cdn.jsdelivr.net/gh/simple-icons/simple-icons/icons/whatsapp.svg';
            waImg.style.cursor = 'pointer';
            waImg.style.marginRight = '8px';
            waImg.addEventListener('click', () => location.href = 'https://wa.me/' + whatsapp);
            socialDiv.appendChild(waImg);

            // Badge WhatsApp fisso
            if (!document.getElementById('whatsappBadge')) {
                const badge = document.createElement('a');
                badge.href = 'https://wa.me/' + whatsapp;
                badge.target = '_blank';
                badge.id = 'whatsappBadge';
                badge.style.position = 'fixed';
                badge.style.bottom = '20px';
                badge.style.right = '20px';
                badge.style.width = '56px';
                badge.style.height = '56px';
                badge.style.borderRadius = '50%';
                badge.style.backgroundColor = '#25D366';
                badge.style.display = 'flex';
                badge.style.justifyContent = 'center';
                badge.style.alignItems = 'center';
                badge.style.boxShadow = '0 4px 12px rgba(0,0,0,0.2)';
                badge.style.cursor = 'pointer';
                badge.style.zIndex = '9999';
                badge.innerHTML = `<img src="https://cdn.jsdelivr.net/gh/simple-icons/simple-icons/icons/whatsapp.svg" style="width:28px; height:28px; filter: invert(1);">`;
                document.body.appendChild(badge);
            }
        }
    """,
             config.getNomeSito(),
             config.getRagioneSociale(),
             config.getIndirizzo(),
             config.getTelefono(),
             config.getEmailContatti(),
             config.getLinkFacebook(),
             config.getLinkInsta(),
             config.getLinkTikTok(),
             whatsapp
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

        const dots = document.createElement('div');
        dots.className = 'carousel-dots';
        slides.forEach((_, i) => {
            const dot = document.createElement('span');
            dot.className = 'carousel-dot';
            dot.addEventListener('click', () => goTo(i));
            dots.appendChild(dot);
        });
        carousel.appendChild(dots);

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

        function goTo(i) { index = (i + slides.length) % slides.length; show(index); }
        function next() { goTo(index + 1); }
        function prev() { goTo(index - 1); }

        function startAuto() {
            if (timer) clearInterval(timer);
            timer = setInterval(() => { if (!isPaused) next(); }, 3000);
        }

        carousel.addEventListener('mouseenter', () => isPaused = true);
        carousel.addEventListener('mouseleave', () => isPaused = false);

        let startX = 0, diffX = 0;
        carousel.addEventListener('touchstart', e => startX = e.touches[0].clientX);
        carousel.addEventListener('touchmove', e => diffX = e.touches[0].clientX - startX);
        carousel.addEventListener('touchend', () => {
            if (Math.abs(diffX) > 50) diffX < 0 ? next() : prev();
            diffX = 0;
        });

        window.addEventListener('keydown', e => {
            if (e.key === 'ArrowRight') next();
            if (e.key === 'ArrowLeft') prev();
        });

        slides[0].style.opacity = '1';
        slides[0].style.zIndex = '2';
        dots.firstChild.classList.add('active');
        startAuto();
    """);

     // --- ANIMAZIONE FADE-IN ON SCROLL ---
     getElement().executeJs("""
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.15 });

        document.querySelectorAll('.fade-in-section, .gallery-item').forEach(el => observer.observe(el));
    """);
 }
}