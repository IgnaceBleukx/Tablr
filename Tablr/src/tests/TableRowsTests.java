package tests;

import static org.junit.Assert.*;

import java.awt.event.MouseEvent;

import org.junit.Before;
import org.junit.Test;

import canvaswindow.MyCanvasWindow;
import domain.Type;
import facades.Tablr;
import uielements.Checkbox;
import uielements.TextField;
import uielements.UIRow;
import uielements.UITable;

public class TableRowsTests {
	
	public MyCanvasWindow prepareTable(){
		// Step 1: Load the tables mode window
		MyCanvasWindow myCW = new MyCanvasWindow("Table Rows Mode");
		myCW.handleKeyEvent(1, 17, ' ');
		myCW.handleKeyEvent(1, 84, ' ');

		// Double click on listview to create a new table
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 155, 152, 2);
		// The user double-clicks a table name.
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 60, 40, 2);
		// The user double clicks below the list of columns:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 170, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 170, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 170, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 170, 2);

		//Changing default value for column0.
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,540, 48, 1);
		String d1 = "default";
		for(int i=0;i<d1.length();i++){
			myCW.handleKeyEvent(0, 0, d1.charAt(i));
		}
		//Changing default value for column1:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 545, 75, 1);
		String d2 = "default@email";
		for(int i=0;i<d2.length();i++){
			myCW.handleKeyEvent(0, 0, d2.charAt(i));
		}
		//Changing type to Boolean for column2:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,105,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,105,1);
		
		//Changing type to integer for column3:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,135,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,135,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,135,1);
		//Changing default value for column3 to 999:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 550,132,1);
		String d3 = "999";
		for(int i=0;i<d3.length();i++){
			myCW.handleKeyEvent(0, 0, d3.charAt(i));
		}
		
		// The user double-clicks the table name to open rows mode
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 60, 37, 2);
		
		// The user double clicks below the list of rows:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 480, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 480, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 480, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 480, 2);

		return myCW;
	}
	
	/**
	 * Use case 8: Add a row
	 * 
	 */
	@Test
	public void useCase8(){
		MyCanvasWindow myCW = prepareTable();
		Tablr tablr = myCW.getTablr();
	
		// Step 2: The system added four new rows in prepareTable(). Its value for each column is the columns default.
		assertEquals("default",tablr.getColumns(tablr.getTables().get(0)).get(0).getDefault());
		assertEquals("default@email",tablr.getColumns(tablr.getTables().get(0)).get(1).getDefault());
		assertNull(tablr.getColumns(tablr.getTables().get(0)).get(2).getDefault());
		assertEquals(999,tablr.getColumns(tablr.getTables().get(0)).get(3).getDefault());
	}

	/**
	 * use Case 4.9: Edit row value
	 */
	@Test
	public void useCase9() {
		// Step 1: Load the window
		MyCanvasWindow myCW = new MyCanvasWindow("Table Rows Mode");
		myCW.handleKeyEvent(1, 17, ' ');
		myCW.handleKeyEvent(1, 84, ' ');

		// Double click on listview to create a new table
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 155, 152, 2);
		// The user double-clicks a table name.
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 60, 40, 2);
		// The user double clicks below the list of columns:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 276, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 276, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 276, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 276, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 276, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 276, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 276, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 276, 2);
		
		//Changing default value for column0.
		// String met default = "default" en Blanks_al = true
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,540,50, 1);
		String d1 = "default";
		for(int i=0;i<d1.length();i++){
			myCW.handleKeyEvent(0, 0, d1.charAt(i));
		}
		//Changing default value for column1.
		// String met default = "default" en Blanks_al = false
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,540,75, 1);
		for(int i=0;i<d1.length();i++){
			myCW.handleKeyEvent(0, 0, d1.charAt(i));
		}		
		//Toggle Blanks
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,500,75, 1);
		
		//Changing default value for column2:
		// Email met default = "default@email" en Blanks_al = true
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 105, 1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 540, 100, 1);
		String d2 = "default@email";
		for(int i=0;i<d2.length();i++){
			myCW.handleKeyEvent(0, 0, d2.charAt(i));
		}
		
		//Changing default value for column3:
		// Email met default = "default@email" en Blanks_al = false
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 135, 1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 540, 135, 1);
		for(int i=0;i<d2.length();i++){
			myCW.handleKeyEvent(0, 0, d2.charAt(i));
		}
		//Toggle Blanks
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 500, 135, 1);
		
		//Changing default value for column4:
		// Boolean: default = Blank  && Blanks_al = true
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,165,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,165,1);
		

		//Changing default value for column5:
		// Boolean: default = true && Blanks_al = false
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,195,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,195,1);
		//set default
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 560,195,1);
		//Toggle Blanks
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 504, 195, 1);
		
		
		//Changing default value for column6:
		// Integer: default = 999  && Blanks_al = true
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,225,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,225,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,225,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 540,225, 1);
		String d4 = "999";
		for(int i=0;i<d4.length();i++){
			myCW.handleKeyEvent(0, 0, d4.charAt(i));
		}
		
		//Changing default value for column7:
		// Integer met default = 999  en Blanks_al = false
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,250,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,250,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450,250,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 540,250, 1);
		for(int i=0;i<d4.length();i++){
			myCW.handleKeyEvent(0, 0, d4.charAt(i));
		}
		//Toggle Blanks
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 505, 250, 1);
		
		// got to ROWS mode
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 60, 35, 2);
				
		// Add a row
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 480, 550 , 2);
		
		// Step 1: Click value of first row for first column
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 40, 70 , 1);

		// Step 2: The system indicates that this row is now selected.
		//Column 0
		TextField textField = (TextField) tablr.getActiveUI().locatedAt(40, 70);
		assertTrue(textField.isSelected());
		for(int i=0;i<d1.length();i++){
			myCW.handleKeyEvent(0, 8, ' ');
		}
		// No error when the field is blank
		assertEquals(false, textField.getError());
		
		//Column 1
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 100, 70 , 1);
		textField = (TextField) tablr.getActiveUI().locatedAt(100, 70);
		assertTrue(textField.isSelected());
		for(int i=0;i<d1.length();i++){
			myCW.handleKeyEvent(0, 8, ' ');
		}
		// Error when the field is blank
		assertEquals(true, textField.getError());
		// Add char to prevent lock
		myCW.handleKeyEvent(0, 0, '5');
		
		
		// Column 2: @ verwijderen
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 180, 70 , 1);
		textField = (TextField) tablr.getActiveUI().locatedAt(180, 70);
		for(int i=0;i<6;i++){
			myCW.handleKeyEvent(0, 8, ' ');
			System.out.println(textField.getText());
		}
		// Error when the field is blank
		assertTrue(textField.getError());
		// Add @ to prevent lock
		myCW.handleKeyEvent(0, 0, '@');
		
		
		// Column 3: @ verwijderen
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 250, 70 , 1);
		textField = (TextField) tablr.getActiveUI().locatedAt(250, 70);
		for(int i=0;i<d2.length();i++){
			myCW.handleKeyEvent(0, 8, ' ');
		}
		// Error when the field is blank
		assertTrue(textField.getError());
		// Add @ to prevent lock
		myCW.handleKeyEvent(0, 0, '@');
		
		
		// Column 4: 
		assertTrue(((Checkbox) tablr.getActiveUI().locatedAt(330, 70)).getGreyedOut());
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 330, 70 , 1);
		assertFalse(((Checkbox) tablr.getActiveUI().locatedAt(330, 70)).getGreyedOut());
		assertTrue(((Checkbox) tablr.getActiveUI().locatedAt(330, 70)).isChecked());
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 330, 70, 1);
		assertFalse(((Checkbox) tablr.getActiveUI().locatedAt(330, 70)).isChecked());
		
		
		// Click on checkbox of column 5 
		assertFalse(((Checkbox) tablr.getActiveUI().locatedAt(400, 70)).getGreyedOut());
		assertTrue(((Checkbox) tablr.getActiveUI().locatedAt(400, 70)).isChecked());
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 400, 70, 1);
		assertFalse(((Checkbox) tablr.getActiveUI().locatedAt(400, 70)).getGreyedOut());
		assertFalse(((Checkbox) tablr.getActiveUI().locatedAt(400, 70)).isChecked());
		
		
		
		// Column 6: Remove all digits
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 70 , 1);
		textField = (TextField) tablr.getActiveUI().locatedAt(450, 70);
		assertTrue(textField.isSelected());
		for(int i=0;i<d4.length();i++){
			myCW.handleKeyEvent(0, 8, ' ');
		}
		// No error when the field is blank
		assertEquals(false, textField.getError());
		
		
		// Column 7: Remove all the digits
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 510, 70 , 1);
		textField = (TextField) tablr.getActiveUI().locatedAt(510, 70);
		assertTrue(textField.isSelected());
		for(int i=0;i<d4.length();i++){
			myCW.handleKeyEvent(0, 8, ' ');
		}
		// Error when the field is blank
		assertEquals(true, textField.getError());
		// Add char to prevent lock
		myCW.handleKeyEvent(0, 0, '5');
	}
	
	/**
	 * use case 4.10: Delete Row 
	 */
	@Test
	public void useCase10() {
//		// Step 1: Load the window
//		MyCanvasWindow myCW = new MyCanvasWindow("Tables Mode");
//		CommunicationManager tablr = myCW.getCommunicationManager();
//		tablr.clearUI();
//		tablr.loadUI(Loadable_Interfaces.TABLES);
//		// The user double-clicks below the list of tables to create a table
//		myCW.handleMouseEvent(0, 40, 530 , 2);
//		
//		//The user double-clicks a table name.
//		myCW.handleMouseEvent(0, 51, 13, 2);
//		
//		// The user double-clicks to create a column
//		myCW.handleMouseEvent(0, 40, 530 , 2);
//		
//		// go back to TABLES_MODE
//		myCW.handleKeyEvent(0, 27, ' ');
		
		MyCanvasWindow myCW = prepareTable();
		Tablr tablr = myCW.getTablr();
		
		// Step 1: The user double-clicks the table name.
		myCW.handleMouseEvent(0, 51, 13, 2);
		
//TODO		assertEquals(Loadable_Interfaces.TABLE_ROWS, tablr.getMode());
		
		// Add a row
		myCW.handleMouseEvent(0, 11, 31 , 2);
		
		// Step 1: The user clicks the margin to the left of first row.
		myCW.handleMouseEvent(0, 11, 61 , 1);
		
		// Step 2: The system indicates that this row is now selected.
		UIRow r = (UIRow) tablr.getActiveUI().locatedAt(11, 61);
		UITable table = (UITable) tablr.getActiveUI().locatedAt(200, 200);
		assertEquals(table.getSelected(), r);

		// Step 3: User presses Delete key
		myCW.handleKeyEvent(0, 127, ' ');
		
		// Step 4: The system removes the row from the table and shows the updated list of rows
		assertEquals(0,tablr.getTables().get(0).getRows().size());
		assertEquals(0,table.getRows().size());
	}
	
}

