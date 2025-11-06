package com.company.sitovetrina.view.userv;

import com.company.sitovetrina.entity.Userv;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "uservs", layout = MainView.class)
@ViewController(id = "Userv.list")
@ViewDescriptor(path = "userv-list-view.xml")
@LookupComponent("uservsDataGrid")
@DialogMode(width = "64em")
public class UservListView extends StandardListView<Userv> {
}