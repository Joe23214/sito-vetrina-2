package com.company.sitovetrina.view.userv;

import com.company.sitovetrina.entity.Userv;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "uservs/:id", layout = MainView.class)
@ViewController(id = "Userv.detail")
@ViewDescriptor(path = "userv-detail-view.xml")
@EditedEntityContainer("uservDc")
public class UservDetailView extends StandardDetailView<Userv> {
}