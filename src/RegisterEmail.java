import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Chris on 2018-02-24.
 */
public class RegisterEmail {
    private DBConnection conn;
    private JTextField textField1;
    private JPanel mailPanel;
    private JButton submitBtn;
    private JLabel jLabel;
    private JLabel revieveStatus;
    private String status;

    public RegisterEmail(String gameName, String platformName, String address, String city) {
        StringBuilder gName = new StringBuilder(gameName);
        StringBuilder cName = new StringBuilder(city);
        gName.deleteCharAt(0);
        cName.deleteCharAt(cName.length() - 1);
        final String properGameName = gName.toString();
        final String properCityName = cName.toString();
        jLabel.setText("<html>Enter E-Mail to add coverage for<br/>" +
                "Game: " + properGameName +
                "<br/>Platform: " + platformName +
                "<br/>Adress: " + address +
                "<br/>City: " + properCityName + "</html>");
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField1.getText().isEmpty()) {
                    conn = new DBConnection();
                    conn.connectDataBase();
                    status = conn.addNewCoverage(textField1.getText(), properCityName, address, platformName, properGameName);
                    revieveStatus.setText(status);

                }
            }
        });
    }

    public JPanel getMailPanel() {
        return this.mailPanel;
    }
}
