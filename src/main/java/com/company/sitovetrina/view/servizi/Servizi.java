package com.company.sitovetrina.view.servizi;


import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "servizi", layout = MainView.class)
@ViewController(id = "Servizi")
@ViewDescriptor(path = "servizi.xml")
public class Servizi extends StandardView {
}