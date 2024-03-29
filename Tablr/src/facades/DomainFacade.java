package facades;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import Utils.DebugPrinter;
import domain.Column;
import domain.ComputedTable;
import domain.StoredTable;
import domain.Table;
import domain.Type;
import exceptions.BlankException;
import exceptions.InvalidNameException;
import exceptions.InvalidTypeException;
import sql.ColumnSpec;
import sql.Query;
import sql.QueryExecutor;
import sql.SQLParser;
import tests.SQLTests;
import ui.FormsModeUI;
import ui.TableDesignModeUI;
import ui.TableRowsModeUI;
import ui.UI;

/**
 * Facade for the Domain part of the program. 
 * Methods should always be called via a reference to Tablr,
 * and not directly via this class.
 */
public class DomainFacade {
		
	/**
	 * Tables
	 */
	private ArrayList<Table> tables = new ArrayList<>();

	/**
	 * The active table is the table that is used when in
	 * TABLE_DESIGN or TABLE_ROWS mode. UI Elements need to
	 * manually set a new table as active before switching modes.
	 * While in TABLES mode, the variable activeTable may be set
	 * but is not used.
	 */
	private Table activeTable;

	/**
	 * Adds a new table to the list of tables
	 * @param table		Table to add
	 */
	public void addTable(Table table) {
		execute(new Command() {			
			public void execute() { 
				tables.add(table);	
				DebugPrinter.print(table);
				domainChangedListener.accept(table);
			}
	
			public void undo() { 
				tables.remove(table);
				domainChangedListener.accept(null);
			}
			public String toString() {
				return "AddTable";
			}
		});
	}
	
	/**
	 * This method adds a table to the current list of tables at a given index.
	 * @param table 	The table to be added to the list of tables.
	 * @param index 	The index on which the table must be added.
	 */
	void addTableAt(Table table, int index) {
			tables.add(index, table);
			DebugPrinter.print(table); 
	}

	/**
	 * Adds an empty table to the list of tables
	 */
	public StoredTable addEmptyTable() {
		String name = nextTableName();
		StoredTable table = new StoredTable(name);
		
		execute(new Command() {
			public void execute() { 		
				tables.add(table);
				domainChangedListener.accept(table);
			}
	
			public void undo() { 
				tables.remove(table);
				domainChangedListener.accept(null);
			}
			public String toString() {
				return "AddEmptyTable";
			}
		});
		return table;
	}

	/**
	 * Remove tables from the list of tables
	 * @param table		Table to remove
	 */
	public void removeTable(StoredTable table) {
		int index = tables.indexOf(table);
		ArrayList<ComputedTable> cTables = new ArrayList<ComputedTable>(table.getDerivedTables());
		execute(new Command() {
			public void execute() {
				DebugPrinter.print(table);
				cTables.stream().forEach(t -> tables.remove(t));
				DebugPrinter.print(table);
				tables.remove(table);
				domainChangedListener.accept(table);
			}
			public void undo() { 
				addTableAt(table,index);
				cTables.stream().forEach(t -> tables.add(t));
				domainChangedListener.accept(table);
			}
		});
		
	}
	
	/**
	 * Remove tables from the list of tables
	 * @param int		Index of table to remove
	 */
	public void removeTable(int index) {
		Table table = tables.get(index);
		if (table.isComputedTable()) {
			removeTable((ComputedTable) table);
		}
		else if (table.isStoredTable()) {
			removeTable((StoredTable) table);
		}
	}

	/**
	 * Remove computed tables from the list of tables (extra functionality of references)
	 * @param table		Table to remove
	 */
	public void removeTable(ComputedTable table) {
		int index = tables.indexOf(table);
		execute(new Command() {
			public void execute() { 		
				tables.remove(table);
				for (int i = 0; i < getTablesPure().size(); i++) {
					tables.get(i).removeDerivedTable(table);
				}
				domainChangedListener.accept(table);
			}
			public void undo() { 
				addTableAt(table,index);
				addReferenceTables(table);
				domainChangedListener.accept(table);
			}
		});
		
	}

	/**
	 * Renames a table.
	 * @param t			Table
	 * @param newName	New name
	 * @throws InvalidNameException 
	 */
	public void renameTable(Table t, String newName) {
		String currentName = t.getName();
		execute(new Command() {
			public void execute() { 		
				t.setName(newName);	;
			}
			public void undo() { 
				t.setName(currentName);
			}
		});
	}

	/**
	 * Returns a list of all table names
	 */
	private ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(Table t : getTables()){
			names.add(t.getName());
		}
		return names;
	}

	/**
	 * Returns the next logical name for a table.
	 * Logical names are 'TableX' where X is the smallest integer
	 * not yet used as a table name.
	 */
	private String nextTableName(){
		String name = "Table";
		int i = 0;
		while(name == "Table"){
			if (!getNames().contains(name+i)){
				name = "Table"+i;
			}
			else{
				i++;
			}
		}
		return name;
	}

	/**
	 * Returns a clone of the tables
	 */
	public ArrayList<Table> getTables() {
		return new ArrayList<Table>(tables);
	}

	/**
	 * Returns the tables (no clone)
	 */
	public ArrayList<Table> getTablesPure() {
		return tables;
	}

	public String getTableQuery(Table table) {
		return table.getQueryString();
	}

	public Table getActiveTable() {
		return this.activeTable;
	}

	/**
	 * Sets the active table to some table t
	 */
	public void setActiveTable(Table t) {
		this.activeTable = t;
	}

	/**
	 * Adds an empty column to a table
	 * @param table				Table
	 * @param type				Type of the new column
	 * @param defaultValue		The default value of the new column 
	 */
	public void addEmptyColumn(StoredTable table, Type type, Object defaultValue) {	
		execute(new Command() {
			Column column = null;
			public void execute() { column = table.addEmptyColumn(type, defaultValue); 
			domainChangedListener.accept(table);}
			public void undo() { table.removeColumn(column); 
			domainChangedListener.accept(table);}
		});
	}

	/**
	 * Returns a list of all columns in a table
	 * @param tab		Tablr
	 */
	public ArrayList<Column> getColumns(Table tab) {
		return tab.getColumns();
	}

	/**
	 * Returns a list of all column names in a table.
	 * @param table		Table
	 */
	public ArrayList<String> getColumnNames(Table table) {
		return table.getColumnNames();
	}

	/**
	 * Remove a column specified by its index from a table
	 * @param tab		Table
	 * @param index		Index
	 * @throws InvalidNameException 
	 */
	public ArrayList<ComputedTable> removeColumn(StoredTable table, int index) {
		DebugPrinter.print("Removing column");
		ArrayList<ComputedTable> cTables = new ArrayList<ComputedTable>();
		Column column = table.getColumns().get(index);
		getTables().stream().filter(t -> t.isComputedTable()).forEach(t -> {
			if (t.queryContainsColumn(column)){
				DebugPrinter.print("Query contains column");
				cTables.add((ComputedTable) t);
			}
		});
		execute(new Command(){
			Column column = getColumns(table).get(index);
			public void execute() { 
				table.removeColumn(index);
				cTables.stream().forEach(t -> {
					getTablesPure().remove(t);
					for (int i = 0; i < getTablesPure().size(); i++) {
						getTablesPure().get(i).removeDerivedTable(t);
					}
				}); 
				domainChangedListener.accept(table);
			}
			
			public void undo() { 
				table.addColumnAt(column, index);
				cTables.stream().forEach(t -> {
					tables.add(t);
					addReferenceTables(t);
				}); 
				domainChangedListener.accept(table);
			}	
		});
		return cTables;
	}

	/**
	 * Sets the name of a column.
	 * @param col						Column
	 * @param text						Name
	 * @throws InvalidNameException		When this name is already in use in the relevant table.
	 */
	public void setColumnName(Column col, String text) throws InvalidNameException {
		execute(new Command(){
			String curText = col.getName();
			public void execute() {
				col.setName(text);
			}
			public void undo() {
				col.setName(curText);
			}
		});
	}

	/**
	 * Returns the type of this column
	 */
	public Type getColumnType(Column col) {
		return col.getColumnType();
		
	}

	/**
	 * Changes the type of a column to its next value.
	 * @param col						Column
	 * @throws InvalidTypeException		When changing the type results in invalid value of cells within the column.
	 */
	public void toggleColumnType(Column col) {
		execute(new Command() {
			public void execute() { 
				col.setNextType(); 
				domainChangedListener.accept(col.getTable());
			}
			public void undo() { 
				col.setPreviousType(); 
				domainChangedListener.accept(col.getTable());
			}
		});
	}

	/**
	 * Whether this column allows blank values
	 * @param col	Column
	 */
	public boolean getBlankingPolicy(Column col) {
		return col.getBlankingPolicy();
	}

	/**
	 * Toggles allowing blank values in a column
	 * @param col			The column
	 * @throws Exception	When trying to disallow blanks while the default value is blank
	 */
	public void toggleBlanks(Column col) throws BlankException {
		execute(new Command() {
			public void execute() { 		
				col.toggleBlanks();
				domainChangedListener.accept(col.getTable());
			}			
			public void undo() { 
				try {
					col.toggleBlanks();
				}catch(BlankException e) {
					DebugPrinter.print("BlankException");
					undoStack.remove(this);
					nbCommandsUndone--;
				}
				domainChangedListener.accept(col.getTable());
			}
		});
	}
	
	/**
	 * Sets the type of this column
	 * @param col						Column
	 * @param type						New Type
	 * @throws InvalidTypeException		Changing the type bring the column into an invalid state
	 */
	public void setColumnType(Column col, Type type){
		execute(new Command() {
			Type oldType = col.getColumnType();
			public void execute() { 
				col.setColumnType(type); 
				domainChangedListener.accept(col.getTable());
			}
			public void undo() { 
				col.setColumnType(oldType);
				domainChangedListener.accept(col.getTable());
			}
		});
		}

	/**
	 * Returns the default value of this column as a string
	 * @param col	Column
	 */
	public String getDefaultString(Column col) {
		Object v = col.getDefault();
		return v == null ? "" : v.toString();
	}

	/**
	 * Returns the default value of this column as an Object
	 * @param col	Column
	 */
	public Object getDefaultValue(Column col) {
		return col.getDefault();
	}

	/**
	 * Sets the default value for a column
	 * @param col
	 * @param def
	 * @throws ClassCastException
	 */
	public void setDefault(Column col, String def) throws ClassCastException {
		execute(new Command() {
				String prev = col.getDefault() == null ? null : col.getDefault().toString();	
				public void execute() { col.setDefaultValue(col.getColumnType().parseValue(def)); }
				public void undo() { col.setDefaultValue(col.getColumnType().parseValue(prev));}
			});
		}

	/**
	 * Toggles the default value for boolean columns
	 * @param col	Column
	 */
	public void toggleDefault(Column col) {
		execute(new Command() {
				public void execute() { col.toggleDefaultBoolean();  
				domainChangedListener.accept(col.getTable());}
				public void undo() { col.togglePreviousDefaultBoolean();  
				domainChangedListener.accept(col.getTable());}
			});
		}

	/**
	 * Adds an empty row to a table
	 * @param table		Table 
	 */
	public void addRow(StoredTable table) {
		execute(new Command() {
			int index = table.getRows();
			public void execute() { table.addRow();  
			domainChangedListener.accept(table);}
			public void undo() { table.removeRow(index); 
			domainChangedListener.accept(table);}
		});
	}
	
	private ArrayList<Object> getRowValues(Table table, int index){
		return new ArrayList<Object>(table.getRowByIndex(index, "").stream().map(c -> c.getValue()).collect(Collectors.toList()));
	}
	
	/**
	 * Returns the number of rows in a table
	 * @param tab		Table
	 */
	public int getRows(Table tab) {
		return tab.getRows();
	}

	/**
	 * Remove a row specified by its index from a table
	 * @param table		Table
	 * @param index		Index
	 */
	public void removeRow(StoredTable table, int index) {
		ArrayList<Object> rowValues = getRowValues(table,index);
		execute(new Command(){
			public void execute() {
				table.removeRow(index); 
				domainChangedListener.accept(table);
			}
			public void undo() {
				table.addFilledRowAt(rowValues,index); 
				domainChangedListener.accept(table);
			}			
		});
	}

	/**
	 * Returns the value of a cell in a column
	 * @param col		The Column
	 * @param index		Index of the cell
	 */
	public Object getValue(Column col, int index) {
		return col.getCell(index).getValue();
		
	}

	/**
	 * Gets the value of a cell specified by its index, as a string.
	 * @param col		Column
	 * @param i			Index of the cell
	 * @return			String representation of the column's value
	 */
	public String getValueString(Column col, int i) {
		Object v = col.getCell(i).getValue();
		return v == null ? "" : v.toString();
	}
	
	/**
	 * changes the value of a cell in a column and returns the table it changed
	 * @param col					Column
	 * @param i						Index
	 * @param string				New Value
	 * @return 
	 * @throws ClassCastException	The new value is not valid for the column's type
	 */
	public void changeCellValue(Column col, int i, String string) throws ClassCastException {
		execute(new Command() {
			String prevValue;
			public void execute() { 
				prevValue = col.getCell(i).getValueString(); 
				col.changeCellValue(i,string); 
			}
			public void undo() { 
				col.changeCellValue(i, prevValue); 
			}
		});
	}
	
	/**
	 * Returns the name of a column
	 */
	public String getColumnName(Column col) {
		return col.getName();
	}
	
	/**
	 * Toggles the value of a boolean cell, accounting for its current value and column's blanking policy
	 * @param col		Column	
	 * @param i			Index of the cell
	 */
	public void toggleCellValueBoolean(Column col, int i) {
		try {
			execute(new Command() {
				public void execute() { col.toggleCellValueBoolean(i); 
				domainChangedListener.accept(col.getTable());
				DebugPrinter.print("toggle value boolean"); }
				public void undo() {  col.togglePreviousCellValueBoolean(i);  
				domainChangedListener.accept(col.getTable());}
			});
		} catch (InvalidNameException e) {
			throw new RuntimeException("InvalidNameException while toggeling cell value boolean");
		}
	}
	
	
	
	/**
	 * Interface of a Command.
	 * A Command can be undone and redone.
	 */
	public interface Command {
		void execute();
		void undo();
	}
	
	/**
	 * Undo Stack: list of Commands in order
	 * to undo them.
	 */
	private ArrayList<Command> undoStack = new ArrayList<>();
	
	/**
	 * Number of Commands that have been undone.
	 */
	int nbCommandsUndone = 0;

	/**
	 * Actions that occur when a certain
	 * Table has changed.
	 */
	private Consumer<Table> domainChangedListener;
	
	/**
	 * Undoes the last Command executed.
	 */
	void undo(){
		DebugPrinter.print(undoStack);
		DebugPrinter.print(nbCommandsUndone);
		if(undoStack.size() > nbCommandsUndone) {
			undoStack.get(undoStack.size() - ++nbCommandsUndone).undo();
		}
	}
	
	/**
	 * Execute the last Command that was undone.
	 */
	void redo(){
		DebugPrinter.print(undoStack);
		DebugPrinter.print(nbCommandsUndone);
		if(nbCommandsUndone > 0)
			undoStack.get(undoStack.size() - nbCommandsUndone--).execute();
	}
	
	
	/**
	 * Executes a command
	 * @param command		Command to execute
	 */
	void execute(Command command){
		for(; nbCommandsUndone > 0; nbCommandsUndone--)
			undoStack.remove(undoStack.size() - 1);
		undoStack.add(command);
		command.execute();
	}
	
	/**
	 * Removes a Command from the Undo stack.
	 */
	public void removeLastCommand() {
		undoStack.remove(undoStack.size()-1);
	}
	
	/**
	 * a method to replace a table with another one: is used to update computed tables when their information from their query changes, or to change from a stored to a computed table.
	 * @param index
	 * @param newTable
	 */
	void replaceTable(int index, ComputedTable newTable) {
		Table oldTable = getTables().get(index);
		ArrayList<ComputedTable> oldDerivedTables = oldTable.getDerivedTables();
		ArrayList<Table> oldTables = getTables();
		if(oldTable.isStoredTable()) {	//We do make a command when replacing a StoredTable with a ComputedTable, but when updating a ComputedTable we don't need that.
			execute(new Command(){
				@Override
				public void execute() {
					oldDerivedTables.stream().forEach(t -> tables.remove(t));
					tables.remove(index);
					tables.add(oldTables.indexOf(oldTable), newTable);
					System.out.println("BOOOOOM");
					domainChangedListener.accept(oldTable);
					addReferenceTables(newTable); //adds this table as a referencing table to the tables it requires
					}
				
				@Override
				public void undo() {
					tables.stream().forEach(t -> t.removeDerivedTable(newTable));
					tables.remove(index);
					tables.add(oldTables.indexOf(oldTable), oldTable);
					oldDerivedTables.stream().forEach(t -> tables.add(t));
					DebugPrinter.print(oldDerivedTables);
					domainChangedListener.accept(newTable);
				}
			});
		}
		else {
			tables.remove(index);
			tables.stream().forEach(t -> t.removeDerivedTable((ComputedTable) oldTable));
			tables.add(oldTables.indexOf(oldTable), newTable);
			addReferenceTables(newTable);
			domainChangedListener.accept(newTable);
		}
	}
	
	/**
	 * a method to check if a table t still exists in the domain
	 * @param t
	 * @return
	 */
	public boolean isDeleted(Table t) {
		if(!tables.contains(t)) return true;
		else return false;
	}

	/**
	 * every table that is referenced by the query of a computed table gets a reference to that computed table.
	 * @param ct: the computed table in question
	 */
	public void addReferenceTables(ComputedTable ct) {
		Query q = ct.getQuery();
		ArrayList<String> nameList = q.tableNames();
		for (int i = 0; i < tables.size(); i++) {
			Table table = tables.get(i);
			if(nameList.contains(table.getName())) {
				table.addDerivedTable(ct);
			}
		}
	}
	
	/**
	 * Loads a list of Sample tables and
	 * returns them
	 */
	public ArrayList<Table> loadSampleTables() {
		ArrayList<Table> newTables = new ArrayList<>();
		SQLTests t =  new SQLTests();
		newTables.addAll(t.createExampleTablesMovie());
		newTables.addAll(t.createExampleTablesStudents());
		newTables.addAll(t.createTables1());
		newTables.addAll(t.createTables2());
		newTables.add(t.createTableMixedTypes());
		
		newTables.get(0).setName("Movies");
		newTables.get(1).setName("Students");
		newTables.get(2).setName("Enrollments");
		newTables.get(3).setName("IntTable1");
		newTables.get(4).setName("IntTable2");
		newTables.get(5).setName("IntTable3");
		newTables.get(6).setName("MixedTypes");
		this.tables = newTables;
		return newTables;
	}

	public void setDomainChangedListener(Consumer<Table> r) {
		this.domainChangedListener = r;
	}

}
