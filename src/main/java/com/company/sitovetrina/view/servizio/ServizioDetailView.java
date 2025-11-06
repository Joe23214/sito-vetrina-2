package com.company.sitovetrina.view.servizio;

import com.company.sitovetrina.entity.Servizio;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "servizios/:id", layout = MainView.class)
@ViewController(id = "Servizio.detail")
@ViewDescriptor(path = "servizio-detail-view.xml")
@EditedEntityContainer("servizioDc")
public class ServizioDetailView extends StandardDetailView<Servizio> {
}