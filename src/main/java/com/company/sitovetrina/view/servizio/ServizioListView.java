package com.company.sitovetrina.view.servizio;

import com.company.sitovetrina.entity.Servizio;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "servizios", layout = MainView.class)
@ViewController(id = "Servizio.list")
@ViewDescriptor(path = "servizio-list-view.xml")
@LookupComponent("serviziosDataGrid")
@DialogMode(width = "64em")
public class ServizioListView extends StandardListView<Servizio> {
}