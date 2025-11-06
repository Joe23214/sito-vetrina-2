package com.company.sitovetrina.view.messaggio;

import com.company.sitovetrina.entity.Messaggio;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "messaggios/:id", layout = MainView.class)
@ViewController(id = "Messaggio.detail")
@ViewDescriptor(path = "messaggio-detail-view.xml")
@EditedEntityContainer("messaggioDc")
public class MessaggioDetailView extends StandardDetailView<Messaggio> {
}