package com.company.sitovetrina.view.configsitovetrina;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "configsitovetrina", layout = MainView.class)
@ViewController("Configsitovetrina")
@ViewDescriptor("configsitovetrina-detail-view.xml")
@EditedEntityContainer("configsitovetrinaDc")
public class ConfigsitovetrinaDetailView extends StandardDetailView<Configsitovetrina> {
    @ViewComponent
    private DataContext dataContext;
    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private InstanceContainer<Configsitovetrina> configsitovetrinaDc;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Configsitovetrina entity = dataManager.load(Configsitovetrina.class)
                .id(1)
                .optional()
                .orElseGet(() -> {
                    Configsitovetrina newConfig = new Configsitovetrina();
                    newConfig.setId(1);
                    return newConfig; // NON salvare subito
                });

        configsitovetrinaDc.setItem(dataContext.merge(entity)); // ora è gestita
    }
}