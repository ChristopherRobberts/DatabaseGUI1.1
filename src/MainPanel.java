import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.ArrayList;

public class MainPanel {
    private JPanel mainPanel;
    private DBConnection conn = new DBConnection();
    private Util util;
    private JTable availableGames;
    private JTable storeStock;
    private JComboBox platformCombo;
    private JComboBox genreCombo;
    private JList list1;
    private String platformParam;
    private String genreParam;
    private int clickedIndex;

    /*Our main window for the application*/
    private MainPanel() {
        conn.connectDataBase();
        ArrayList<String> genres = conn.getGenre();
        ArrayList<String> platform = conn.getPlatform();
        util = new Util();
        //TODO(Chris): discuss with Peter over front page content.
        String[] columnNames = {"Available Games"};

        /*Add the different alternatives for the dropdown windows for products and genres.*/
        util.addToComboBox(genres, this.genreCombo);
        util.addToComboBox(platform, this.platformCombo);
        platformParam = "";
        genreParam = "";

        /*Add listener to the combo box alternative for genres, update table content when combo box
        * alternative is updated.*/
        genreCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genreParam = genreCombo.getSelectedItem().toString();
                util.addToTable(conn.productsWithGenreOrPlatform(platformParam, genreParam), columnNames, availableGames);
            }
        });

        /*Add listener to the combo box alternative for platforms, update table content when
        combo box alternative is updated.*/
        platformCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                platformParam = platformCombo.getSelectedItem().toString();
                util.addToTable(conn.productsWithGenreOrPlatform(platformParam, genreParam), columnNames, availableGames);
            }
        });
        util.addToTable(conn.productsWithGenreOrPlatform(platformParam, genreParam), columnNames, availableGames);

        /*Add listener to table values, allowing user to gain access to store locations with the
        * chosen game in storage. Create table and show table with this information.*/
        availableGames.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && availableGames.getSelectedRow() != -1) {
                    String[] columnNames = {"Title", "Platform", "Adress", "Ort", "Stock"};

                    /*Update storeStock table content depending on selected value of the availableGames table.*/
                    util.addToTable
                            (conn.showStoreStockForGivenProduct(availableGames.getValueAt(availableGames.getSelectedRow(), 0).toString(), platformParam),
                                    columnNames, storeStock);
                    ArrayList<ArrayList<String>> rowList = new ArrayList<>();
                    for (int i = 0; i < storeStock.getRowCount(); i++) {
                        if (storeStock.getModel().getValueAt(i, 4).toString().equals("0")) {
                            ArrayList<String> columnList = new ArrayList<>();
                            for (int j = 0; j < storeStock.getColumnCount() - 1; j++) {
                                columnList.add(storeStock.getModel().getValueAt(i, j).toString());
                            }
                            rowList.add(columnList);
                        }
                    }
                    list1.setListData(rowList.toArray());
                }
            }
        });

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickedIndex = list1.getSelectedIndex();
                String[] tmp = list1.getModel().getElementAt(clickedIndex).toString().split(", ");
                if (e.getClickCount() == 1) {
                    JFrame jFrame = new JFrame();
                    RegisterEmail reg = new RegisterEmail(tmp[0], tmp[1], tmp[2], tmp[3]);
                    jFrame.setContentPane(reg.getMailPanel());
                    jFrame.setDefaultCloseOperation(jFrame.HIDE_ON_CLOSE);
                    jFrame.pack();
                    jFrame.setVisible(true);
                }
            }
        };
        list1.addMouseListener(mouseListener);
    }

    private JPanel getMainPanel() {
        return this.mainPanel;
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setTitle("SpelRVI");
        MainPanel mainPanel = new MainPanel();
        jFrame.setContentPane(mainPanel.getMainPanel());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
