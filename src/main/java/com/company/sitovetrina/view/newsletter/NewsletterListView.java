package com.company.sitovetrina.view.newsletter;

import com.company.sitovetrina.entity.Newsletter;
import com.company.sitovetrina.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "newsletters", layout = MainView.class)
@ViewController(id = "Newsletter.list")
@ViewDescriptor(path = "newsletter-list-view.xml")
@LookupComponent("newslettersDataGrid")
@DialogMode(width = "64em")
public class NewsletterListView extends StandardListView<Newsletter> {
}