package facades;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import domain.Table;
import ui.TableDesignModeUI;
import ui.TablesModeUI;
import ui.TableRowsModeUI;
import ui.UI;
import uielements.Text;
import uielements.UIElement;

/**
 * Facade for the UI part of the program. 
 * Methods should always be called via a reference to the CommunicationManager,
 * and not directly via this class.
 */
public class WindowManager {
	private Tablr tablr;
	
	public WindowManager(Tablr c) {
		tablr = c;
		tablesModeUIs = new ArrayList<TablesModeUI>();
		tableRowsModeUIs = new HashMap<Table,TableRowsModeUI>();
		tableDesignModeUIs = new HashMap<Table,TableDesignModeUI>();
		
	}
	
	public Tablr getCommunicationManager() {
		return tablr;
	}
	
	private ArrayList<TablesModeUI> tablesModeUIs;
	private HashMap<Table,TableRowsModeUI> tableRowsModeUIs;
	private HashMap<Table,TableDesignModeUI> tableDesignModeUIs;
	
	/**
	 * The selectedUI is the only UI that receives keyboard input
	 */
	private UI selectedUI;
	
	private ArrayList<UI> getUIs() {
		ArrayList<UI> uis = new ArrayList<>();
		tablesModeUIs.stream().forEach(x -> uis.add(x));
		tableRowsModeUIs.values().stream().forEach(x -> uis.add(x));
		tableDesignModeUIs.values().stream().forEach(x -> uis.add(x));
		return uis;
	}
	
	/**
	 * Returns all UIElements in ALL UI's
	 */
	private ArrayList<UIElement> getAllElements() {
		ArrayList<UIElement> elements = new ArrayList<>();
		tablesModeUIs.stream().forEach(ui -> elements.addAll(ui.getElements()));
		tableRowsModeUIs.values().stream().forEach(ui -> elements.addAll(ui.getElements()));
		tableDesignModeUIs.values().stream().forEach(ui -> elements.addAll(ui.getElements()));
		return elements;
	}
	
	/**
	 * Returns all UIElements in ALL UI's
	 */
	private ArrayList<UIElement> getAllActiveElements() {
		ArrayList<UIElement> elements = new ArrayList<>();
		tablesModeUIs.stream().filter(e -> e.isActive()).forEach(ui -> elements.addAll(ui.getElements()));
		tableRowsModeUIs.values().stream().filter(e -> e.isActive()).forEach(ui -> elements.addAll(ui.getElements()));
		tableDesignModeUIs.values().stream().filter(e -> e.isActive()).forEach(ui -> elements.addAll(ui.getElements()));
		return elements;
	}
	
	public void addTablesModeUI(TablesModeUI ui) {
		tablesModeUIs.add(ui);
		loadTablesModeUI(ui);
	}
	
	public void loadTablesModeUI(TablesModeUI ui){
		ui.setTablr(tablr);
		ui.setWindowManager(this);
		this.selectedUI = ui;
		ui.loadUI();
	}
	
	public void loadTableRowsModeUI(Table table){
		TableRowsModeUI ui = tableRowsModeUIs.get(table);
		this.selectedUI = ui;
		ui.setTablr(tablr);
		ui.setWindowManager(this);
		ui.loadUI(table);
	}
	
	public void loadTableDesignModeUI(Table table){
		TableDesignModeUI ui = tableDesignModeUIs.get(table);
		ui.setTablr(tablr);
		ui.setWindowManager(this);
		ui.loadUI(table);
	}
	
	public void addTableDesignModeUI(Table table, TableDesignModeUI ui){
		this.tableDesignModeUIs.put(table, ui);
	}
	
	public void addTableRowsModeUI(Table table, TableRowsModeUI ui){
		this.tableRowsModeUIs.put(table,ui);
	}

	
	/**
	 * Returns the UI at point (x,y).
	 * If multiple UI's overlap, the current active UI will be returned.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public UI getUIAt(int x, int y) {
		if (selectedUI.containsPoint(x, y)) {
			return selectedUI;
		}
		else {
			System.out.println("[WindowManager.java:118] " + getUIs());
			for (UI ui : getUIs()) {
				System.out.println("[WindowManager.java:121] " + ui);
				if (ui.isActive() && ui.containsPoint(x,y)) return ui;
			}
		}
	
		return null;
	}
	
	public void newSelected(UIElement e) {
		getAllElements().stream().forEach(el -> el.selectElement(e));
	}
	
	public void getSelectionLock(UIElement e) {
		this.lockedSelectedElement = e;
	}
	
	public void releaseSelectionLock(UIElement e) {
		if (lockedSelectedElement != e)
			throw new IllegalArgumentException("Trying to release selection lock from non-selected element");
		lockedSelectedElement = null;
	}
	
	public void clearUIAt(int x,int y) {
		getUIAt(x,y).clear();
	}

	public void getLock(UIElement e) {
		hardLockedElement = e;
	}

	public void releaseLock(UIElement e) {
		if (hardLockedElement != e)
			throw new IllegalArgumentException("Trying to release hard lock from non-selected element");
		hardLockedElement = null;	
	}


	public ArrayList<UIElement> getElementsUIAt(int x, int y) {
		return new ArrayList<UIElement>(getUIAt(x,y).getElements());
	}

	public void paint(Graphics g) {
		//Paint all UI's that are active
		tablesModeUIs.stream().filter(u->u.isActive() && !u.equals(selectedUI)).forEach(e -> e.paint(g));
		tableRowsModeUIs.values().stream().filter((u) -> u.isActive() && !u.equals(selectedUI)).forEach((e) -> e.paint(g));
		tableDesignModeUIs.values().stream().filter((u) -> u.isActive() && !u.equals(selectedUI)).forEach((e) -> e.paint(g));
		if (selectedUI != null) selectedUI.paint(g);
 	}
	
	public ArrayList<TablesModeUI> getTablesModeUIs() {
		return new ArrayList<TablesModeUI>(tablesModeUIs);
	}
	
	public HashMap<Table,TableDesignModeUI> getTableDesignUIs() {
		return new HashMap<Table,TableDesignModeUI>(tableDesignModeUIs);
	}
	
	public HashMap<Table,TableRowsModeUI> getTableRowsUIs() {
		return new HashMap<Table,TableRowsModeUI>(tableRowsModeUIs);
	}
	
	public UI getSelectedUI() {
		return this.selectedUI;
	}
	
	public void selectUI(UI u) {
		selectedUI.deselect();
		this.selectedUI = u;
		if (u != null) u.select();
	}
	
	/**
	 * The hardlockedElement is the only element that receives
	 * Mouse and keyboard input. Used to lock the UI when an
	 * element is in an incorrent state (e.g. invalid text value).
	 */
	public UIElement hardLockedElement = null;
	
	
	/**
	 * A lockedSelectedElement prevents other elements from being selected.
	 * It does not affect keyboard and mouse input.
	 */
	public UIElement lockedSelectedElement;
	
	/**
	 * @return	null if no element has a hard lock,
	 * 			the locked element otherwise
	 */
	public UIElement getLockedElement() {
		return hardLockedElement;
	}
	
	/**
	 * Select element newElement. 
	 * Behaviour varies depending on whether or not an element is blocking the UI from selecting different elements.
	 * @param newElement: the element that wants to be selected
	 */
	public void selectElement(UIElement newElement) {
		//An element has placed a lock on selecting other elements
		if (lockedSelectedElement != null) { 
			System.out.println("[WindowManager.java:164] cannot select because locked.");
			return;
		}
		
		getAllElements().stream().forEach(e -> e.selectElement(newElement));
	}
	 
}
