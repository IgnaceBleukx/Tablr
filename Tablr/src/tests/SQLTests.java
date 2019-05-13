package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import domain.*;
import exceptions.InvalidNameException;
import exceptions.InvalidQueryException;

import org.junit.Test;

import sql.BooleanExpression;
import sql.CellIDExpression;
import sql.ColumnSpec;
import sql.EqualsExpression;
import sql.JoinTableSpec;
import sql.NumberExpression;
import sql.Query;
import sql.QueryExecutor;
import sql.SQLParser;
import sql.SimpleTableSpec;
import sql.StringExpression;
import sql.TableSpec;


public class SQLTests {

	@Test
	public void testJOIN() throws InvalidNameException, InvalidQueryException {

		ArrayList<Table> tables = createTables1();

		SimpleTableSpec s1 = new SimpleTableSpec("Table1", "Table1");
		SimpleTableSpec s2 = new SimpleTableSpec("Table2", "Table2");
		JoinTableSpec s = new JoinTableSpec(s1, s2, 
				new CellIDExpression("Table1", "colB"), 
				new CellIDExpression("Table2", "colC"));
		Table result = s.resolve(tables);
		result.printTable();
		
		
	}
	
	@Test
	public void testWHEREQuery() throws InvalidNameException, InvalidQueryException {
		String query = "SELECT t.colA AS a, t.colB as b FROM Table1 AS t WHERE t.a 7";
		Query q = SQLParser.parseQuery(query);
		Table result = QueryExecutor.executeQuery(q, createTables1());
		result.printTable();
		
	}
	
	@Test
	public void testWHERE() throws InvalidNameException {
		ArrayList<Table> tables = createTables1();
		Query q = new Query();
		TableSpec s = new SimpleTableSpec("Table1","Table1");
		EqualsExpression e = new EqualsExpression(new CellIDExpression("Table1","colA"),new NumberExpression(7));
		q.setExpression(e);
		q.setTableSpecs(s);
		Table newTable = q.resolveWhere(tables.get(0));
		newTable.printTable();
		assertEquals(1,newTable.getRows());
		assertEquals(7,newTable.getColumns().get(0).getValueAt(0));
		assertEquals(1,newTable.getColumns().get(1).getValueAt(0));
		//SELECT * FROM Table1 AS Table1 WHERE Table1.colA = 7
	}
	
	@Test
	public void testWHERE2() throws InvalidNameException {
		TableSpec s = new SimpleTableSpec("Table1","t");
		EqualsExpression e = new EqualsExpression(new CellIDExpression("Table1","colB"),new BooleanExpression(null));
		Query q = new Query();
		q.setTableSpecs(s);
		q.setExpression(e);
		Table newTable = q.resolveWhere(createTableMixedTypes());
		newTable.printTable();
	}
	
	@Test
	public void testSELECT() throws InvalidNameException  {
		ArrayList<Table> tables = createTables1();
		tables.get(0).getColumns().get(0).setName("Table1.colA");
		tables.get(0).getColumns().get(1).setName("Table1.colB");
		Query q = new Query();
		HashMap<String,String> aliases = new HashMap<String,String>();
		aliases.put("Table1", "Table1");
		q.addColumnSpec(new ColumnSpec(new CellIDExpression("Table1","colA"),"newColA"));
		Table newTable = q.selectColumns(tables.get(0),aliases);
		newTable.printTable();
		//[
		//	ColumnSpec(cellIDExpression(Table1.colA) AS newColA), 
		//  = SELECT Table1.colA as newColA
		//]
	}
	

	
	
	@Test
	public void testQueryNoWhere() throws InvalidNameException, InvalidQueryException {
		ArrayList<Table> tables = createTables1();
		Query q = SQLParser.parseQuery("SELECT t1.colA AS result "
		        +"FROM Table1 AS t1 INNER JOIN Table2 AS t2 "
		        +"ON t1.colB = t2.colC "
		        +"WHERE TRUE");
		Table t = QueryExecutor.executeQuery(q, tables);
		//t.printTable();
	}
	
	@Test
	public void testWhere() throws InvalidNameException, InvalidQueryException {
		ArrayList<Table> tables = createTables1();
		Query q = SQLParser.parseQuery("SELECT t1.colA AS col1, t1.colB AS col2 "
		        +"FROM Table1 AS t1 "
		        +"WHERE t1.colA = 7");
		Table t = QueryExecutor.executeQuery(q, tables);
		//t.printTable();
	}
	
	/**
	 * Creates a list containing one Table:
	 * 
	 * @return
	 * @throws InvalidNameException 
	 */
	public ArrayList<Table> createTables2() throws InvalidNameException {
		StoredTable table1 = new StoredTable("Table1");
		Column colA = new Column("colA",null,Type.INTEGER, 0);
		Column colB = new Column("colB",null,Type.INTEGER, 0);
		Column colC = new Column("colC",null,Type.INTEGER, 0);
		Column colD = new Column("colD",null,Type.INTEGER, 0);
		Column colE = new Column("colE",null,Type.INTEGER, 0);
		
		Cell<Integer> A1 = new Cell<Integer>(1);
		Cell<Integer> A2 = new Cell<Integer>(6);
		Cell<Integer> A3 = new Cell<Integer>(11);
		Cell<Integer> A4 = new Cell<Integer>(16);
		Cell<Integer> A5 = new Cell<Integer>(21);
		
		Cell<Integer> B1 = new Cell<Integer>(2);
		Cell<Integer> B2 = new Cell<Integer>(7);
		Cell<Integer> B3 = new Cell<Integer>(12);
		Cell<Integer> B4 = new Cell<Integer>(17);
		Cell<Integer> B5 = new Cell<Integer>(22);
		
		Cell<Integer> C1 = new Cell<Integer>(3);
		Cell<Integer> C2 = new Cell<Integer>(8);
		Cell<Integer> C3 = new Cell<Integer>(13);
		Cell<Integer> C4 = new Cell<Integer>(18);
		Cell<Integer> C5 = new Cell<Integer>(23);
		
		Cell<Integer> D1 = new Cell<Integer>(4);
		Cell<Integer> D2 = new Cell<Integer>(9);
		Cell<Integer> D3 = new Cell<Integer>(14);
		Cell<Integer> D4 = new Cell<Integer>(19);
		Cell<Integer> D5 = new Cell<Integer>(24);
		
		Cell<Integer> E1 = new Cell<Integer>(5);
		Cell<Integer> E2 = new Cell<Integer>(10);
		Cell<Integer> E3 = new Cell<Integer>(15);
		Cell<Integer> E4 = new Cell<Integer>(20);
		Cell<Integer> E5 = new Cell<Integer>(25);
		
		colA.addCell(A1); 
		colA.addCell(A2); 
		colA.addCell(A3); 
		colA.addCell(A4); 
		colA.addCell(A5); 
		
		colB.addCell(B1); 
		colB.addCell(B2); 
		colB.addCell(B3); 
		colB.addCell(B4); 
		colB.addCell(B5);
		
		colC.addCell(C1); 
		colC.addCell(C2); 
		colC.addCell(C3); 
		colC.addCell(C4); 
		colC.addCell(C5);
		
		colD.addCell(D1); 
		colD.addCell(D2); 
		colD.addCell(D3); 
		colD.addCell(D4); 
		colD.addCell(D5);
		
		colE.addCell(E1); 
		colE.addCell(E2); 
		colE.addCell(E3); 
		colE.addCell(E4); 
		colE.addCell(E5);
		
		ArrayList<Column> cols1 = new ArrayList<Column>();
		cols1.add(colA);
		cols1.add(colB);
		cols1.add(colC);
		cols1.add(colD);
		cols1.add(colE);
		
		table1.addAllColumns(cols1);
		
		ArrayList<Table> tables = new ArrayList<>();
		tables.add(table1);
		return tables;
	}
	
	/**
	 * Creates a list of Tables containing two Tables:
	 * Table1
	 *	colA colB 
	 *    7   1
     *    8   2
     *    9   1
     *   11   2
     *
	 * Table2
	 * colC colD 
	 *    1  -1
	 *    1  -2
	 *    2  -4
	 *    2  -5
	 * @throws InvalidNameException Will never happen with well-defined tables.
	 */
	public ArrayList<Table> createTables1() throws InvalidNameException {

		//
		//Table1
		//colA colB 
		//   7   1
		//   8   2
		//   9   1
		//  11   2

		//Table2
		//colC colD 
		//   1  -1
		//   1  -2
		//   2  -4
		//   2  -5
		StoredTable table1 = new StoredTable("Table1");
		StoredTable table2 = new StoredTable("Table2");
		
		Column colA = new Column("colA",null,Type.INTEGER, 0);
		Column colB = new Column("colB",null,Type.INTEGER, 0);
		Column colC = new Column("colC",null,Type.INTEGER, 0);
		Column colD = new Column("colD",null,Type.INTEGER, 0);
		
		Cell<Integer> CellA11 = new Cell<Integer>(7);
		Cell<Integer> CellA12 = new Cell<Integer>(1);
		Cell<Integer> CellA21 = new Cell<Integer>(8);
		Cell<Integer> CellA22 = new Cell<Integer>(2);
		Cell<Integer> CellA31 = new Cell<Integer>(9);
		Cell<Integer> CellA32 = new Cell<Integer>(1);
		Cell<Integer> CellA41 = new Cell<Integer>(11);
		Cell<Integer> CellA42 = new Cell<Integer>(2);
		
		
		Cell<Integer> CellB11 = new Cell<Integer>(1);
		Cell<Integer> CellB12 = new Cell<Integer>(-1);
		Cell<Integer> CellB21 = new Cell<Integer>(1);
		Cell<Integer> CellB22 = new Cell<Integer>(-2);
		Cell<Integer> CellB31 = new Cell<Integer>(2);
		Cell<Integer> CellB32 = new Cell<Integer>(-4);
		Cell<Integer> CellB41 = new Cell<Integer>(2);
		Cell<Integer> CellB42 = new Cell<Integer>(-5);
		
		colA.addCell(CellA11);
		colA.addCell(CellA21);
		colA.addCell(CellA31);
		colA.addCell(CellA41);
		
		colB.addCell(CellA12);
		colB.addCell(CellA22);
		colB.addCell(CellA32);
		colB.addCell(CellA42);
		
		colC.addCell(CellB11);
		colC.addCell(CellB21);
		colC.addCell(CellB31);
		colC.addCell(CellB41);
		
		colD.addCell(CellB12);
		colD.addCell(CellB22);
		colD.addCell(CellB32);
		colD.addCell(CellB42);
		
		ArrayList<Column> cols1 = new ArrayList<Column>();
		cols1.add(colA);
		cols1.add(colB);
		ArrayList<Column> cols2 = new ArrayList<Column>();
		cols2.add(colC);
		cols2.add(colD);
		
		table1.addAllColumns(cols1);
		table2.addAllColumns(cols2);
		
		ArrayList<Table> tables = new ArrayList<>();
		tables.add(table1);
		tables.add(table2);
		return tables;
	}

	/**
	 * Table:
	 * colA	 colB    colC    	 colD
	 *  1	 true  	 ""    "test@email.1.com"
	 *  null false "test2"        ""
	 *  3	 null  "test3" "test@email.3.com"
	 *	4	 null  "test4" "test@email.4.com"
	 * @throws InvalidNameException 
	 */
	public Table createTableMixedTypes() throws InvalidNameException {
		Cell<Integer> A1 = new Cell<Integer>(1);
		Cell<Integer> A2 = new Cell<Integer>(null);
		Cell<Integer> A3 = new Cell<Integer>(3);
		Cell<Integer> A4 = new Cell<Integer>(4);
		
		Column colA = new Column("colA",new ArrayList<Cell<?>>(Arrays.asList(A1,A2,A3,A4)),Type.INTEGER,0);
		
		Cell<Boolean> B1 = new Cell<Boolean>(true);
		Cell<Boolean> B2 = new Cell<Boolean>(false);
		Cell<Boolean> B3 = new Cell<Boolean>(null);
		Cell<Boolean> B4 = new Cell<Boolean>(null);
		
		Column colB = new Column("colB",new ArrayList<Cell<?>>(Arrays.asList(B1,B2,B3,B4)),Type.INTEGER,0);

		
		Cell<String> C1 = new Cell<String>("");
		Cell<String> C2 = new Cell<String>("test2");
		Cell<String> C3 = new Cell<String>("test3");
		Cell<String> C4 = new Cell<String>("test4");

		Column colC = new Column("colC",new ArrayList<Cell<?>>(Arrays.asList(C1,C2,C3,C4)),Type.INTEGER,0);

		Cell<String> D1 = new Cell<String>("test@email.1.com");
		Cell<String> D2 = new Cell<String>("");
		Cell<String> D3 = new Cell<String>("test@email.3.com");
		Cell<String> D4 = new Cell<String>("test@email.4.com");
		
		Column colD = new Column("colD",new ArrayList<Cell<?>>(Arrays.asList(D1,D2,D3,D4)),Type.INTEGER,0);

		StoredTable table = new StoredTable("Table1");
		table.addColumn(colA);
		table.addColumn(colB);
		table.addColumn(colC);
		table.addColumn(colD);
		
		table.printTable();
		
		return table;
		
	}
	
}