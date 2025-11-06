package com.company.sitovetrina.view.home;


import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "home", layout = MainView.class)
@ViewController(id = "Home")
@ViewDescriptor(path = "home.xml")
public class Home extends StandardView {
}