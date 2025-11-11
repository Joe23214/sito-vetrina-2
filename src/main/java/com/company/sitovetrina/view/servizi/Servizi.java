    package com.company.sitovetrina.view.servizi;

    import com.company.sitovetrina.entity.Servizio;
    import com.company.sitovetrina.view.main.MainView;
    import com.vaadin.flow.component.Html;
    import com.vaadin.flow.component.UI;
    import com.vaadin.flow.component.button.Button;
    import com.vaadin.flow.component.dependency.CssImport;
    import com.vaadin.flow.component.html.Anchor;
    import com.vaadin.flow.component.html.Div;
    import com.vaadin.flow.component.html.IFrame;
    import com.vaadin.flow.component.html.Image;
    import com.vaadin.flow.router.Route;
    import com.vaadin.flow.server.StreamResource;
    import com.vaadin.flow.server.StreamResourceRegistry;
    import com.vaadin.flow.server.auth.AnonymousAllowed;
    import io.jmix.core.DataManager;
    import io.jmix.flowui.view.*;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;

    import java.io.IOException;
    import java.nio.file.*;
    import java.util.List;
    import java.util.stream.Stream;

    @Route(value = "servizi", layout = MainView.class)
    @ViewController("Servizi")
    @ViewDescriptor(path = "servizi.xml")
    @AnonymousAllowed
    public class Servizi extends StandardView {

        @Autowired
        private DataManager dataManager;

        // Percorso base dove salvi allegati e immagini
        @Value("${site.files.path}")
        private String filesPath;

        @ViewComponent
        private Div servicesContainer;

        @Subscribe
        public void onBeforeShow(BeforeShowEvent event) {
            List<Servizio> servizi = dataManager.load(Servizio.class).all().list();

            servicesContainer.removeAll();

            if (servizi.isEmpty()) {
                Div empty = new Div();
                empty.addClassName("no-services");
                empty.add(new Html("<div><h2>Nessun servizio disponibile</h2><p>Torna presto per aggiornamenti.</p></div>"));
                servicesContainer.add(empty);
                return;
            }

            if (servizi.size() == 1) {
                Servizio s = servizi.get(0);
                Div singleWrapper = createFullpageServiceCard(s);
                servicesContainer.add(singleWrapper);
                UI.getCurrent().getPage().executeJs("window.scrollTo(0,0);");
            } else {
                Div grid = new Div();
                grid.addClassName("services-grid");
                for (Servizio s : servizi) {
                    grid.add(createSlimServiceCard(s));
                }
                servicesContainer.add(grid);
            }
        }

        // ----- FULLPAGE CARD -----
        private Div createFullpageServiceCard(Servizio s) {
            Div wrapper = new Div();
            wrapper.addClassName("service-fullpage-wrapper");

            Div card = new Div();
            card.addClassName("service-fullpage-card");

            Div header = new Div();
            header.addClassName("service-header");
            header.add(new Html("<h1 class='service-title'>" + safe(s.getTitolo()) + "</h1>"));
            if (s.getSottoTitolo() != null)
                header.add(new Html("<h3 class='service-subtitle'>" + safe(s.getSottoTitolo()) + "</h3>"));
            card.add(header);

            Div desc = new Div();
            desc.addClassName("service-description");
            if (s.getDescrizione() != null)
                desc.add(new Html("<div>" + toHtmlParagraphs(s.getDescrizione()) + "</div>"));
            card.add(desc);

            Div attachArea = createInlineAttachmentViewer(s.getId(),true);
            card.add(attachArea);


            Div cta = new Div();
            cta.addClassName("service-cta");
            if (s.getLinkInfo() != null && !s.getLinkInfo().isBlank()) {
                Button infoBtn = new Button("Scopri di più");
                infoBtn.addClassName("jmix-button");
                infoBtn.getElement().setAttribute("onclick", "window.open('" + s.getLinkInfo() + "','_blank')");
                cta.add(infoBtn);
            }
            card.add(cta);

            wrapper.add(card);
            return wrapper;
        }
        private Div createInlineAttachmentViewer(Integer servizioId, boolean fullpage) {
            Div container = new Div();
            container.addClassName("inline-attachments");
            if (fullpage) {
                container.addClassName("fullpage");
            } else {
                container.addClassName("thumb");
            }

            Path dir = Paths.get(filesPath);
            if (!Files.exists(dir) || !Files.isDirectory(dir)) return container;

            String prefix = "servizio-" + servizioId + "-";

            try (Stream<Path> stream = Files.list(dir)) {
                stream.filter(p -> {
                    String fname = p.getFileName().toString().toLowerCase();
                    return fname.startsWith(prefix) &&
                            (fname.endsWith(".png") || fname.endsWith(".jpg") || fname.endsWith(".jpeg")
                                    || fname.endsWith(".gif") || fname.endsWith(".webp") || fname.endsWith(".pdf"));
                }).sorted().forEach(p -> {
                    String fname = p.getFileName().toString();

                    StreamResource res = new StreamResource(fname, () -> {
                        try {
                            return Files.newInputStream(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    UI.getCurrent().getSession().getResourceRegistry().registerResource(res);
                    String resUrl = StreamResourceRegistry.getURI(res).toString();

                    if (fname.toLowerCase().endsWith(".pdf")) {
                        container.add(new Html(
                                "<iframe src='" + resUrl + "#toolbar=0&navpanes=0&scrollbar=1' " +
                                        "class='" + (fullpage ? "inline-pdf fullpage" : "inline-pdf thumb") + "'></iframe>"
                        ));
                    } else {
                        Image img = new Image(res, fname);
                        img.addClassName(fullpage ? "attachment-img-full" : "attachment-img-thumb");
                        img.setWidth("100%");
                        img.setHeight("auto");
                        container.add(img);
                    }
                });
            } catch (IOException e) {
                System.err.println("Errore lettura allegati per servizio " + servizioId + ": " + e.getMessage());
            }

            return container;
        }




        private Div createSlimServiceCard(Servizio s) {
            Div card = new Div();
            card.addClassName("service-card");

            Div content = new Div();
            content.addClassName("service-card-content");

            // Titolo / sottotitolo / descrizione
            content.add(new Html("<h2 class='service-card-title'>" + safe(s.getTitolo()) + "</h2>"));
            if (s.getSottoTitolo() != null)
                content.add(new Html("<p class='service-card-subtitle'>" + safe(s.getSottoTitolo()) + "</p>"));
            if (s.getDescrizione() != null)
                content.add(new Html("<div class='service-card-desc'>" + toHtmlShort(s.getDescrizione()) + "</div>"));

            // 🔹 Sezione allegati
            Div attachments = new Div();
            attachments.addClassName("service-card-attachments");

            populateAttachmentsIntoDiv(s.getId(), attachments, false);

            // 🔹 Wrappiamo eventuali PDF/iframe in un div responsivo
            attachments.getChildren().forEach(child -> {
                if (child instanceof IFrame) {
                    Div wrapper = new Div();
                    wrapper.addClassName("pdf-wrapper");

                    // Altezza responsiva: desktop 60vh, mobile 30vh
                    wrapper.getStyle().set("width", "100%");
                    wrapper.getStyle().set("height", "60vh");       // default desktop
                    wrapper.getStyle().set("max-height", "60vh");
                    wrapper.getStyle().set("overflow", "hidden");

                    // Contenuto iframe
                    IFrame iframe = (IFrame) child;
                    iframe.getElement().setAttribute("style", "width:100%; height:100%; border:none; object-fit:contain; cursor:pointer;");

                    wrapper.add(iframe);
                    attachments.replace(iframe, wrapper);
                }
            });

            content.add(attachments);
            card.add(content);

            return card;
        }


        private void openFullscreenViewer(Integer servizioId) {
            Div overlay = new Div();
            overlay.addClassName("fullscreen-overlay");

            // Contenuto (PDF o immagini)
            Div content = createInlineAttachmentViewer(servizioId, true);
            content.addClassName("fullscreen-content");

            // Bottone chiudi
            Button closeBtn = new Button("Chiudi", e -> {
                UI.getCurrent().remove(overlay);
            });
            closeBtn.addClassName("close-fullscreen-btn");

            overlay.add(closeBtn, content);

            UI.getCurrent().add(overlay);
        }


        private void populateAttachmentsIntoDiv(Integer servizioId, Div container, boolean fullpage) {
            Path dir = Paths.get(filesPath);
            if (!Files.exists(dir) || !Files.isDirectory(dir)) return;

            String prefix = "servizio-" + servizioId + "-";
            try (Stream<Path> stream = Files.list(dir)) {
                stream.filter(p -> {
                    String fname = p.getFileName().toString().toLowerCase();
                    return fname.startsWith(prefix) &&
                            (fname.endsWith(".png") || fname.endsWith(".jpg") || fname.endsWith(".jpeg")
                                    || fname.endsWith(".gif") || fname.endsWith(".webp") || fname.endsWith(".pdf"));
                }).sorted().forEach(p -> {
                    String fname = p.getFileName().toString();

                    StreamResource res = new StreamResource(fname, () -> {
                        try {
                            return Files.newInputStream(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    UI.getCurrent().getSession().getResourceRegistry().registerResource(res);
                    String resUrl = StreamResourceRegistry.getURI(res).toString();

                    if (fname.toLowerCase().endsWith(".pdf")) {
                        Html pdfIframe = new Html(
                                "<iframe src='" + resUrl + "#toolbar=0&navpanes=0&scrollbar=1' class='inline-pdf'></iframe>"
                        );

// Creo un Div wrapper cliccabile
                        Div pdfWrapper = new Div();
                        pdfWrapper.addClassName("pdf-wrapper");
                        pdfWrapper.getElement().appendChild(pdfIframe.getElement());
                        pdfWrapper.getStyle().set("cursor", "pointer"); // per il cursore

// Click listener sul Div wrapper
                        pdfWrapper.addClickListener(e -> openFullscreenViewer(servizioId));

// Aggiungo al container
                        container.add(pdfWrapper);


                    } else {
                        Image img = new Image(res, fname);
                        img.addClassName(fullpage ? "attachment-img-full" : "attachment-img-thumb");
                        img.setWidth("100%");
                        img.setHeight("auto");

                        // 🔹 Click listener per aprire fullscreen
                        img.addClickListener(e -> openFullscreenViewer(servizioId));

                        container.add(img);
                    }
                });
            } catch (IOException e) {
                System.err.println("Errore lettura allegati per servizio " + servizioId + ": " + e.getMessage());
            }
        }



        // ----- URL CORRETTO per allegati -----
        private String createStreamResourcePath(Path p) {
            String fname = p.getFileName().toString();
            StreamResource res = new StreamResource(fname, () -> {
                try {
                    return Files.newInputStream(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            // ✅ Usa il registry corretto (Vaadin 24+)
            return StreamResourceRegistry.getURI(res).toString();
        }

        // ----- HELPER METODI -----
        private String safe(String v) {
            return v == null ? "" : escapeHtml(v);
        }

        private String escapeHtml(String input) {
            if (input == null) return "";
            return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }

        private String toHtmlParagraphs(String txt) {
            if (txt == null) return "";
            StringBuilder sb = new StringBuilder();
            for (String line : txt.split("\\r?\\n")) {
                sb.append("<p>").append(escapeHtml(line)).append("</p>");
            }
            return sb.toString();
        }

        private String toHtmlShort(String txt) {
            if (txt == null) return "";
            String trimmed = txt.length() > 400 ? txt.substring(0, 400) + "..." : txt;
            return toHtmlParagraphs(trimmed);
        }
        private long countAttachmentsFor(Integer servizioId) {
            Path dir = Paths.get(filesPath);
            if (!Files.exists(dir) || !Files.isDirectory(dir)) return 0;

            String prefix = "servizio-" + servizioId + "-";
            try (Stream<Path> stream = Files.list(dir)) {
                return stream.filter(p -> {
                    String fname = p.getFileName().toString().toLowerCase();
                    return fname.startsWith(prefix) &&
                            (fname.endsWith(".png") || fname.endsWith(".jpg") || fname.endsWith(".jpeg")
                                    || fname.endsWith(".gif") || fname.endsWith(".webp") || fname.endsWith(".pdf"));
                }).count();
            } catch (IOException e) {
                return 0;
            }
        }

    }
