package tests;

import static org.junit.Assert.*;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import canvaswindow.MyCanvasWindow;
import domain.*;
import facades.Tablr;
import uielements.Checkbox;
import uielements.ListView;
import uielements.Text;
import uielements.TextField;
import uielements.UIRow;

public class TableDesignTests {		
	/**
	 * This method returns a canvas window with a loaded table without columns in tables mode.
	 * @return
	 */
//	public MyCanvasWindow prepareTable(){
//		MyCanvasWindow myCW = new MyCanvasWindow("TableDesign Mode");
//		Tablr coMan = myCW.getTablr();
//		coMan.loadTableModeUI();
//		myCW.handleMouseEvent(0, 100,100, 2);
//		return myCW;
//	}


	/**
	 * Use case 6: add column
	 */
	@Test
	public void useCase6() {
		// Load the window
		MyCanvasWindow myCW = new MyCanvasWindow("Table Design mode test");
		Tablr tablr = myCW.getTablr();
		myCW.handleKeyEvent(1, 17, Character.MIN_VALUE);
		myCW.handleKeyEvent(1, 84, Character.MIN_VALUE);
		// Double click on listview to create a new table
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 155, 152, 2);
		// The user double-clicks a table name.
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 60, 40, 2);

		
		//Step 1: The user clicks below the list of columns:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 160, 2);
		Column addedColumn = tablr.getTables().get(0).getColumns().get(0);
		assertEquals(1, tablr.getTables().get(0).getColumns().size());
		assertEquals("Column0",addedColumn.getName());
		assertEquals(Type.STRING,addedColumn.getColumnType());
		assertTrue(addedColumn.getBlankingPolicy());
		assertEquals("",addedColumn.getDefault());	
	}
	
	@Test
	public void useCase7() {
		MyCanvasWindow myCW = new MyCanvasWindow("Table Design mode test");
		Tablr tablr = myCW.getTablr();
		// Open a new tables mode subwindow
		myCW.handleKeyEvent(1, 17, Character.MIN_VALUE);
		myCW.handleKeyEvent(1, 84, Character.MIN_VALUE);
		// Double click on listview to create a new table
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 155, 152, 2);
		// The user double-clicks a table name.
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 60, 40, 2);
		
		//Adding two columns:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 490, 205, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 490, 205, 2);

		
		//Step1: The user clicks on the name of first column:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 385, 45, 1);
		
		//Step2: The user tries to change the name of the column:
		//Step2.1: The user sets a valid name for the column:
		//Clearing the name:
		for(int i=0;i<7;i++) myCW.handleKeyEvent(0,8,Character.MIN_VALUE);
		String name = "name";
		for(int i=0;i<name.length();i++){
			myCW.handleKeyEvent(0,0,name.charAt(i));
		}
		assertEquals(name,tablr.getColumnName(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		
		//Step2.2: The user enters an invalid name:
		TextField t = (TextField) tablr.getUIAt(385, 45).locatedAt(385,45);
		//Clearing the name:
		for(int i=0;i<4;i++) myCW.handleKeyEvent(0,8,Character.MIN_VALUE);
		assertTrue(t.getError());
		name = "Column1";
		for(int i=0;i<name.length();i++){
			myCW.handleKeyEvent(0,0,name.charAt(i));
		}
		assertNotEquals(name,tablr.getColumnName(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		assertTrue(t.isSelected());
		// t is in error because it has the same name as the second column
		snapShot(myCW,"pic1.png");
		assertTrue(t.getError());
		
		
		//Backspace so the name becomes valid:
		myCW.handleKeyEvent(0,8,Character.MIN_VALUE);
		assertFalse(t.getError());
		
		//The user clicks outside the textfield to finish editing the column name:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 430, 200, 1);
		assertFalse(t.isSelected());
		
		//Or the user presses enter
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 385, 45, 1);
		t =  (TextField) tablr.getUIAt(385, 45).locatedAt(385, 45);
		assertTrue(t.isSelected());
		myCW.handleKeyEvent(0,10, Character.MIN_VALUE);
		assertFalse(t.isSelected());
		
		/**
		 * Extension 1a
		 */
		//Step 1: The user clicks the type of some column:
		//The state of the column allows blanks and has a blank value as default, the type of the column is STRING:
		assertEquals(Type.STRING,tablr.getColumnType(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//The user clicks the column type:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,455,50,1);
		//The type of the column changes to EMAIL:
		assertEquals(Type.EMAIL,tablr.getColumnType(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//The user clicks the column type:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,455,50,1);
		//The type of the column changes to BOOLEAN:
		assertEquals(Type.BOOLEAN,tablr.getColumnType(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//The user clicks the column type:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,455,50,1);	
		//The type of the column changes to INTEGER:
		assertEquals(Type.INTEGER,tablr.getColumnType(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//The user clicks the column type:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,455,50,1);
		//The type of the column changes back to STRING:
		assertEquals(Type.STRING,tablr.getColumnType(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//Changing the default value for the column:
		// Click default cell of first column
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 550, 50, 1);
		String d = "default";
		for(int i=0;i<d.length();i++){
			myCW.handleKeyEvent(0,0,d.charAt(i));
		}
		myCW.handleKeyEvent(0, 10, Character.MIN_VALUE);
		//Trying to change the type of the column to email:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);
		//However, the current default value "default" is not valid for the type EMAIL:
		Text type = (Text) tablr.getUIAt(450, 50).locatedAt(450,50);
		assertTrue(type.getError());
		//The user is not able to click on anything else in the application:
		//Trying to click on the table name:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,350,50,1);
		assertFalse(t.isSelected());

		//The user clicks the type again, it changes to BOOLEAN
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);
		//However, "default" is not valid for the type BOOLEAN
		assertTrue(type.getError());
		//The user clicks the type again, it changes to INTEGER
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);
		//However, "default" is not valid for the type INTEGER
		assertTrue(type.getError());
		//The user clicks the type again, it changes to STRING again
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);
		//"default" is a valid default value for type STRING
		assertFalse(type.getError());
		//Setting default value back to blank:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 550, 50, 1);
		for (int i=0;i<"default".length();i++) 
			myCW.handleKeyEvent(0,8,Character.MIN_VALUE);
		assertEquals("",tablr.getDefaultString(tablr.getTables().get(0).getColumns().get(0)));
		myCW.handleKeyEvent(0,10,Character.MIN_VALUE);
		
		/**
		 * Extension 1b
		 */
		Checkbox box = (Checkbox) tablr.getUIAt(500, 50).locatedAt(500, 50);
		//Changing the default value to a non-blank value:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,550,50,1);
		myCW.handleKeyEvent(0,0,'d');
		assertEquals("d",tablr.getDefaultString(tablr.getTables().get(0).getColumns().get(0)));
		//Step 1: The user clicks on the checkbox indicating whether it allows blanks or not:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,500,50,1);
		//The current blanking policy is true, it is changed to false:
		assertFalse(tablr.getBlankingPolicy(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//Changing the blanking policy back to true:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,500,50,1);
		//Changing the default value to blank:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,550,50,1);
		myCW.handleKeyEvent(0,8,Character.MIN_VALUE);
		//Trying to change the blanking policy back to false:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,500,50,1);
		/*However, the current default value is blank, the checkbox becomes red and
		the user is not able to click on anything else but the checkbox*/
		box = (Checkbox) tablr.getUIAt(450, 50).locatedAt(500, 50);
		assertEquals("",((TextField) tablr.getUIAt(550,50).locatedAt(550,50)).getText());
		assertEquals("",tablr.getTables().get(0).getColumns().get(0).getDefault());
		assertTrue(box.getError());
		assertEquals(box,tablr.getLockedElement());
		//Trying to click on the second column name:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,350,80,1);
		TextField tf = (TextField) tablr.getUIAt(350, 80).locatedAt(350, 80);
		assertFalse(tf.isSelected());

		//The user clicks the checkbox again, so the blanking policy changes to true:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,500,50,1);
		assertFalse(box.getError());
		assertTrue(tablr.getBlankingPolicy(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		
		/**
		 * Extension 1c
		 */
	    //Setting the default value to "true"
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,550,50,1);
		d = "true";
		for (int i=0;i<d.length();i++) 
			myCW.handleKeyEvent(0,0,d.charAt(i));
		//Changing the type to BOOLEAN:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);	
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);
		//The type is BOOLEAN, so the default value is represented in a checkbox
		assertTrue(tablr.getUIAt(560, 50).locatedAt(560,50) instanceof Checkbox);
		assertEquals(true, tablr.getDefaultValue(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//Step1: The user clicks the default value for some column:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 560,50,1);
		//The default value was true, it becomes false:
		assertEquals(false, tablr.getDefaultValue(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//The user clicks the checkbox again, the default value becomes blank:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 560,50,1);
		assertEquals(null, tablr.getDefaultValue(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		assertEquals("", tablr.getDefaultString(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//The default value is blank and the type is boolean, the checkbox is greyed out:
		assertEquals(null, ((Checkbox) tablr.getUIAt(560, 50).locatedAt(560,50)).getValue());
		//The user clicks the checkbox again, the default value becomes true:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 560,50,1);
		assertEquals(true, tablr.getDefaultValue(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//The user clicks the checkbox, indicating the blanking policy of the column:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 500,50,1);
		assertFalse(tablr.getBlankingPolicy(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		
		//The user clicks the default value checkbox
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 560,50,1);
		//The default value was true, it becomes false:
		assertEquals(false, tablr.getDefaultValue(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//The user clicks the default value checkbox again:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 560,50,1);
		//The default value was false, it becomes true again:
		assertEquals(true, tablr.getDefaultValue(tablr.getColumns(tablr.getTables().get(0)).get(0)));

		//The user changes the type to STRING by clicking the column type twice:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);		
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);
		//The user tries to change the default value to blank by deleting all characters:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,550,50,1);
		TextField defaultString = (TextField) tablr.getUIAt(550, 50).locatedAt(550,50);
		for(int i=0;i<4;i++){
			myCW.handleKeyEvent(0,8,Character.MIN_VALUE);
		}
		assertEquals(0,defaultString.getText().length());
		//However, the column does not allow blanks:
		assertTrue(defaultString.getError());
		//Changing the default value to a non-empty value:
		myCW.handleKeyEvent(0, 0, 'd');
		//Allowing blanks again:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,500, 50, 1);
		//Deleting the current default value:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,550, 60, 1);
		myCW.handleKeyEvent(0,8,Character.MIN_VALUE);
		
		//The user changes the type of the column to EMAIL by clicking on the column type:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);
		assertEquals(Type.EMAIL,tablr.getColumnType(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		TextField defaultEmail = (TextField) tablr.getUIAt(550, 50).locatedAt(550,50);
		//The user selects the default value textfield:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 550, 50, 1);
		//The user enters a valid default value for the column:
		d = "valid.email@tests.com";
		for (int i=0;i<d.length();i++) myCW.handleKeyEvent(0, 0, d.charAt(i));
		//The default value of the column is changed successfully:
		assertEquals(d, tablr.getDefaultValue(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//Deleting the current default value:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 550, 50, 1);
		for(int i =0;i<d.length();i++) myCW.handleKeyEvent(0, 8,Character.MIN_VALUE);
		assertEquals(0,defaultEmail.getText().length());
		//The user tries to enter a non-valid default value:
		d = "wrong@Value@default.com";
		for (int i=0;i<d.length();i++) myCW.handleKeyEvent(0, 0, d.charAt(i));
		assertTrue(defaultEmail.getError());
		//Deleting the current default value:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 550, 50, 1);
		for(int i =0;i<d.length();i++) myCW.handleKeyEvent(0, 8,Character.MIN_VALUE);
		assertEquals(0,defaultEmail.getText().length());

		//The user changes the type of the column to INTEGER by clicking the column type twice:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,450,50,1);
		assertEquals(Type.INTEGER,tablr.getColumnType(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		TextField defaultInteger = (TextField) tablr.getUIAt(550, 50).locatedAt(550,50);
		//The user clicks on the textfield containing the default value:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED,550, 50,1);
		//The user enters a valid default value:
		d = "999";
		for (int i=0;i<d.length();i++) myCW.handleKeyEvent(0, 0, d.charAt(i));
		//The default value is updated successfully
		assertEquals(999, tablr.getDefaultValue(tablr.getColumns(tablr.getTables().get(0)).get(0)));
		//Clearing the textfield
		for (int i=0;i<d.length();i++) myCW.handleKeyEvent(0, 8,Character.MIN_VALUE);
		//The user tries to enter a non-valid default value:
		d = "007";
		for (int i=0;i<d.length();i++) myCW.handleKeyEvent(0, 0, d.charAt(i));
		//The textfield shows a red border around it.
		assertTrue(defaultInteger.getError());
	}
	
	/**
	 * Use case 8: Delete Column
	 */
	@Test
	public void useCase8() {
		// Load the window
		MyCanvasWindow myCW = new MyCanvasWindow("Table Design mode test");
		Tablr tablr = myCW.getTablr();
		myCW.handleKeyEvent(1, 17, Character.MIN_VALUE);
		myCW.handleKeyEvent(1, 84, Character.MIN_VALUE);
		// Double click on listview to create a new table
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 155, 152, 2);
		// The user double-clicks a table name.
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 60, 40, 2);
		// The user double clicks twice below the list of columns:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 160, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 160, 2);
		
		// The user double-clicks the table name to open rows mode
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 60, 40, 2);
		
		// The user double clicks twice below the list of rows:
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 480, 2);
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 450, 480, 2);

		//Step 1: The user clicks on a column
		myCW.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 313, 76, 1);
		UIRow firstColumn = (UIRow) tablr.getUIAt(313, 76).locatedAt(313,76);
		ListView view = (ListView) tablr.getUIAt(430, 180).locatedAt(430,180);
		//Step 2: The system indicates that the column is now selected
		assertEquals(true, firstColumn.isSelected());
		//Step 3: The user presses the delete key:
		myCW.handleKeyEvent(0, 127,Character.MIN_VALUE);

		//Step 4: The system removes the column from the list of columns...
		assertEquals(1,tablr.getColumns(tablr.getTables().get(0)).size());
		assertFalse(tablr.getColumnNames(tablr.getTables().get(0)).contains("Column1"));
		// ... and removes the value for the deleted column from all of the table's rows.
		//The domain does not store values in rows, so this is automatically true when the column is deleted.
	}
	
	
	private void snapShot(MyCanvasWindow myCW,String out) {
		File outputFile = new File(out);
		try {
			ImageIO.write(myCW.captureImage(), "png", outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
