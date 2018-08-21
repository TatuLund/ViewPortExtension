package org.vaadin.viewportextension.client;

import com.vaadin.shared.communication.ServerRpc;

public interface ViewPortExtensionServerRpc extends ServerRpc {
	public void onViewPortStatusChanged(boolean isInViewPort);

}
