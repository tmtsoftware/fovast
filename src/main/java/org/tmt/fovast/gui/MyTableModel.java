package org.tmt.fovast.gui;

/**
 *
 */
import java.util.ArrayList;
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
    ArrayList<Object[]> data = new ArrayList<Object[]>();
    int _flag = 0;
    StarTable table;
    
    public MyTableModel(StarTable table) {
        this.table=table;
    }

//    public MyTableModel(String[][] rowData , String[] colNames) {
//        for(int i=0;i<rowData.length;i++){
//            this.data.add(rowData[i]);
//        }
//        this.columnNames = colNames;
//    }

    // Set the column names.
    public void setColNames(String[] colNames) {
        columnNames = new String[colNames.length];
        for (int i = 0; i < colNames.length; i++) {

            columnNames[i] = colNames[i];
        }
    }

    // Return column count.
    public int getColumnCount() {
        if(columnNames != null)
            return columnNames.length;
        else
            return 0;
    }

    // Return row count
    public int getRowCount() {
        return data.size();
    }

    // Return column name of the specified column
    public String getColumnName(int col) {
        return columnNames[col];
    }

    // Return the value of a particular cell
    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }

    // Return the datatype of particular column.
//    public Class getColumnClass(int c) {
////        if (_flag == 1) {
////            if (c == 0 || c == 3 || c == 4 || c == 5) {
////                return Integer.class;
////            }
////        }
//
////        return String.class;
//        return table.getColumnInfo(c).getContentClass();
//    }

    // We want a editable table
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    // Initiallize the data array
    public void initiallize(int numRows, int numCols) {
        //this.numCols = numCols;
        data = new ArrayList<Object[]>();
        for (int i = 0; i < data.size(); i++)
        data.add(new Object[numCols]);
    }

    public void setValueAt(Object value, int row, int col) {
        
        if(row >= data.size()) {
            for( int i = data.size();i<=row ;i++){
                data.add(new Object[getColumnCount()]);
            }
        }
        Object[] rowObj = data.get(row);
        rowObj[col] = value;
        fireTableCellUpdated(row, col);
    }

    void clear() {
        columnNames = null;
        data = new ArrayList<Object[]>();
        _flag = 0;
        table = null;
    }
}
