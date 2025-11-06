package com.company.sitovetrina.view.messaggio;

import com.company.sitovetrina.entity.Messaggio;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "messaggios", layout = MainView.class)
@ViewController(id = "Messaggio.list")
@ViewDescriptor(path = "messaggio-list-view.xml")
@LookupComponent("messaggiosDataGrid")
@DialogMode(width = "64em")
public class MessaggioListView extends StandardListView<Messaggio> {
}