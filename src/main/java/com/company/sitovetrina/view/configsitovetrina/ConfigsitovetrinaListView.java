package com.company.sitovetrina.view.configsitovetrina;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "configsitovetrinas", layout = MainView.class)
@ViewController(id = "Configsitovetrina.list")
@ViewDescriptor(path = "configsitovetrina-list-view.xml")
@LookupComponent("configsitovetrinasDataGrid")
@DialogMode(width = "64em")
public class ConfigsitovetrinaListView extends StandardListView<Configsitovetrina> {
}