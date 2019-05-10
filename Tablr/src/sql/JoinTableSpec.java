package sql;

import java.util.ArrayList;
import java.util.HashMap;

import Utils.DebugPrinter;
import domain.Cell;
import domain.Column;
import domain.StoredTable;
import domain.Table;
import exceptions.InvalidNameException;
import exceptions.InvalidQueryException;

public class JoinTableSpec extends TableSpec {
	public JoinTableSpec(TableSpec leftTable, SimpleTableSpec rightTable, 
						 CellIDExpression leftCell, CellIDExpression rightCell) {
		this.leftTable = leftTable;
		this.rightTable = rightTable;
		this.leftCell = leftCell;
		this.rightCell = rightCell;

	}
	
	TableSpec leftTable;
	SimpleTableSpec rightTable;

	
	CellIDExpression leftCell;
	CellIDExpression rightCell;
	
	public TableSpec getLeftTable() {
		return this.leftTable;
	}
	
	public SimpleTableSpec getRightTable() {
		return this.rightTable;
	}
	
	public CellIDExpression getLeftCell() {
		return leftCell;
	}
	
	public CellIDExpression getRightCell() {
		return rightCell;
	}
	
	public String toString() {
		return "JoinTableSpec("+leftTable.toString()+" INNER JOIN "+rightTable.toString()
				+" ON "+leftCell.toString()+" = "+rightCell.toString()+")";
	}

	/**
	 * Creates map (alias -> tableName)
	 */
	@Override
	public HashMap<String, String> findTableNameAliases() {
		HashMap<String,String> s = new HashMap<String,String>();
		
		HashMap<String,String> sLeft = leftTable.findTableNameAliases();
		HashMap<String,String> sRight = rightTable.findTableNameAliases();
		
		s.putAll(sLeft);
		s.putAll(sRight);
		
		return s;
	}

	/**
	 * Resolves this TableSpec into a Table
	 * @throws InvalidQueryException 
	 * @throws InvalidNameException 
	 */
	@Override
	public Table resolve(ArrayList<Table> tables) throws InvalidQueryException {
		Table left = leftTable.resolve(tables);
		Table right = rightTable.resolve(tables);
		
		String tableNameLeft = left.getName();
		String tableNameRight = right.getName();

		
		String columnNameLeft = leftCell.getcolumnName();
		String columnNameRight = rightCell.getcolumnName();
		
		ArrayList<Column> columnsLeft = left.getColumns();
		ArrayList<Column> columnsRight = right.getColumns();

		
		Column leftColumn = columnsLeft.
							stream().
							filter(c -> c.getName().equals(columnNameLeft)).
							findFirst().
							orElseThrow(() -> new InvalidQueryException("Unable to find column "+columnNameLeft+" in JOIN clause"));
		
		Column rightColumn = columnsRight.
				stream().
				filter(c -> c.getName().equals(columnNameRight)).
				findFirst().
				orElseThrow(() -> new InvalidQueryException("Unable to find column "+columnNameRight+" in JOIN clause"));

		
		ArrayList<RowPair> matches = new ArrayList<>();
		
		//Find rows that match, save as tuples (i1, i2)
		for (int i=0; i<leftColumn.getCells().size();i++) {
			for (int j=0; j<rightColumn.getCells().size();j++) {
				if (leftColumn.getCells().get(i).getValue().equals(rightColumn.getCells().get(j).getValue())){
					matches.add(new RowPair(i,j));
				}
				else {
					//DebugPrinter.print("No match: "+leftColumn.getCells().get(i).getValue()+" and "+rightColumn.getCells().get(j).getValue());
				}
			}
		}
		
		ArrayList<Column> newColumns = new ArrayList<Column>();
		columnsLeft.stream()
				   .forEach((c) -> createDuplicateColumn(tableNameLeft,c, newColumns));
		columnsRight.stream()
					.filter((c)->!c.getName().equals(columnNameRight))
					.forEach((c) -> createDuplicateColumn(tableNameRight,c,newColumns));
		
		StoredTable t = new StoredTable("t");
		t.addAllColumns(newColumns);
		
		//Build new table from the list of matches (i1,i2)
		for (RowPair p : matches) {
			ArrayList<Cell> leftCells = left.getRowByIndex(p.getLeftIndex(),"");
			ArrayList<Cell> rightCells = right.getRowByIndex(p.getRightIndex(),columnNameRight);
			ArrayList<Cell> newRow = new ArrayList<Cell>(leftCells);
			newRow.addAll(rightCells);
			t.addRow(newRow);
		}
		
		return t;
	}
	
	private void createDuplicateColumn(String tableName, Column c, ArrayList<Column> list) {
		try {
			list.add(new Column(tableName+"."+c.getName(),null,c.getColumnType(),c.getDefault()));
		} catch (InvalidNameException e) {
			//
		}
	}
	
}

class RowPair {
	public RowPair(int i1, int i2) {
		this.i1 = i1;
		this.i2 = i2;
	}
	int i1;
	int i2;
	
	public int getLeftIndex() {
		return i1;
	}
	
	public int getRightIndex() {
		return i2;
	}
	
}
