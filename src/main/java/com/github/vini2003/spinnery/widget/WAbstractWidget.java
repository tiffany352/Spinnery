package com.github.vini2003.spinnery.widget;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.ResourceLocation;
import com.github.vini2003.spinnery.registry.ThemeRegistry;
import com.github.vini2003.spinnery.registry.WidgetRegistry;
import com.github.vini2003.spinnery.util.EventUtilities;
import com.github.vini2003.spinnery.widget.api.*;
import com.github.vini2003.spinnery.widget.api.listener.*;

import static com.github.vini2003.spinnery.registry.ThemeRegistry.DEFAULT_THEME;

/**
 * A WAbstractWidget provides the base functionality
 * needed by any widget. Such includes events,
 * positions, sizes, utility methods, and much more.
 * It is extended by all widgets.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class WAbstractWidget implements ITickableTileEntity, WLayoutElement, WThemable, WStyleProvider, WEventListener {
	protected WInterface linkedInterface;
	protected WLayoutElement parent;

	protected Position position = Position.origin();

	protected Size size = Size.of(0, 0);
	protected Size baseAutoSize = Size.of(0, 0);
	protected Size minimumAutoSize = Size.of(0, 0);
	protected Size maximumAutoSize = Size.of(Integer.MAX_VALUE, Integer.MAX_VALUE);

	protected ITextComponent label = new StringTextComponent("");

	protected boolean isHidden = false;
	protected boolean hasFocus = false;

	protected WCharTypeListener runnableOnCharTyped;
	protected WMouseClickListener runnableOnMouseClicked;
	protected WKeyPressListener runnableOnKeyPressed;
	protected WKeyReleaseListener runnableOnKeyReleased;
	protected WFocusGainListener runnableOnFocusGained;
	protected WFocusLossListener runnableOnFocusReleased;
	protected WTooltipDrawListener runnableOnDrawTooltip;
	protected WMouseReleaseListener runnableOnMouseReleased;
	protected WMouseMoveListener runnableOnMouseMoved;
	protected WMouseDragListener runnableOnMouseDragged;
	protected WMouseScrollListener runnableOnMouseScrolled;
	protected WAlignListener runnableOnAlign;

	protected ResourceLocation theme;
	protected Style styleOverrides = new Style();

	public WAbstractWidget() {
	}

	/**
	 * Retrieves the interface attached to this widget.
	 *
	 * @return The interface attached to this widget.
	 */
	public WInterface getInterface() {
		return linkedInterface;
	}

	/**
	 * Sets the interface attached to this widget.
	 *
	 * @param linkedInterface Interface to be attached to this widget.
	 */
	public <W extends WAbstractWidget> W setInterface(WInterface linkedInterface) {
		this.linkedInterface = linkedInterface;
		return (W) this;
	}

	@Override
	public void tick() {
	}

	/**
	 * Asserts whether this widget has a label or not.
	 *
	 * @return True if labeled; False if not.
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean hasLabel() {
		return !label.getFormattedText().isEmpty();
	}

	/**
	 * Retrieves this widget's label.
	 *
	 * @return This widget's label.
	 */
	@OnlyIn(Dist.CLIENT)
	public ITextComponent getLabel() {
		return label;
	}

	/**
	 * Sets this widget's label as a Text (any type).
	 *
	 * @param label Label to be used by this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setLabel(ITextComponent label) {
		this.label = label;
		onLayoutChange();
		return (W) this;
	}

	/**
	 * Sets this widget's label as a String (formatted into StringTextComponent).
	 *
	 * @param label Label to be used by this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setLabel(String label) {
		this.label = new StringTextComponent(label);
		onLayoutChange();
		return (W) this;
	}

	/**
	 * Asserts whether this widget's label is shadowed or not.
	 *
	 * @return True if shadowed; False if not.
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean isLabelShadowed() {
		return getStyle().asBoolean("label.shadow");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Style getStyle() {
		ResourceLocation widgetId = WidgetRegistry.getId(getClass());
		if (widgetId == null) {
			Class superClass = getClass().getSuperclass();
			while (superClass != Object.class) {
				widgetId = WidgetRegistry.getId(superClass);
				if (widgetId != null) break;
				superClass = superClass.getSuperclass();
			}
		}
		return Style.of(ThemeRegistry.getStyle(getTheme(), widgetId)).mergeFrom(styleOverrides);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ResourceLocation getTheme() {
		if (theme != null) return theme;
		if (parent != null && parent instanceof WThemable) return ((WThemable) parent).getTheme();
		if (linkedInterface != null && linkedInterface.getTheme() != null)
			return linkedInterface.getTheme();
		return DEFAULT_THEME;
	}

	/**
	 * Sets the theme associated with this widget as an ResourceLocation.
	 *
	 * @param theme Theme to be used by this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setTheme(ResourceLocation theme) {
		this.theme = theme;
		return (W) this;
	}

	/**
	 * Sets the theme associated with this widget  as a String (formatted into ResourceLocation).
	 *
	 * @param theme Theme to be used by this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setTheme(String theme) {
		return setTheme(new ResourceLocation(theme));
	}

	/**
	 * Method called when the widget's position needs to be realigned with its anchors, if any.
	 */
	@OnlyIn(Dist.CLIENT)
	public void align() {
	}

	/**
	 * Method called which centers the widget's position in the horizontal (X) and vertical (Y) axis relative to the screen.
	 */
	@OnlyIn(Dist.CLIENT)
	public void center() {
		setPosition(Position.of(getPosition())
				.setX(getParent().getX() + getParent().getWidth() / 2 - getWidth() / 2)
				.setY(getParent().getY() + getParent().getHeight() / 2 - getHeight() / 2));
	}

	/**
	 * Retrieves this widget's position.
	 *
	 * @return This widget's position.
	 */
	@OnlyIn(Dist.CLIENT)
	public Position getPosition() {
		return position;
	}

	/**
	 * Retrieves this widget's parent.
	 *
	 * @return This widget's parent.
	 */
	@OnlyIn(Dist.CLIENT)
	public WLayoutElement getParent() {
		return parent;
	}

	/**
	 * Method called when a change happens in the widget layout of the current interface, which propagates to all parents.
	 */
	@Override
	public void onLayoutChange() {
		if (parent != null) parent.onLayoutChange();
	}

	/**
	 * Sets this widget's parent element.
	 *
	 * @param parent Element to be used as parent.
	 */
	public <W extends WAbstractWidget> W setParent(WLayoutElement parent) {
		this.parent = parent;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public int getWidth() {
		return size.getWidth();
	}

	@OnlyIn(Dist.CLIENT)
	public int getHeight() {
		return size.getHeight();
	}

	/**
	 * Sets this widget's height.
	 *
	 * @param height Value to be used as height.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setHeight(int height) {
		return setSize(Size.of(size).setHeight(height));
	}

	/**
	 * Sets this widget's width.
	 *
	 * @param width Value to be used as width.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setWidth(int width) {
		return setSize(Size.of(size).setWidth(width));
	}

	/**
	 * Sets this widget's position.
	 *
	 * @param position Value to be used as position.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setPosition(Position position) {
		if (!this.position.equals(position)) {
			this.position = position;
			onLayoutChange();
		}
		return (W) this;
	}

	/**
	 * Method called to center this widget on the horizontal (X) axis, relative to the screen.
	 */
	@OnlyIn(Dist.CLIENT)
	public void centerX() {
		setPosition(Position.of(getPosition())
				.setX(getParent().getX() + getParent().getWidth() / 2 - getWidth() / 2));
	}

	/**
	 * Method called to center this widget on the vertical (Y) axis, relative to the screen.
	 */
	@OnlyIn(Dist.CLIENT)
	public void centerY() {
		setPosition(Position.of(getPosition())
				.setY(getParent().getY() + getParent().getHeight() / 2 - getHeight() / 2));
	}

	/**
	 * Overrides a property of this widget's style with a given value.
	 *
	 * @param property Property to be overriden.
	 * @param value    Value for property to be associated with.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W overrideStyle(String property, Object value) {
		styleOverrides.override(property, value);
		return (W) this;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void draw() {
	}

	// WLayoutElement

	/**
	 * Retrieves this widget's size.
	 *
	 * @return This widget's size.
	 */
	@OnlyIn(Dist.CLIENT)
	public Size getSize() {
		return size;
	}

	/**
	 * Retrieves this widget's automatic resizing size, used by self-resizing containers.
	 *
	 * @return This widget's automatic resizing size.
	 */
	@OnlyIn(Dist.CLIENT)
	public Size getBaseAutoSize() {
		return baseAutoSize;
	}

	/**
	 * Retrieves this widget's minimum automatic resizing size, used by self-resizing containers.
	 *
	 * @return This widget's minimum automatic resizing size.
	 */
	@OnlyIn(Dist.CLIENT)
	public Size getMinimumAutoSize() {
		return minimumAutoSize;
	}

	/**
	 * Retrieves this widget's maximum automatic resizing size, used by self-resizing containers.
	 *
	 * @return This widget's maximum automatic resizing size.
	 */
	@OnlyIn(Dist.CLIENT)
	public Size getMaximumAutoSize() {
		return maximumAutoSize;
	}

	/**
	 * Sets the size of this widget.
	 *
	 * @param size Size this widget should assume.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setSize(Size size) {
		if (!this.size.equals(size)) {
			this.size = size;
			onLayoutChange();
		}
		return (W) this;
	}

	/**
	 * Sets this widget's minimum automatic resizing size, used by self-resizing containers.
	 *
	 * @param minimumAutoSize Minimum automatic resizing size this widget should assume.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setMinimumAutoSize(Size minimumAutoSize) {
		if (!this.minimumAutoSize.equals(minimumAutoSize)) {
			this.minimumAutoSize = minimumAutoSize;
		}
		return (W) this;
	}

	/**
	 * Sets this widget's maximum automatic resizing size, used by self-resizing containers.
	 *
	 * @param maximumAutoSize Maximum automatic resizing size this widget should assume.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setMaximumAutoSize(Size maximumAutoSize) {
		if (!this.maximumAutoSize.equals(maximumAutoSize)) {
			this.maximumAutoSize = maximumAutoSize;
		}
		return (W) this;
	}

	/**
	 * Sets this widget's base/default automatic resizing size, used by self-resizing containers.
	 *
	 * @param baseAutoSize Base/Default automatic resizing size this widget should assume.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setBaseAutoSize(Size baseAutoSize) {
		if (!this.baseAutoSize.equals(baseAutoSize)) {
			this.baseAutoSize = baseAutoSize;

		}
		return (W) this;
	}

	/**
	 * Asserts whether this widget only listens to keyboard events when focused; that is,
	 * when {@link #isFocused()} return true.
	 *
	 * @return True if focused listener; False if not.
	 */
	public boolean isFocusedKeyboardListener() {
		return false;
	}

	/**
	 * Asserts whether this widget only listens to mouse events when focused; that is,
	 * when {@link #isFocused()} return true.
	 *
	 * @return True if focused listener; False if not.
	 */
	public boolean isFocusedMouseListener() {
		return false;
	}

	/**
	 * Dispatches {@link #runnableOnKeyPressed}, and calls this method
	 * for any children widget event listeners.
	 *
	 * @param keyCode     Keycode associated with pressed key.
	 * @param character   Character associated with pressed key.
	 * @param keyModifier Modifier(s) associated with pressed key.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onKeyPressed(int keyCode, int character, int keyModifier) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveKeyboard(widget))
					widget.onKeyPressed(keyCode, character, keyModifier);
			}
		}
		if (runnableOnKeyPressed != null) {
			runnableOnKeyPressed.event(this, keyCode, character, keyModifier);
		}
	}

	/**
	 * Dispatches {@link #runnableOnKeyReleased}, and calls this method
	 * for any children widget event listeners.
	 *
	 * @param keyCode     Keycode associated with pressed key.
	 * @param character   Character associated with pressed key.
	 * @param keyModifier Modifier(s) associated with pressed key.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onKeyReleased(int keyCode, int character, int keyModifier) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveKeyboard(widget))
					widget.onKeyReleased(keyCode, character, keyModifier);
			}
		}
		if (runnableOnKeyReleased != null) {
			runnableOnKeyReleased.event(this, keyCode, character, keyModifier);
		}
	}

	/**
	 * Dispatches {@link #runnableOnCharTyped}, and calls this method
	 * for any children widget event listeners.
	 *
	 * @param character Character associated with key pressed.
	 * @param keyCode   Keycode associated with key pressed.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onCharTyped(char character, int keyCode) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveKeyboard(widget))
					widget.onCharTyped(character, keyCode);
			}
		}
		if (runnableOnCharTyped != null) {
			runnableOnCharTyped.event(this, character, keyCode);
		}
	}

	/**
	 * Dispatches {@link #runnableOnFocusGained}, and calls this method
	 * for any children widget event listeners.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onFocusGained() {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget) && ((WAbstractWidget) widget).isFocused()) {
					widget.onFocusGained();
				}
			}
		}
		if (runnableOnFocusGained != null && isFocused()) {
			runnableOnFocusGained.event(this);
		}
	}

	/**
	 * Dispatches {@link #runnableOnFocusReleased}, and calls this method
	 * for any children widget event listeners.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onFocusReleased() {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget) && !((WAbstractWidget) widget).isFocused()) {
					widget.onFocusReleased();
				}
			}
		}
		if (runnableOnFocusReleased != null && !isFocused()) {
			runnableOnFocusReleased.event(this);
		}
	}

	/**
	 * Dispatches {@link #runnableOnMouseReleased}, and calls this method
	 * for any children widget event listeners.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onMouseReleased(mouseX, mouseY, mouseButton);
			}
		}
		if (runnableOnMouseReleased != null) {
			runnableOnMouseReleased.event(this, mouseX, mouseY, mouseButton);
		}
	}

	/**
	 * Dispatches {@link #runnableOnMouseClicked}, and calls this method
	 * for any children widget event listeners.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget))
					widget.onMouseClicked(mouseX, mouseY, mouseButton);
			}
		}
		if (runnableOnMouseClicked != null) {
			runnableOnMouseClicked.event(this, mouseX, mouseY, mouseButton);
		}
	}

	/**
	 * Dispatches {@link #runnableOnMouseDragged}, and calls this method
	 * for any children widget event listeners.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseDragged(int mouseX, int mouseY, int mouseButton, double deltaX, double deltaY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget))
					widget.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
			}
		}
		if (runnableOnMouseDragged != null) {
			runnableOnMouseDragged.event(this, mouseX, mouseY, mouseButton, deltaX, deltaY);
		}
	}

	/**
	 * Dispatches {@link #runnableOnMouseMoved}, and calls this method
	 * for any children widget event listeners.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseMoved(int mouseX, int mouseY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (widget instanceof WAbstractWidget) {
					WAbstractWidget updateWidget = ((WAbstractWidget) widget);
					boolean then = updateWidget.hasFocus;
					updateWidget.updateFocus(mouseX, mouseY);
					boolean now = updateWidget.hasFocus;

					if (then && !now) {
						updateWidget.onFocusReleased();
					} else if (!then && now) {
						updateWidget.onFocusGained();
					}

				}
				if (EventUtilities.canReceiveMouse(widget)) widget.onMouseMoved(mouseX, mouseY);
			}
		}
		if (runnableOnMouseMoved != null) {
			runnableOnMouseMoved.event(this, mouseX, mouseY);
		}
	}

	/**
	 * Method called to update this widget's focus status.
	 *
	 * @param positionX The horizontal (X) position based on which to calculate focus.
	 * @param positionY The vertical (Y) position based on which to calculate focus.
	 * @return True if focused; False if not.
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean updateFocus(int positionX, int positionY) {
		if (isHidden()) {
			return false;
		}

		setFocus(isWithinBounds(positionX, positionY));
		return isFocused();
	}

	/**
	 * Asserts whether this widget is hidden or not.
	 *
	 * @return True if hidden; False if not.
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean isHidden() {
		return isHidden;
	}

	/**
	 * Sets the widget's hidden state.
	 *
	 * @param isHidden Boolean representing true (hidden) or false (visible).
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setHidden(boolean isHidden) {
		this.isHidden = isHidden;
		setFocus(false);
		return (W) this;
	}

	/**
	 * Sets the widget's focus state.
	 *
	 * @param hasFocus Boolean representing true (focused) or false (unfocused).
	 */
	@OnlyIn(Dist.CLIENT)
	public void setFocus(boolean hasFocus) {
		if (!isFocused() && hasFocus) {
			this.hasFocus = hasFocus;
		}
		if (isFocused() && !hasFocus) {
			this.hasFocus = hasFocus;
		}
	}

	/**
	 * Asserts whether this widget is focused or not.
	 *
	 * @return True if focused; false if not.
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean isFocused() {
		return hasFocus;
	}

	/**
	 * Asserts whether this widget is within boundaries of specified parameters or not.
	 *
	 * @param positionX The horizontal (X) position based on which to calculate boundaries.
	 * @param positionY The vertical (Y) position based on which to calculate boundaries.
	 * @return True if within boundaries; False if not.
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean isWithinBounds(int positionX, int positionY) {
		return isWithinBounds(positionX, positionY, 0);
	}

	/**
	 * Asserts whether this widget is within boundaries of specified parameters or not,
	 * given a vertical and horizontal tolerance.
	 *
	 * @param positionX The horizontal (X) position based on which to calculate boundaries.
	 * @param positionY The vertical (Y) position based on which to calculate boundaries.
	 * @param tolerance The horizontal (X) and vertical (Y) tolerance based on which to calculate boundaries.
	 * @return True if within boundaries; False if not.
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean isWithinBounds(int positionX, int positionY, int tolerance) {
		return positionX + tolerance > getX()
				&& positionX - tolerance < getX() + getWidth()
				&& positionY + tolerance > getY()
				&& positionY - tolerance < getY() + getHeight();
	}

	@OnlyIn(Dist.CLIENT)
	public int getX() {
		return position.getX();
	}

	@OnlyIn(Dist.CLIENT)
	public int getY() {
		return position.getY();
	}

	@OnlyIn(Dist.CLIENT)
	public int getZ() {
		return position.getZ();
	}

	/**
	 * Sets this widget's depth (Z) position.
	 *
	 * @param z Value to be used as depth (Z) position.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setZ(int z) {
		return setPosition(Position.of(position).setZ(z));
	}

	/**
	 * Sets this widget's vertical (Y) position.
	 *
	 * @param y Value to be used as vertical (Y) position.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setY(int y) {
		return setPosition(Position.of(position).setY(y));
	}

	/**
	 * Sets this widget's horizontal (X) position.
	 *
	 * @param x Value to be used as horizontal (X) position.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setX(int x) {
		return setPosition(Position.of(position).setX(x));
	}

	/**
	 * Dispatches {@link #runnableOnMouseScrolled}, and calls this method
	 * for any children widget event listeners.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseScrolled(int mouseX, int mouseY, double deltaY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget)) {
					widget.onMouseScrolled(mouseX, mouseY, deltaY);
				}
			}
		}
		if (runnableOnMouseScrolled != null) {
			runnableOnMouseScrolled.event(this, mouseX, mouseY, deltaY);
		}
	}

	/**
	 * Dispatches {@link #runnableOnDrawTooltip}, and calls this method
	 * for any children widget event listeners.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onDrawTooltip(int mouseX, int mouseY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onDrawTooltip(mouseX, mouseY);
			}
		}
		if (runnableOnDrawTooltip != null) {
			runnableOnDrawTooltip.event(this, mouseX, mouseY);
		}
	}

	/**
	 * Dispatches {@link #runnableOnAlign}, and calls this method
	 * for any children widget event listeners. Also dispatches
	 * a layout change in case positions or sizes changed.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void onAlign() {
		if (runnableOnAlign != null) {
			runnableOnAlign.event(this);
		}
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onAlign();
			}
		}
		onLayoutChange();
	}

	/**
	 * Retrieves this widget's event called when {@link #onFocusGained()} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WFocusGainListener<W> getOnFocusGained() {
		return runnableOnFocusGained;
	}

	/**
	 * Sets this widget's event called when {@link #onFocusGained()} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnFocusGained(WFocusGainListener<W> linkedRunnable) {
		this.runnableOnFocusGained = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onFocusReleased()} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WFocusLossListener<W> getOnFocusReleased() {
		return runnableOnFocusReleased;
	}

	/**
	 * Sets this widget's event called when {@link #onFocusReleased()} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnFocusReleased(WFocusLossListener<W> linkedRunnable) {
		this.runnableOnFocusReleased = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onKeyPressed(int, int, int)} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WKeyPressListener<W> getOnKeyPressed() {
		return runnableOnKeyPressed;
	}

	/**
	 * Sets this widget's event called when {@link #onKeyPressed(int, int, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnKeyPressed(WKeyPressListener<W> linkedRunnable) {
		this.runnableOnKeyPressed = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onCharTyped(char, int)}  is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WCharTypeListener<W> getOnCharTyped() {
		return runnableOnCharTyped;
	}

	/**
	 * Sets this widget's event called when {@link #onCharTyped(char, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnCharTyped(WCharTypeListener<W> linkedRunnable) {
		this.runnableOnCharTyped = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onKeyReleased(int, int, int)}  is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WKeyReleaseListener<W> getOnKeyReleased() {
		return runnableOnKeyReleased;
	}

	/**
	 * Sets this widget's event called when {@link #onKeyReleased(int, int, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnKeyReleased(WKeyReleaseListener<W> linkedRunnable) {
		this.runnableOnKeyReleased = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseClicked(int, int, int)} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseClickListener<W> getOnMouseClicked() {
		return runnableOnMouseClicked;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseClicked(int, int, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseClicked(WMouseClickListener<W> linkedRunnable) {
		this.runnableOnMouseClicked = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseDragged(int, int, int, double, double)} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseDragListener<W> getOnMouseDragged() {
		return runnableOnMouseDragged;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseDragged(int, int, int, double, double)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseDragged(WMouseDragListener<W> linkedRunnable) {
		this.runnableOnMouseDragged = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseMoved(int, int)} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseMoveListener<W> getOnMouseMoved() {
		return runnableOnMouseMoved;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseMoved(int, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseMoved(WMouseMoveListener<W> linkedRunnable) {
		this.runnableOnMouseMoved = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseScrolled(int, int, double)} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseScrollListener<W> getOnMouseScrolled() {
		return runnableOnMouseScrolled;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseScrolled(int, int, double)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseScrolled(WMouseScrollListener<W> linkedRunnable) {
		this.runnableOnMouseScrolled = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseReleased(int, int, int)} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseReleaseListener<W> getOnMouseReleased() {
		return runnableOnMouseReleased;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseReleased(int, int, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseReleased(WMouseReleaseListener<W> linkedRunnable) {
		this.runnableOnMouseReleased = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onDrawTooltip(int, int)} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WTooltipDrawListener<W> getOnDrawTooltip() {
		return runnableOnDrawTooltip;
	}

	/**
	 * Sets this widget's event called when {@link #onDrawTooltip(int, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnDrawTooltip(WTooltipDrawListener<W> linkedRunnable) {
		this.runnableOnDrawTooltip = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onAlign()} is called.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WAlignListener<W> getOnAlign() {
		return runnableOnAlign;
	}

	/**
	 * Sets this widget's event called when {@link #onAlign()} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnAlign(WAlignListener<W> linkedRunnable) {
		this.runnableOnAlign = linkedRunnable;
		return (W) this;
	}
}