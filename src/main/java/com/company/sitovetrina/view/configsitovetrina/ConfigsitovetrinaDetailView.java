package com.company.sitovetrina.view.configsitovetrina;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.entity.Servizio;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceRegistry;
import io.jmix.core.DataManager;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.vaadin.flow.dom.Element;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route(value = "configsitovetrina",layout = MainView.class)
@ViewController("Configsitovetrina")
@ViewDescriptor("configsitovetrina-detail-view.xml")
@EditedEntityContainer("configsitovetrinaDc")
public class ConfigsitovetrinaDetailView extends StandardDetailView<Configsitovetrina> {

    @Autowired
    private DataManager dataManager;
    @ViewComponent
    private DataContext dataContext;
    @ViewComponent
    private InstanceContainer<Configsitovetrina> configsitovetrinaDc;
    @ViewComponent private Upload uploadBanner;
    @ViewComponent private Div previewBanner;
 /*   @ViewComponent
    private Upload uploadlogin;

    @ViewComponent
    private Div previewLogin;*/

    @ViewComponent private Upload uploadFoto1;
    @ViewComponent private Upload uploadFoto2;
    @ViewComponent private Upload uploadFoto3;
    @ViewComponent private Upload uploadFoto4;
    @ViewComponent private Upload uploadGif;
    @ViewComponent private Upload uploadLogo;

    @ViewComponent private Div previewFoto1;
    @ViewComponent private Div previewFoto2;
    @ViewComponent private Div previewFoto3;
    @ViewComponent private Div previewFoto4;
    @ViewComponent private Div previewGif;
    @ViewComponent private Div previewLogo;
    @ViewComponent private Upload uploadCarosello1;
    @ViewComponent private Upload uploadCarosello2;
    @ViewComponent private Upload uploadCarosello3;

    @ViewComponent private Div previewCarosello1;
    @ViewComponent private Div previewCarosello2;
    @ViewComponent private Div previewCarosello3;
    @ViewComponent
    private VerticalLayout serviziUploaderBox;

    @Value("${site.files.path}")
    private String filesPath;

    private Map<String, Div> previewMap;
    private Map<String, Upload> uploadMap;



    @Subscribe
    public void onInit(InitEvent event) {
        // --- upload login background ---
        previewMap = Map.of(
                "foto1", previewFoto1,
                "foto2", previewFoto2,
                "foto3", previewFoto3,
                "foto4", previewFoto4,
                "mediaGif", previewGif,
                "logo", previewLogo,
                "fotocarosello1", previewCarosello1,
                "fotocarosello2", previewCarosello2,
                "fotocarosello3", previewCarosello3,
                "hero", previewBanner   // 👈 aggiunta
        );
      /*  previewMap.put("loginBg", previewLogin);*/

        uploadMap = Map.of(
                "foto1", uploadFoto1,
                "foto2", uploadFoto2,
                "foto3", uploadFoto3,
                "foto4", uploadFoto4,
                "mediaGif", uploadGif,
                "logo", uploadLogo,
                "fotocarosello1", uploadCarosello1,
                "fotocarosello2", uploadCarosello2,
                "fotocarosello3", uploadCarosello3,
                "hero", uploadBanner    // 👈 aggiunta
        );
      /*  uploadMap.put("loginBg", uploadlogin);*/

        uploadMap.forEach(this::setupUpload);
        List<Servizio> servizi = dataManager.load(Servizio.class).all().list();
        if (servizi.isEmpty()) {
            serviziUploaderBox.add(new Div() {{
                setText("Nessun servizio trovato nel database.");
            }});
        } else {
            for (Servizio servizio : servizi) {
                serviziUploaderBox.add(creaUploaderServizio(servizio));
            }
        }

        previewMap.keySet().forEach(this::renderPreviewsForBase);
    }



    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Configsitovetrina entity = dataManager.load(Configsitovetrina.class)
                .id(1)
                .optional()
                .orElseGet(() -> {
                    Configsitovetrina newConfig = new Configsitovetrina();
                    newConfig.setId(1);
                    return newConfig;
                });
        configsitovetrinaDc.setItem(dataContext.merge(entity));
    }
    private Component creaUploaderServizio(Servizio servizio) {
        Div container = new Div();

        // Titolo servizio
        Div title = new Div();
        title.setText("📄 Allegati per: " + servizio.getTitolo());
        title.setClassName("h6");
        container.add(title);

        // Upload
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFileSize(10485760); // 10MB
        upload.setDropAllowed(true);
        upload.setAutoUpload(true);
        upload.setClassName("custom-upload");
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "application/pdf");

        upload.addSucceededListener(e -> {
            try (InputStream in = buffer.getInputStream()) {
                salvaAllegatoServizio(servizio, e.getFileName(), in);
                Notification.show("File caricato per " + servizio.getTitolo(), 5000, Notification.Position.TOP_CENTER);
            } catch (IOException ex) {
                Notification.show("Errore nel caricamento: " + ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
            }
        });

        container.add(upload);
        return container;
    }
    private void salvaAllegatoServizio(Servizio servizio, String fileName, InputStream in) throws IOException {
        // Cartella base
        Path baseDir = Paths.get(filesPath);
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }

        // Prefisso per i file di questo servizio
        String prefix = "servizio-" + servizio.getId() + "-";

        // 🔹 Elimina eventuali file esistenti per questo servizio
        try (Stream<Path> existingFiles = Files.list(baseDir)) {
            existingFiles
                    .filter(p -> p.getFileName().toString().startsWith(prefix))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            System.err.println("Errore eliminazione vecchio file: " + e.getMessage());
                        }
                    });
        }

        // 🔹 Salva il nuovo file
        Path target = baseDir.resolve(prefix + fileName);
        try (OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            in.transferTo(out);
        }
    }



    private void setupUpload(String baseName, Upload upload) {
        MemoryBuffer buffer = new MemoryBuffer();
        upload.setReceiver(buffer);
        upload.addSucceededListener(e -> {
            String fileName = e.getFileName();
            try (InputStream in = buffer.getInputStream()) {
                String ext = getExtension(fileName);
                if (ext == null) ext = ".bin";
                saveWithVersioning(baseName, ext, in);
                renderPreviewsForBase(baseName);
                Notification.show("File caricato: " + fileName, 3000, Notification.Position.TOP_CENTER);
            } catch (Exception ex) {
                Notification.show("Errore nel salvataggio: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER);
            }
        });
    }

    private String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return (i >= 0) ? fileName.substring(i).toLowerCase() : null;
    }

    private synchronized void saveWithVersioning(String baseName, String ext, InputStream in) throws IOException {
        Path dir = Paths.get(filesPath);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        // rinomina eventuali -new -> -old
        Files.list(dir)
                .filter(p -> p.getFileName().toString().startsWith(baseName + "-new"))
                .forEach(p -> {
                    try {
                        Files.move(p, p.resolveSibling(p.getFileName().toString().replace("-new", "-old")),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ignored) {}
                });

        Path target = dir.resolve(baseName + "-new" + ext);
        try (OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            in.transferTo(out);
        }
    }
    private void renderPreviewsForBase(String baseName) {
        Div container = previewMap.get(baseName);
        container.removeAll();

        Path dir = Paths.get(filesPath);
        if (!Files.exists(dir)) return;

        try {
            // Trova eventuali file -old
            List<Path> oldFiles = Files.list(dir)
                    .filter(p -> p.getFileName().toString().startsWith(baseName + "-old"))
                    .collect(Collectors.toList());

            if (!oldFiles.isEmpty()) {
                for (Path f : oldFiles) {
                    container.add(makePreviewBox("Versione attuale (-old):", f, true, baseName));
                }
            } else {
                // Mostra il messaggio solo se davvero non ci sono file -old
                Div msg = new Div();
                msg.setText("Nessuna immagine -old trovata");
                container.add(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Component makePreviewBox(String title, Path file, boolean showRestore, String baseName) {
        Div box = new Div();
        box.addClassName("preview-box");

        // Titolo sopra
        Div titleDiv = new Div();
        titleDiv.setText(title);
        titleDiv.getStyle()
                .set("font-weight", "600")
                .set("margin-bottom", "8px");
        box.add(titleDiv);

        // Riga con immagine e pulsante
        Div row = new Div();
        row.addClassName("preview-row");

        // Contenitore anteprima (immagine o video)
        Component preview = createPreviewComponent(file.toFile());
        preview.addClassName("preview-container");
        row.add(preview);

        // Pulsante esterno (solo se serve)
        if (showRestore) {
            Button restore = new Button("Ripristina", e -> {
                try {
                    restoreOldToNew(baseName, file.getFileName().toString());
                    renderPreviewsForBase(baseName);
                    Notification.show("Immagine ripristinata con successo", 2500, Notification.Position.TOP_CENTER);
                } catch (IOException ex) {
                    Notification.show("Errore nel ripristino: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER);
                }
            });
            restore.addClassName("restore-button");
            row.add(restore);
        }

        box.add(row);
        return box;
    }

    private Component createPreviewComponent(File file) {
        String name = file.getName().toLowerCase();
        StreamResource res = new StreamResource(file.getName(), () -> safeInputStream(file));

        // contenitore senza bordo
        Div container = new Div();
        container.addClassName("preview-container");


        if (name.endsWith(".mp4")) {
            Element videoEl = new Element("video");
            videoEl.setAttribute("src", StreamResourceRegistry.getURI(res).toString());
            videoEl.setAttribute("controls", true);
            videoEl.setAttribute("width", "260");
            videoEl.setAttribute("height", "180");
            container.getElement().appendChild(videoEl);
        } else {
            Image img = new Image(res, "preview");
            img.setWidth("auto");
            img.setHeight("150px");
            img.getStyle()
                    .set("object-fit", "contain")
                    .set("border", "none")
                    .set("box-shadow", "none")
                    .set("border-radius", "0");
            container.add(img);
        }

        return container;
    }


    private InputStream safeInputStream(File f) {
        try {
            return new FileInputStream(f);
        } catch (Exception e) {
            return InputStream.nullInputStream();
        }
    }

    private void restoreOldToNew(String baseName, String oldFileName) throws IOException {
        Path dir = Paths.get(filesPath);
        Path oldFile = dir.resolve(oldFileName);

        // 🔹 Trova eventuale file -new
        Optional<Path> currentNew = Files.list(dir)
                .filter(p -> p.getFileName().toString().startsWith(baseName + "-new"))
                .findFirst();

        // 🔹 Se esiste un -new, rinominalo in -tmp
        Path tmpNew = null;
        if (currentNew.isPresent()) {
            Path newFile = currentNew.get();
            tmpNew = dir.resolve(baseName + "-tmp" + getExtension(newFile.getFileName().toString()));
            Files.move(newFile, tmpNew, StandardCopyOption.REPLACE_EXISTING);
        }

        // 🔹 Rinomina l'-old selezionato a -new
        String newName = oldFileName.replace("-old", "-new");
        Files.move(oldFile, dir.resolve(newName), StandardCopyOption.REPLACE_EXISTING);

        // 🔹 Se c’era un vecchio -new, diventa ora -old
        if (tmpNew != null) {
            String newOldName = tmpNew.getFileName().toString().replace("-tmp", "-old");
            Files.move(tmpNew, dir.resolve(newOldName), StandardCopyOption.REPLACE_EXISTING);
        }

        // 🔹 Dopo lo scambio, ricarica la nuova preview
        renderPreviewsForBase(baseName);
    }


}
