package org.vaadin.viewportextension.demo;

import org.vaadin.viewportextension.ViewPortExtension;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Push
@Theme("demo")
@Title("ViewPortExtension Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {
	
    VerticalLayout log = new VerticalLayout();

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }
    
    public class MyComponent extends Label {

    	ViewPortExtension extension;
        Thread t;
    	int counter = 0;
    	
    	public MyComponent(String text) {
    		super(text);
    		this.setHeight("100px");
    		this.setWidth("100%");
    		this.addStyleName("border");
    		this.addStyleName(ValoTheme.LABEL_HUGE);
            extension = new ViewPortExtension(this);
            extension.addViewPortStatusChangedListener(event -> {
            	if (event.isInViewPort()) {
            		log.addComponentAsFirst(new Label(this.getValue() +" is in the view port"));
            		if (log.getComponentCount() == 500) {
            			Component comp = log.getComponent(499);
            			log.removeComponent(comp);
            		}
            		t = new Thread(() -> {
            			while (true) {
            				try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								break;
							}
            				counter++;
            				final int count = counter;
            				if (getUI().isAttached()) {
            					getUI().access(() -> {
            						setValue(text + " - " + count + "s displayed");
            					});
            				}
            			}
            		});
            		t.start();
            	} else {
            		log.addComponentAsFirst(new Label(this.getValue() +" is not in the view port"));
            		if (log.getComponentCount() == 500) {
            			Component comp = log.getComponent(499);
            			log.removeComponent(comp);
            		}
            		if (t != null) {
            			t.interrupt();
            			t = null;
            		}
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
    	HorizontalLayout main = new HorizontalLayout();
    	main.setSizeFull();
        main.setStyleName("demoContentLayout");
        int height = Page.getCurrent().getBrowserWindowHeight();
    	Panel panel = new Panel("Content: These labels sense view port");
    	panel.setHeight(height+"px");
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setMargin(false);
        layout.setSpacing(false);
        for (int i=0;i<200;i++) {
        	MyComponent component = new MyComponent("Label "+i);
        	layout.addComponent(component);
        }
        panel.setContent(layout);
    	Panel logPanel = new Panel("Log: Scroll content on the rigth");
    	logPanel.setHeight(height+"px");
    	logPanel.setContent(log);
        main.addComponents(logPanel,panel);
        setContent(main);
    }
}
