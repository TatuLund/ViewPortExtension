package org.vaadin.viewportextension;

import java.lang.reflect.Method;

import org.vaadin.viewportextension.client.ViewPortExtensionServerRpc;
import org.vaadin.viewportextension.client.ViewPortExtensionState;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.util.ReflectTools;

@SuppressWarnings("serial")
public class ViewPortExtension extends AbstractExtension {

	private boolean isInViewPort;
	
	public interface ViewPortStatusChangedListener extends ConnectorEventListener {
		Method STATUSCHANGED_METHOD = ReflectTools.findMethod(
				ViewPortStatusChangedListener.class, "onStatusChanged", ViewPortStatusChangedEvent.class);
		public void onStatusChanged(ViewPortStatusChangedEvent event);
	}

	private AbstractComponent component;

	/**
	 * The constructor extends selected component with isInViewPort capability
	 * 
	 * @param component Component to be extended
	 */
	public ViewPortExtension(AbstractComponent component) {
		this.extend(component);
		this.component = component;

        // To receive events from the client, we register ServerRpc
		registerRpc(new ViewPortExtensionServerRpc() {

			@Override
			public void onViewPortStatusChanged(boolean newIsInViewPort) {
				isInViewPort = newIsInViewPort;
				fireEvent(new ViewPortStatusChangedEvent(component,isInViewPort));				
			}
		});
    }

	
	/**
	 * Return the viewport status of the component, true if in viewport
	 * 
	 * @return The status
	 */
	public boolean isInViewPort() {
		return isInViewPort;
	}
	
	/**
	 * Add a new ViewPortStatusChangedListener
	 * The ViewPortStatusChangedEvent event is fired when Component view port status changes
	 *  
	 * @param listener A ViewPortStatusChangedListener to be added
	 */
	public Registration addViewPortStatusChangedListener(ViewPortStatusChangedListener listener) {
		return addListener(ViewPortStatusChangedEvent.class, listener, ViewPortStatusChangedListener.STATUSCHANGED_METHOD);
	}
	
    // We must override getState() to cast the state to ViewPortExtensionState
    @Override
    protected ViewPortExtensionState getState() {
        return (ViewPortExtensionState) super.getState();
    }
	
}
