package uielements;

import java.awt.Color;
import java.awt.Graphics;

public class Checkbox extends UIElement {

	/*
	 * Constructor of the checkbox.
	 * @param x: The x position of the left top corner of the checkbox.
	 * @param y: The y position of the left top corner of the checkbox.
	 * @param checked: Whether the checkbox is checked or not.
	 */
	public Checkbox(int x, int y,int w, int h, boolean checked){
		super(x, y,w,h);
		this.checked = checked;
	}

	/**
	 * 	Toggles the state of this checkbox ON->OFF or OFF->ON
	 */
	public void toggle(){
		checked = !checked;
	}
	
	public void greyOut(){
		this.greyedOut = true;
	}
	
	public void unGreyOut(){
		this.greyedOut = false;
	}

	/**
	 * Whether this checkbox is checked or not
	 */
	private boolean checked = true;
	private boolean greyedOut = false;
	
	@Override
	public void paint(Graphics g){
		if (greyedOut) g.fillRect(super.getX(), super.getY(), super.getWidth(), super.getHeight());	
		else if(checked){
			int x1 = super.getX();
			int y1 = super.getY();
			int x2 = super.getX()+super.getWidth();
			int y2 = super.getY()+super.getHeight();
			g.drawLine(x1, y1, x2, y2);
			g.drawLine(x1, y2, x2, y1);
		}
		g.drawRect(super.getX(), super.getY(), super.getWidth(), super.getHeight());
	}

	@Override
	public void handleSingleClick() {
		//System.out.println("Checkbox pressed");
		toggle();
		for (Runnable r : this.singleClickListeners) {
			r.run();
		}
		
	}

	@Override
	public void handleDoubleClick() {
		return;
	}

	@Override
	public void handleKeyboardEvent(int keyCode, char keyChar) {
		if (keyboardListeners.get(keyCode) == null)
			return;
		
		for (Runnable r : keyboardListeners.get(keyCode)) {
			r.run();
		}
	}


}
