package domain;

import java.util.ArrayList;
import java.util.stream.Collectors;

import exceptions.InvalidNameException;

/**
 * Class containing a Table.
 * A Table is a collection of Columns with a name
 * and comes in two variants: Stored and Computed.
 *
 */
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
	
	/**
	 * A list of tables whose queries reference this table
	 */
	private ArrayList<ComputedTable> derivedTables = new ArrayList<ComputedTable>();
	
	/**
	 * Adds a table to the list of derived tables
	 * @param t		Table
	 */
	public void addDerivedTable(ComputedTable t) {
		derivedTables.add(t);
	}
	
	/**
	 * Removes a table from the list of derived tables
	 * @param t		Table
	 */
	public void removeDerivedTable(ComputedTable t){
		derivedTables.remove(t);
	}
	
	/**
	 * Removes a list of tables from the list of derived tables.
	 * @return		List of tables
	 */
	public ArrayList<ComputedTable> removeDerivedTables() {
		ArrayList<ComputedTable> references = new ArrayList<ComputedTable>(getDerivedTables());
		for (ComputedTable ref : getDerivedTables()) {
			removeDerivedTable(ref);
		}
		return references;
	}
	
	/**
	 * Adds a list of tables to the list of derived tables.
	 * @param references		List of Tables
	 */
	public void addDerivedTables(ArrayList<ComputedTable> references) {
		derivedTables.addAll(references);
	}
	
	/**
	 * Returns a list of all tables that are derived
	 * from this Table
	 */
	public ArrayList<ComputedTable> getDerivedTables(){
		return new ArrayList<ComputedTable>(derivedTables);
	}
	
	
	/**
	 * Loads a list of Columns into this StoredTable
	 * @param c		List<> of Columns
	 */
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
		//return nbOfRows;
		return getColumns().isEmpty() ? nbOfRows : getColumns().get(0).getCells().size();
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
	 * Clones this table
	 */
	public StoredTable clone(String newName) {
		StoredTable t = new StoredTable(newName);
		t.addAllColumns(getColumns());
		return t;
	}
	
	/**
	 * Returns the Cells that occur at a certain index
	 * @param i				Index
	 * @param ignoreCol		Ignore the cells of this column
	 */
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
		getColumnNames().stream().forEach((name)->System.out.print(String.format("%15s",name+" ")));
		System.out.println("");
		
		for (int i=0;i<getColumns().get(0).getCells().size();i++) {
			for (int j=0;j<getColumns().size();j++) {
				Object val = getColumns().get(j).getCells().get(i).getValue();
				String valString = val == null ? "null" : val.toString();
				valString = valString.isEmpty() ? "BLANK" : valString;
				System.out.print(String.format("%15s", valString+" "));
			}
			System.out.println("");

		}
		System.out.println("");
	}

	/**
	 * checks if any of the tables that reference this table are referencing a certain column
	 * @param column: the column to check out
	 * @return
	 */
	public abstract boolean queryContainsColumn(Column column);

	
}
