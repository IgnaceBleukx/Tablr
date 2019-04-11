package uielements;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

import javax.naming.CommunicationException;

public class TextField extends UIElement {

	/**
	 * Constructor of the TextField.
	 * @param x: The x position of the left top corner of the TextField.
	 * @param y: The y position of the left top corner of the TextField.
	 * @param checked: Whether the TextField is checked or not.
	 */
	public TextField(int x, int y,int w, int h, String t) {
		super(x, y, w, h);
		setText(t);
	}
	
	/**
	 * The text in this Textfield
	 */
	private String text;

	/**
	 * Last text before started editing
	 */
	private String prevText;

	public String getPrevText() {
		return prevText;
	}

	public void restoreText() {
		System.out.println("[Textfield.java:37] Restoring text: "+getPrevText());
		setText(getPrevText());
	}
	
	/**
	 * This method sets the text of the current TextBox
	 * @param t: The text to be set.
	 */
	public void setText(String t){
		if (t == null) this.text = "";
		else this.text = t;
	}
	
	/**
	 * This method returns the text of the current TextBox.
	 */
	public String getText(){
		return this.text;
	}

	@Override
	public void paint(Graphics g) {
		FontMetrics metrics =  g.getFontMetrics(g.getFont());
		g.setColor(Color.white);
		g.fillRect(super.getX(),super.getY(), super.getWidth(), super.getHeight());
					
		
		if (getError()){
			g.setColor(Color.red);
			g.drawRect(super.getX()+1,super.getY()+1, super.getWidth()-2, super.getHeight()-2);
		}else
			g.setColor(Color.black);
		g.drawRect(super.getX(),super.getY(), super.getWidth(), super.getHeight());
		g.setColor(Color.black);
		int y = this.getY() +  ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
		
		if (!this.isSelected())
			g.drawString(this.getText(), super.getX()+10, y);
		else
			g.drawString(this.getText() + "<", super.getX()+10, y);
	}
	
	@Override
	public void handleSingleClick() {

		if (!isSelected()) {
			//setSelected();
			getUI().getTablr().notifyNewSelected(this);
			this.prevText = getText();
		}
	}
	
	
	@Override
	public void handleDoubleClick() {
		new ArrayList<>(doubleClickListeners).forEach(l -> l.run());
	}
	
	@Override
	public void handleKeyboardEvent(int keyCode, char keyChar) {
		if (keyCode == 10 && isSelected() == true) { //Enter, end editing
			if (!getError())
				setNotSelected();
		}

		if (keyCode == 27 &&  isSelected()) {
			restoreText();
			setNotSelected();
			if(getError()) isNotError();
		}
				
		if (keyCode == 8 && isSelected()) { //Backspace: remove character
			if (getText().length() > 0) {
				String newText = getText().substring(0, getText().length() - 1);
				setText(newText);
			}	
		}

		
		if ((Character.isLetterOrDigit(keyChar) || keyChar == '@' || keyChar == '.') && isSelected()) {
			setText(getText()+keyChar);
		}
		
		
		if (new HashMap<Integer, ArrayList<Runnable>>(keyboardListeners).get(keyCode) == null)
			return;
		
		new HashMap<Integer, ArrayList<Runnable>>(keyboardListeners).get(keyCode).stream().forEach(l -> l.run());

	}
	
	@Override
	public void handleDrag(int x, int y) {
		new ArrayList<BiConsumer<Integer,Integer>>(dragListeners).stream().forEach(r -> r.accept(x, y));
	}

}
