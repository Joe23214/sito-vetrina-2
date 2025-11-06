package com.company.sitovetrina.view.uservpass;

import com.company.sitovetrina.entity.Uservpass;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "uservpasses", layout = MainView.class)
@ViewController(id = "Uservpass.list")
@ViewDescriptor(path = "uservpass-list-view.xml")
@LookupComponent("uservpassesDataGrid")
@DialogMode(width = "64em")
public class UservpassListView extends StandardListView<Uservpass> {
}