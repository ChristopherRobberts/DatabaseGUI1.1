import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Created by Chris on 2018-02-23.
 */
public class Util {

    /*Add content queried to database to the given table.*/
    public void addToTable(ArrayList<ArrayList<String>> content, String [] columns, JTable table) {
        String[][] bar = new String[content.size()][];

        /*Override function isCellEditable and make it always return false for every table model created.*/
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };
        for (int i = 0; i < content.size(); i++) {
            ArrayList<String> row = content.get(i);
            bar[i] = row.toArray(new String[content.get(i).size()]);
            model.addRow(bar[i]);
        }
        table.setModel(model);
    }

    /*Add content queried to database to the given combo box.*/
    public void addToComboBox(ArrayList<String> alternatives, JComboBox comboBox) {
        for (int i = 0; i < alternatives.size(); i++) {
            comboBox.addItem(alternatives.get(i));
        }
    }
}
