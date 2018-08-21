package org.vaadin.viewportextension.demo;

import org.vaadin.viewportextension.ViewPortExtension;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("ViewPortExtension Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }
    
    public class MyComponent extends Label {

    	ViewPortExtension extension;
    	
    	public MyComponent(String text) {
    		super(text);
    		this.setHeight("100px");
    		this.setWidth("100%");
            extension = new ViewPortExtension(this);
            extension.addViewPortStatusChangedListener(event -> {
            	if (event.isInViewPort()) {
            		System.out.println(this.getValue() +" is in the view port");
            	} else {
            		System.out.println(this.getValue() +" is not in the view port");
            	}
            });    		
    	}
    	
    	public boolean isInViewPort() {
    		return extension.isInViewPort();
    	}
    }

    @Override
    protected void init(VaadinRequest request) {

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setWidth("100%");
        layout.setMargin(false);
        layout.setSpacing(false);
        for (int i=0;i<200;i++) {
        	MyComponent component = new MyComponent("Label "+i);
        	layout.addComponent(component);
        }
        setContent(layout);
    }
}
