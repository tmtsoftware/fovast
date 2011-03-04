package org.tmt.fovast.gui;

/**
 *
 * @author Disha_Gujrathi
 */
import javax.swing.table.AbstractTableModel;
import uk.ac.starlink.table.StarTable;

/**
 * This class creates a table model before creating the tables
 * for displaying the Cone Search info
 *
 *
 */
public class MyTableModel extends AbstractTableModel {

    String[] columnNames = null;
    Object[][] data = null;
    int _flag = 0;
    StarTable table;
    public MyTableModel(StarTable table) {
        this.table=table;
    }



    // Set the column names.
    public void setColNames(String[] colNames) {
        columnNames = new String[colNames.length];
        for (int i = 0; i < colNames.length; i++) {
            columnNames[i] = colNames[i];
        }
    }

    // Return column count.
    public int getColumnCount() {
        return columnNames.length;
    }

    // Return row count
    public int getRowCount() {
        return data.length;
    }

    // Return column name of the specified column
    public String getColumnName(int col) {
        return columnNames[col];
    }

    // Return the value of a particular cell
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    // Return the datatype of particular column.
    public Class getColumnClass(int c) {
//        if (_flag == 1) {
//            if (c == 0 || c == 3 || c == 4 || c == 5) {
//                return Integer.class;
//            }
//        }

//        return String.class;
        return table.getColumnInfo(c).getContentClass();
    }

    // We want a editable table
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    // Initiallize the data array
    public void initiallize(int numRows, int numCols) {
        data = new Object[numRows][];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Object[numCols];
        }
    }

    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}
