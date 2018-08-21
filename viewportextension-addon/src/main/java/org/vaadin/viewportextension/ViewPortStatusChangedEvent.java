package org.vaadin.viewportextension;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CustomComponent;

/**
 * The ViewPortStatusChangedEvent event when component viewport status changes
 * 
 * @author Tatu Lund
 */
@SuppressWarnings("serial")
public class ViewPortStatusChangedEvent extends CustomComponent.Event {

	private boolean isInViewPort;
	
	public ViewPortStatusChangedEvent(AbstractComponent source, boolean isInViewPort) {
		super(source);
		this.isInViewPort = isInViewPort;
	}

	/**
	 * Return the viewport status of the component, true if in viewport
	 * 
	 * @return The status
	 */
	public boolean isInViewPort() {
		return isInViewPort;
	}
}
