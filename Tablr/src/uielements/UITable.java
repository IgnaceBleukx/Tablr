package uielements;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.BiConsumer;

import Utils.GeometricUtils;
import Utils.Rounder;
import domain.Cell;
import domain.Column;
import domain.Table;
import domain.Type;
import facades.Tablr;
import facades.WindowManager;
import ui.UI;

public class UITable extends UIElement {

	/**
	 * Constructor of the Table.
	 * @param x: The x position of the left top corner of the Table.
	 * @param y: The y position of the left top corner of the Table.
	 * @param legend: The legendary of the table.
	 * @param rows: The rows of the table.
	 */
	public UITable(int x, int y, int w, int h,UIRow legend, ArrayList<UIRow> newRows) {
		super(x, y,w,h);
		this.setLegend(legend);
		this.addAllRows(rows);
		scrollBarH.setUI(getUI());
		scrollBarV.setUI(getUI());
		updateScrollBars();
		
		//Adding listeners to scroll
		scrollBarV.addPressListener((e) -> {
			new ArrayList<>(pressListeners).stream().forEach(l -> l.accept(scrollBarV));
		});
		scrollBarV.addScrollListener((delta)->{
			rows.stream().forEach(r -> r.move(0,-delta));
		});
		scrollBarV.addDragListener((newX,newY) ->{
			int delta = newY - scrollBarV.getGrabPointY();
			scrollBarV.scroll(delta);
			scrollBarV.setGrabPointX(newX);
			scrollBarV.setGrabPointY(newY);
		});
		
		scrollBarH.addPressListener((e) -> {
			new ArrayList<>(pressListeners).stream().forEach(l -> l.accept(scrollBarH));
		});
		scrollBarH.addScrollListener((delta) -> {
			System.out.println("[UITable.java:53]: Rows in UITable:" + rows);
			rows.stream().forEach(r -> r.move(-delta,0));
			legend.move(-delta,0);
		});
		scrollBarH.addDragListener((newX,newY) -> {
			int delta = newX - scrollBarH.getGrabPointX();
			scrollBarH.scroll(delta);
			scrollBarH.setGrabPointX(newX);
			scrollBarV.setGrabPointY(newY);
		});
		
	}

	/**
	 * the rows stored in this table
	 */
	ArrayList<UIRow> rows =  new ArrayList<UIRow>();
	
	public ArrayList<UIRow> getRows() {
		return new ArrayList<UIRow>(rows);
	}

	/**
	 * the upper row of this table that contains the legend of all the columns
	 */
	UIRow legend;
	
	public UIRow getLegend() {
		return legend;
	}
	
	private static int scrollBarW = 10;
	
	VerticalScrollBar scrollBarV = new VerticalScrollBar(getEndX()-scrollBarW,getY()+20,scrollBarW,getHeight()-scrollBarW-20);
	HorizontalScrollBar scrollBarH = new HorizontalScrollBar(getX(),getEndY()-scrollBarW,getWidth()-scrollBarW,scrollBarW);
	
	private void updateScrollBars(){
		int elementsStartY = rows.stream().mapToInt(e -> e.getY()).sorted().findFirst().orElse(legend.getEndY());
		int elementsEndY = rows.stream().map(e -> e.getEndY()).sorted(Comparator.reverseOrder()).findFirst().orElse(getEndY());
		
		int elementsStartX = rows.stream().mapToInt(e -> e.getX()).sorted().findFirst().orElse(legend.getX());
		int elementsEndX = rows.stream().map(e -> e.getEndX()).sorted(Comparator.reverseOrder()).findFirst().orElse(legend.getEndX());

		scrollBarV.update(elementsStartY, elementsEndY,legend.getEndY(),getEndY()-scrollBarW);
		scrollBarH.update(elementsStartX, elementsEndX,getX(),getEndX()-scrollBarW);
	}
	
	/**
	 * This method sets the legend for the UITable.
	 * @param newLegend
	 */
	public void setLegend(UIRow newLegend){
		newLegend.setUI(getUI());
		this.legend = newLegend;
	}
	
	
	/**
	 * adds a row to the table
	 * @param row: the row to be added
	 */
	public void addRow(UIRow row){
		this.rows.add(row);
		row.setUI(getUI());
		updateScrollBars();
	}

	/**
	 * This method adds an arraylist of rows to the current UITable.
	 * @param rows 	The arraylist containing the rows to be added.
	 */
	public void addAllRows(ArrayList<UIRow> rows){
		rows.stream().forEach(r -> this.addRow(r));
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawRect(getX(), getY(), getWidth(), getHeight());
		Shape oldClip = g.getClip();
		int[] i = GeometricUtils.intersection(getX(), legend.getEndY(), getWidth(), getHeight()-legend.getHeight(), oldClip.getBounds().x, 
												oldClip.getBounds().y, oldClip.getBounds().width, oldClip.getBounds().height);
		g.setClip(new Rectangle(i[0],i[1],i[2],i[3]));
		rows.stream().forEach(r -> r.paint(g));
		i = GeometricUtils.intersection(legend.getX(), legend.getY(), legend.getWidth(), legend.getHeight(), oldClip.getBounds().x, 
				oldClip.getBounds().y, oldClip.getBounds().width, oldClip.getBounds().height);
		g.setClip(new Rectangle(i[0],i[1],i[2],i[3]));
		legend.paint(g);
		g.setClip(oldClip);
		if (getSelected() != null) {
			UIElement s = this.getSelected();
			g.fillOval(s.getX()+s.getWidth()+10, s.getY()+s.getHeight()/2, 8, 8);
		}
		scrollBarH.paint(g);
		scrollBarV.paint(g);
	}

	/**
	 * The currently selected element in this table
	 */
	private UIElement selected;
	
	public UIElement getSelected() {
		return selected;
	}

	/**
	 * Returns the most specific UIElement located at (x,y) by searching in its rows and legend
	 * @param x		X Coordinate
	 * @param y		Y Coordinate
	 * @return		UIElement a at position (x,y)
	 * @note		If possible and correct, UIElements inside containers will be returned
	 */
	@Override
	public UIElement locatedAt(int x, int y) {
		if (!containsPoint(x,y)) return null;

		UIElement found = null;

		
		found = legend.locatedAt(x,y); //Look in legend
		if (found != null) return found;

		found = scrollBarV.locatedAt(x, y);//Look in scrollbars
		if (found != null) return found;
		found = scrollBarH.locatedAt(x, y);
		if (found != null) return found;
		
		rows.stream().forEach(e -> System.out.println(e));
		for (UIRow e : rows) { //Look in rows
			found = e.locatedAt(x,y);
			if (found != null)
				return found;
		}
		return this; //If no elements in the table match, return this.
	}

	@Override
	public void handleSingleClick() {
		new ArrayList<>(singleClickListeners).forEach(l -> l.run());
	}

	@Override
	public void handleDoubleClick() {
		new ArrayList<>(doubleClickListeners).forEach(l -> l.run());
	}
	
	@Override
	public void handleKeyboardEvent(int keyCode, char keyChar) {
		for(int i=0;i<rows.size();i++){
			rows.get(i).handleKeyboardEvent(keyCode, keyChar);
		}
		if (new HashMap<Integer, ArrayList<Runnable>>(keyboardListeners).get(keyCode) == null)
			return;
		new HashMap<Integer, ArrayList<Runnable>>(keyboardListeners).get(keyCode).stream().forEach(l -> l.run());
	}
	
	@Override
	public void handleDrag(int x, int y) {
		new ArrayList<BiConsumer<Integer,Integer>>(dragListeners).stream().forEach(r -> r.accept(x, y));
	}
	
	/**
	 * Selects a new element
	 */
	@Override
	public void selectElement(UIElement e) {
		if (e==this) 
			select();
		else
			deselect();
		
		for (UIElement el : rows) {
			el.selectElement(e);
		}
	}
	
	/**
	 * Sets the UI
	 */
	@Override
	public void setUI(UI ui) {
		this.ui = ui;
		rows.stream().forEach(r -> r.setUI(ui));
		legend.setUI(ui);
		scrollBarV.setUI(ui);
		scrollBarH.setUI(ui);
	}
	
	/**
	 * Whether this element is in error
	 */
	@Override
	public boolean getError() {
		for (UIElement e : this.rows) {
			if (e.getError())
				return true;
		}
		return false;
	}
	
	/**
	 * Move
	 */
	@Override
	public void move(int deltaX, int deltaY) {
		setX(getX() + deltaX);
		setY(getY() + deltaY);
		legend.move(deltaX, deltaY);
		rows.stream().forEach(e -> e.move(deltaX, deltaY));
		scrollBarH.move(deltaX, deltaY);
		scrollBarV.move(deltaX, deltaY);
	}
	
	/**
	 * Resize to the left
	 */
	@Override
	public void resizeL(int deltaX){
		this.setWidth(getWidth()-deltaX);
		this.setX(getX()+deltaX);
		legend.move(deltaX,0);
		rows.stream().forEach(r -> r.move(deltaX,0));
		scrollBarV.resizeL(deltaX);
		scrollBarH.resizeL(deltaX);
		updateScrollBars();
	}
	
	/**
	 * Resize to the right
	 */
	@Override
	public void resizeR(int deltaX){
		UIElement border = null;
		for (UIRow r : rows) {
			if (r.getX() < this.getX()) {
				border = r;
				break;
			}
		}
		if (border != null) {
			rows.stream().forEach(r -> r.move(deltaX, 0));
			legend.move(deltaX,0);
		}
		this.setWidth(getWidth()+deltaX);
		scrollBarV.resizeR(deltaX);
		scrollBarH.resizeR(deltaX);
		updateScrollBars();
	}
	
	/**
	 * Resize to the bottom
	 */
	@Override
	public void resizeB(int deltaY){
		UIElement border = null;
		for (UIRow r :  rows) {
			if (r.getY() < legend.getEndY()) {
				border = r;
				break;
			}
		}
		if (border != null) {
			rows.stream().forEach(r -> r.move(0, deltaY));
		}
		this.setHeight(getHeight()+deltaY);
		scrollBarV.resizeB(deltaY);
		scrollBarH.resizeB(deltaY);
		updateScrollBars();
	}
	
	/**
	 * Resize to the top
	 */
	@Override
	public void resizeT(int deltaY){
		this.setHeight(getHeight()-deltaY);
		this.setY(getY()+deltaY);
		legend.move(0,deltaY);
		rows.stream().forEach(r -> r.move(0, deltaY));
		scrollBarV.resizeT(deltaY);
		scrollBarH.resizeT(deltaY);
		updateScrollBars();
	}
	
	/**
	 * Clones this object
	 */
	@Override
	public UITable clone(){
		ArrayList<UIRow> clonedRows = new ArrayList<UIRow>();
		rows.stream().forEach(e -> clonedRows.add(e.clone()));
		UITable clone = new UITable(getX(),getY(),getWidth(),getHeight(),legend.clone(),clonedRows);
		clone.scrollBarH = scrollBarH;
		clone.scrollBarV = scrollBarV;
		return clone;
	}

}
