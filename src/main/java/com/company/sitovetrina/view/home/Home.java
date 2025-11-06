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
import io.jmix.core.DataManager;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Route(value = "home", layout = MainView.class)
@ViewController(id = "Home")
@ViewDescriptor(path = "home.xml")
public class Home extends StandardView {

    @ViewComponent
    private Image heroImage;
    @ViewComponent
    private Image mediaGif;
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

    @Autowired
    private DataManager dataManager;
    @ViewComponent
    private H3 aboutTitle;
    @ViewComponent
    private Div descrText;

    @Subscribe
    public void onInit(InitEvent event) {
        // Carica configurazione (id 1)
        Optional<Configsitovetrina> cfgOpt = dataManager.load(Configsitovetrina.class).id(1).optional();
        cfgOpt.ifPresent(cfg -> {
            if (cfg.getAboutTesto() != null)
                aboutTitle.setText(cfg.getAboutTitolo());
                aboutText.setText(cfg.getAboutTesto());
                homeTitle.setText(cfg.getNomeSito());
                descrText.setText(cfg.getDescrizioneSito());
        });

        // Imposta immagini e gif
        setStreamResourceIfExists(heroImage, "hero-new");
        setStreamResourceIfExists(mediaGif, "mediaGif-new");
        setStreamResourceIfExists(foto1, "foto1-new");
        setStreamResourceIfExists(foto2, "foto2-new");
        setStreamResourceIfExists(foto3, "foto3-new");
        setStreamResourceIfExists(logo, "logo-new");
        setStreamResourceIfExists(carouselFoto1, "fotocarosello1-new");
        setStreamResourceIfExists(carouselFoto2, "fotocarosello2-new");
        setStreamResourceIfExists(carouselFoto3, "fotocarosello3-new");

        // Margine inferiore per tutta la pagina Home  da capire a datri messi.
       getContent().getStyle().set("margin-bottom", "40vh");
    }


    private void setStreamResourceIfExists(Image img, String baseNameWithSuffix) {
        // Normalizza nome base (rimuove -new / -old)
        String base = baseNameWithSuffix;
        if (base.endsWith("-new") || base.endsWith("-old")) {
            base = base.substring(0, base.lastIndexOf('-'));
        }

        String[] extensions = {".png", ".jpg", ".jpeg", ".gif", ".webp"};
        Path dir = Paths.get(filesPath);
        if (!Files.exists(dir)) return;

        try {
            // 🔍 Priorità: base-new.ext -> base-old.ext -> base.ext
            Path foundPath = null;

            // 1. cerca file -new
            for (String ext : extensions) {
                Path p = dir.resolve(base + "-new" + ext);
                if (Files.exists(p)) {
                    foundPath = p;
                    break;
                }
            }

            // 2. se non trovato, cerca file -old
            if (foundPath == null) {
                for (String ext : extensions) {
                    Path p = dir.resolve(base + "-old" + ext);
                    if (Files.exists(p)) {
                        foundPath = p;
                        break;
                    }
                }
            }

            // 3. se non trovato, cerca file base
            if (foundPath == null) {
                for (String ext : extensions) {
                    Path p = dir.resolve(base + ext);
                    if (Files.exists(p)) {
                        foundPath = p;
                        break;
                    }
                }
            }

            // ✅ Se trovato, crea la risorsa con MIME corretto
            if (foundPath != null) {
                Path finalFoundPath = foundPath;
                StreamResource res = new StreamResource(
                        foundPath.getFileName().toString() + "?v=" + Files.getLastModifiedTime(foundPath).toMillis(),
                        () -> {
                            try {
                                return Files.newInputStream(finalFoundPath);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );

                // 👉 Imposta tipo MIME corretto (necessario per GIF animate!)
                String mimeType = Files.probeContentType(foundPath);
                if (mimeType != null) {
                    res.setContentType(mimeType);
                }

                img.setSrc(res);
                img.setAlt(foundPath.getFileName().toString());
            }

        } catch (IOException ex) {
            Notification.show("Errore caricamento immagine: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER);
        }
    }



    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        getElement().executeJs("""
        const images = Array.from(document.querySelectorAll('.carousel-image'));
        let index = 0;
        function show(i) {
            images.forEach((img, n) => img.classList.toggle('active', n === i));
        }
        show(index);
        document.getElementById('nextBtn').onclick = () => {
            index = (index + 1) % images.length;
            show(index);
        };
        document.getElementById('prevBtn').onclick = () => {
            index = (index - 1 + images.length) % images.length;
            show(index);
        };
        setInterval(() => {
            index = (index + 1) % images.length;
            show(index);
        }, 5000);
    """);
    }

}
