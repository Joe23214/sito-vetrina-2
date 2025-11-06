package com.company.sitovetrina.view.uservpass;

import com.company.sitovetrina.entity.Uservpass;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "uservpasses/:id", layout = MainView.class)
@ViewController(id = "Uservpass.detail")
@ViewDescriptor(path = "uservpass-detail-view.xml")
@EditedEntityContainer("uservpassDc")
public class UservpassDetailView extends StandardDetailView<Uservpass> {
}