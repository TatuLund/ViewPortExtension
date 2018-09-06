package org.vaadin.viewportextension.client;

import org.vaadin.viewportextension.ViewPortExtension;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(ViewPortExtension.class)
public class ViewPortExtensionConnector extends AbstractExtensionConnector {

	boolean isInViewPort;

	private boolean determineIsInViewPort(final Widget widget) {
		int x = widget.getAbsoluteLeft();
    	  int y = widget.getAbsoluteTop();
    	  int wx = widget.getOffsetWidth()+x;
    	  int wy = widget.getOffsetHeight()+y;
    	  int scrollTop = Window.getScrollTop();
    	  int scrollLeft = Window.getScrollLeft();
    	  int windowBottom = Window.getClientHeight()+scrollTop;
    	  int windowRight = Window.getClientWidth()+scrollLeft;
    	  
    	  boolean newIsInViewPort = (wy > scrollTop) && (y < windowBottom) && (wx > scrollLeft) && (x < windowRight);
    	  
    	  return newIsInViewPort;
	}

	
    // ServerRpc is used to send events to server. Communication implementation
    // is automatically created here
	ViewPortExtensionServerRpc rpc = RpcProxy.create(ViewPortExtensionServerRpc.class, this);

    // We must implement getState() to cast to correct type
    @Override
    public ViewPortExtensionState getState() {
        return (ViewPortExtensionState) super.getState();
    }

	@Override
	protected void extend(ServerConnector target) {
       	final Widget widget = ((ComponentConnector) target).getWidget();
		AnimationCallback isInViewPortCallback = new AnimationCallback() {
			@Override
			public void execute(double timestamp) {
				isInViewPort = determineIsInViewPort(widget);
				rpc.onViewPortStatusChanged(isInViewPort);
			}
        };

	    // Determine initial value when component is attached
		widget.addAttachHandler(event -> {
			if (event.isAttached()) {
				AnimationScheduler.get().requestAnimationFrame(isInViewPortCallback);
			} else {
				// If component is detached it is no longer in viewport
				isInViewPort = false;
				rpc.onViewPortStatusChanged(isInViewPort);
			}
		});		
		
		// Browser window resizing may lead to change of viewport visibility
		Window.addResizeHandler(event -> {
			boolean newIsInViewPort = determineIsInViewPort(widget);
  		  	if (newIsInViewPort != isInViewPort) {
  		  		isInViewPort = newIsInViewPort;
  		  		rpc.onViewPortStatusChanged(isInViewPort);
  		  	}			
		});

		// Listen to events that are related to scrolling either with keyboard or mouse
		Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(Event.NativePreviewEvent event) {	    	  

				if ((event.getTypeInt() == Event.ONMOUSEWHEEL) || (event.getTypeInt() == Event.ONSCROLL) || (event.getTypeInt() == Event.ONKEYUP) || (event.getTypeInt() == Event.ONMOUSEUP)) {

					boolean newIsInViewPort = determineIsInViewPort(widget);
	    	  
					if (newIsInViewPort != isInViewPort) {
						isInViewPort = newIsInViewPort;
						rpc.onViewPortStatusChanged(isInViewPort);
					}
				}
			}

	    });		
	}
}
