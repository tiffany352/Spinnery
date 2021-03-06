package spinnery.widget.api.listener;

import spinnery.widget.WAbstractWidget;

/**
 * An interface for events called when the mouse's scroll wheel is scrolled.
 */
public interface WMouseScrollListener<W extends WAbstractWidget> {
	void event(W widget, float mouseX, float mouseY, double deltaY);
}
