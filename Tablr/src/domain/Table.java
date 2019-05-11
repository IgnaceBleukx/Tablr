package domain;

import java.util.ArrayList;
import java.util.stream.Collectors;

import exceptions.InvalidNameException;


public abstract class Table {
	
	/**
	 * Creates a new empty stored Table
	 * @param name		The name of this table
	 */
	public Table(String name) {
		setName(name);
	}
	
	
	/**
	 * The table's name
	 */
	private String name;

	/**
	 * This method returns the name of the current Table.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method sets the name of the current Table.
	 * @param name 	The name to be set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	

	/**
	 * Returns the query associated with this Table.
	 * Computed Tables will always have have a valid SQL query,
	 * while Stored Tables will always have a query "".
	 */
	public abstract String getQueryString();
	
	public void addAllColumns(ArrayList<Column> c) {
		this.columns = new ArrayList<Column>(c);
	}

	
	/**
	 * The table's columns from left to right
	 */
	protected ArrayList<Column> columns = new ArrayList<Column>();
	
	/**
	 * This method returns the columns of the current Table.
	 * @return	A Collection of Columns where each column is guaranteed to have this table set as its table.
	 */
 	public ArrayList<Column> getColumns() {
		return columns;
	}

	
	/**
	 * The number of rows in this table.
	 */
	protected int nbOfRows;
	
	/**
	 * This method returns the number of rows of the current Table.
	 */
	public int getRows() {
		return nbOfRows;
		//return getColumns().isEmpty() ? nbOfRows : getColumns().get(0).getCells().size();
	}
	
	/**
	 * This method returns an ArrayList of all the names of the columns of the current Table.
	 */
	public ArrayList<String> getColumnNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Column column: this.getColumns()){
			names.add(column.getName());
		}
		return names;
	}
	
	/**
	 * This method removes a Column from the current table based on an index.
	 * @param index 	The index of the column which is to be removed.
	 * @return 			The removed Column.
	 */
	public Column removeColumn(int index){
		Column column = columns.get(index);
		columns.remove(column);
		column.setTable(null);
		return column;
	}
	
	/**
	 * This method removes a Column from the current table.
	 * @param column	Reference to the column to delete
	 */
	public void removeColumn(Column column) {
		removeColumn(columns.indexOf(column));
	}
	
	/**
	 * Returns whether this table is a Stored Table.
	 */
	public boolean isStoredTable() {
		return this.getQueryString().equals("");
	}
	
	/**
	 * Returns whether this table is a Computed Table.
	 * 
	 */
	public boolean isComputedTable() {
		return (!isStoredTable());
	}

	/**
	 * Adds a given column to the table
	 * @param column
	 */
	public void addColumn(Column column) {
		this.columns.add(column);
	}
	
	/**
	 * Clones this table
	 */
	public StoredTable clone(String newName) {
		StoredTable t = new StoredTable(newName);
		t.addAllColumns(getColumns());
		return t;
	}
	
	public ArrayList<Cell> getRowByIndex(int i,String ignoreCol) {
		return new ArrayList<Cell>(
				getColumns()
				.stream()
				.filter((c)->!c.getName().equals(ignoreCol))
				.map((c)->c.getCell(i))
				.collect(Collectors.toList()));
	}
	
	/**
	 * Prints the table in a somewhat readable form
	 */
	public void printTable() {
		System.out.println(getName());
		getColumnNames().stream().forEach((name)->System.out.print(name+" "));
		System.out.println("");
		
		for (int i=0;i<getColumns().get(0).getCells().size();i++) {
			for (int j=0;j<getColumns().size();j++) {
				String val = getColumns().get(j).getCells().get(i).getValue().toString();
				System.out.print(String.format("%4s", val));
			}
			System.out.println("");

		}
		System.out.println("");
	}
}
