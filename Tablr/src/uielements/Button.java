package uielements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class Button extends UIElement {

	/** Constructor of the Button.
	 * @param x: The x position of the left top corner of the Button.
	 * @param y: The y position of the left top corner of the Button.
	 * @param text: The text of the button.
	 */
	public Button(int x, int y,int w, int h, String text){
		super(x,y, w, h);
		setText(text);
	}
	
	/**
	 * The (optional) text inside the button
	 */
	private String text ;
	
	/**
	 * This method returns the text of the current Button.
	 */
	public String getText(){
		return this.text;
	}
	
	/**
	 * @param t: The text to be set to the current Button.
	 * This method sets the text of the current Button.
	 */
	public void setText(String t){
		this.text = t;
	}
	
	/**
	 * Paints the Button on the Canvas.
	 */
	@Override
	public void paint(Graphics g) {
	    // Drawing button
		g.setColor(getColor());
		int arcWidth = 8;
		int arcHeight = 8;
		g.fillRoundRect(super.getX(), super.getY(), super.getWidth(), super.getHeight(), arcWidth, arcHeight);
		g.setColor(Color.BLACK);
		g.drawRoundRect(super.getX(), super.getY(), super.getWidth(), super.getHeight(), arcWidth, arcHeight);
		//Drawing text on button:
		Shape oldClip = g.getClip();
		g.setClip(getX(),getY(),getWidth(),getHeight());
		drawCenteredText(g, this.getText());
		g.setClip(oldClip);
	}
	
	/**
	 * Handles a single click by running all actions associated with it.
	 */
	@Override
	public void handleSingleClick() {
		new ArrayList<>(singleClickListeners).stream().forEach(l -> l.run());
	} 
	
	/**
	 * Handles a double click by running all actions associated with it.
	 */
	@Override
	public void handleDoubleClick() {
		new ArrayList<>(doubleClickListeners).stream().forEach(l -> l.run());
	}
	
	/**
	 * Handles a keyboard event by running all actions associated with the pressed key.
	 */
	@Override
	public void handleKeyboardEvent(int keyCode, char keyChar) {
		if (new HashMap<Integer, ArrayList<Runnable>>(keyboardListeners).get(keyCode) == null)
			return;
		new HashMap<Integer, ArrayList<Runnable>>(keyboardListeners).get(keyCode).stream().forEach(l -> l.run());
	}
	
	/**
	 * Handles dragging by running all actions associated with dragging this element.
	 */
	@Override
	public void handleDrag(int x, int y) {
		new ArrayList<BiConsumer<Integer,Integer>>(dragListeners).stream().forEach(r -> r.accept(x, y));
	}
	
	/**
	 * Clones this object
	 */
	@Override
	public Button clone(){
		return new Button(getX(),getY(),getWidth(),getHeight(),getText());
	}

	
}
