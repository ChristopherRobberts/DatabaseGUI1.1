import java.sql.*;
import java.util.ArrayList;

public class DBConnection {
    private Connection conn;

    public void connectDataBase() {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/Chris/Desktop/SpelRvi_Access.accdb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String addNewCoverage(String emailParam, String cityParam, String addressParam, String platformParam, String titleParam) {
        try {
            boolean isAlreadyRegistered = false;
            String query;
            PreparedStatement prepStatement;
            Statement st = conn.createStatement();
            ResultSet rs;
            int coverageID = 0;
            int idCounter = 0;
            int saldo_id = 0;
            query = "SELECT * FROM bevakare";
            rs = st.executeQuery(query);
            while (rs.next()) {
                if (rs.getString(2).equals(emailParam)) {
                    isAlreadyRegistered = true;
                    coverageID = rs.getInt(1);
                }
                idCounter++;
            }
            if (!isAlreadyRegistered) {
                coverageID = idCounter;
                st.close();
                query = "INSERT INTO Bevakare (bevakar_id, emejlAdress) VALUES (?, ?)";
                prepStatement = conn.prepareStatement(query);
                prepStatement.setInt(1, coverageID);
                prepStatement.setString(2, emailParam);
                prepStatement.executeUpdate();
            }
            query = "SELECT saldo_id " +
                    "FROM SpelSaldo, Butik " +
                    "WHERE SpelSaldo.butik_id = Butik.butik_id " +
                    "AND SpelSaldo.produkt_id IN (SELECT produkt_id FROM Produkt, SpelTitel " +
                                                "WHERE Produkt.titel_id = SpelTitel.titel_id " +
                                                "AND SpelTitel.speltitel = ? " +
                                                "AND Produkt.plattform = ?) " +
                    "AND Butik.adress = ? AND Butik.ort = ?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, titleParam);
            prepStatement.setString(2, platformParam);
            prepStatement.setString(3, addressParam);
            prepStatement.setString(4, cityParam);
            rs = prepStatement.executeQuery();
            while (rs.next()) {
                saldo_id = rs.getInt(1);
            }
            prepStatement.close();
            query = "INSERT INTO Bevakning (saldo_id, bevakar_id) VALUES(?, ?)";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, saldo_id);
            prepStatement.setInt(2, coverageID);
            prepStatement.executeUpdate();
            prepStatement.close();
            return "Success! Thank you for visiting Spel RVI " + emailParam;
        } catch (Exception e) {return "you have already asked to be informed about this game!";}
    }

    public ArrayList<ArrayList<String>> showStoreStockForGivenProduct(String titleParam, String platformParam) {
        try {
            String query;
            PreparedStatement prepStatement;
            if (!platformParam.equals("")) {
                query = "SELECT SpelTitel.speltitel, Produkt.plattform, Butik.adress, Butik.ort, SpelSaldo.antal\n" +
                        "FROM Produkt, SpelTitel, SpelSaldo, Butik\n" +
                        "WHERE SpelTitel.titel_id = Produkt.titel_id\n" +
                        "AND Produkt.produkt_id = SpelSaldo.produkt_id\n" +
                        "AND  SpelSaldo.butik_id = Butik.butik_id\n" +
                        "AND SpelTitel.speltitel = ? AND Produkt.plattform = ?";
                prepStatement = conn.prepareStatement(query);
                prepStatement.setString(1, titleParam);
                prepStatement.setString(2, platformParam);
            } else {
                query = "SELECT SpelTitel.speltitel, Produkt.plattform, Butik.adress, Butik.ort, SpelSaldo.antal\n" +
                        "FROM Produkt, SpelTitel, SpelSaldo, Butik\n" +
                        "WHERE SpelTitel.titel_id = Produkt.titel_id\n" +
                        "AND Produkt.produkt_id = SpelSaldo.produkt_id\n" +
                        "AND  SpelSaldo.butik_id = Butik.butik_id\n" +
                        "AND SpelTitel.speltitel = ?";
                prepStatement = conn.prepareStatement(query);
                prepStatement.setString(1, titleParam);
            }
            ResultSet rs = prepStatement.executeQuery();
            ArrayList<ArrayList<String>> arr = new ArrayList<>();
            while (rs.next()) {
                ArrayList<String> innerArray = new ArrayList<>();
                innerArray.add(rs.getString(1));
                innerArray.add(rs.getString(2));
                innerArray.add(rs.getString(3));
                innerArray.add(rs.getString(4));
                innerArray.add(rs.getString(5));
                arr.add(innerArray);
            }
            prepStatement.close();
            return arr;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<ArrayList<String>> productsWithGenreOrPlatform(String platformParam, String genreParam) {
        try {
            ArrayList<ArrayList<String>> arr = new ArrayList<>();
            ResultSet rs;
            PreparedStatement prepStatement;
            if (platformParam.equals("") && !genreParam.isEmpty()) {
                String query = "SELECT DISTINCT SpelTitel.speltitel\n" +
                        "FROM Plattform " +
                        "INNER JOIN ((SpelTitel INNER JOIN " +
                        "(Genre INNER JOIN GenreTabell ON Genre.gerneTyp = GenreTabell.genreTypFN)" +
                        "ON SpelTitel.titel_id = GenreTabell.titel_idFN) INNER JOIN Produkt " +
                        "ON SpelTitel.titel_id = Produkt.titel_id) ON Plattform.Namn = Produkt.plattform\n" +
                        "WHERE gerneTyp = ?;";
                prepStatement = conn.prepareStatement(query);
                prepStatement.setString(1, genreParam);
                rs = prepStatement.executeQuery();
                while (rs.next()) {
                    ArrayList<String> innerArray = new ArrayList<>();
                    innerArray.add(rs.getString(1));
                    arr.add(innerArray);
                }
                return arr;
            } else if (genreParam.equals("") && !platformParam.isEmpty()) {
                String query = "SELECT DISTINCT SpelTitel.speltitel\n" +
                        "FROM Plattform " +
                        "INNER JOIN ((SpelTitel INNER JOIN " +
                        "(Genre INNER JOIN GenreTabell ON Genre.gerneTyp = GenreTabell.genreTypFN) " +
                        "ON SpelTitel.titel_id = GenreTabell.titel_idFN) INNER JOIN Produkt " +
                        "ON SpelTitel.titel_id = Produkt.titel_id) ON Plattform.Namn = Produkt.plattform\n" +
                        "WHERE Plattform.namn = ?;";
                prepStatement = conn.prepareStatement(query);
                prepStatement.setString(1, platformParam);
                rs = prepStatement.executeQuery();
                while (rs.next()) {
                    ArrayList<String> innerArray = new ArrayList<>();
                    innerArray.add(rs.getString(1));
                    arr.add(innerArray);
                }
                return arr;
            } else if (genreParam.isEmpty() && platformParam.isEmpty()) {
                try {
                    Statement statement = conn.createStatement();
                    rs = statement.executeQuery("SELECT speltitel FROM SpelTitel");
                    arr = new ArrayList<>();
                    while (rs.next()) {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(rs.getString("speltitel"));
                        arr.add(tmp);
                    }
                    statement.close();
                    return arr;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            } else {
                String query = "SELECT DISTINCT SpelTitel.speltitel\n" +
                        "FROM Plattform " +
                        "INNER JOIN ((SpelTitel INNER JOIN " +
                        "(Genre INNER JOIN GenreTabell ON Genre.gerneTyp = GenreTabell.genreTypFN) " +
                        "ON SpelTitel.titel_id = GenreTabell.titel_idFN) INNER JOIN Produkt ON SpelTitel.titel_id = Produkt.titel_id) " +
                        "ON Plattform.Namn = Produkt.plattform\n" +
                        "WHERE gerneTyp = ? AND plattform = ?";
                prepStatement = conn.prepareStatement(query);
                prepStatement.setString(1, genreParam);
                prepStatement.setString(2, platformParam);
                rs = prepStatement.executeQuery();
                while (rs.next()) {
                    ArrayList<String> innerArray = new ArrayList<>();
                    innerArray.add(rs.getString(1));
                    arr.add(innerArray);
                }
                return arr;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getGenre() {
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Genre");
            ArrayList<String> genres = new ArrayList<>();
            genres.add("");
            while (rs.next()) {
                genres.add(rs.getString(1));
            }
            statement.close();
            return genres;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getPlatform() {
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Plattform");
            ArrayList<String> platforms = new ArrayList<>();
            platforms.add("");
            while (rs.next()) {
                platforms.add(rs.getString(1));
            }
            statement.close();
            return platforms;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
