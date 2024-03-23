package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLCommandGenerator {
    private String sqlCommand;
    private ResultSet resultSet;
    private int parametersCounter;
    private PreparedStatement preparedStatement;

    public String generateTableSqlCommand(String name, String[][] tableParameters) {
        sqlCommand = "CREATE TABLE IF NOT EXISTS " + name + " (";

        for (String[] columnParameters : tableParameters) {
            sqlCommand = sqlCommand + columnParameters[0] + " " + columnParameters[1];

            if (columnParameters[0].equals(tableParameters[tableParameters.length - 1][0])) {
                sqlCommand = sqlCommand + ");";
            } else {
                sqlCommand = sqlCommand + ", ";
            }
        }

        return sqlCommand;
    }

    public String generateInsertSqlCommand(String table, Object[][] insertData) {
        sqlCommand = "INSERT INTO " + table + " (";

        for (Object[] data : insertData) {
            if (data[0].equals(insertData[insertData.length - 1][0])) {
                sqlCommand = sqlCommand + data[0] + ")";
            } else {
                sqlCommand = sqlCommand + data[0] + ", ";
            }
        }

        sqlCommand = sqlCommand + " VALUES (";

        for (Object[] data : insertData) {
            if (data[0].equals(insertData[insertData.length - 1][0])) {
                sqlCommand = sqlCommand + "?);";
            } else {
                sqlCommand = sqlCommand + "?,";
            }
        }

        return sqlCommand;
    }

    public String generateSelectSqlCommand(String table, String column, String Key, String keyValue) {

        return "SELECT " + column + " FROM " + table + " WHERE " + Key + " = " + keyValue + " ORDER BY id ASC";
    }

    public String generateSelectSqlCommand(String table, String column, String Key1, String keyValue1, String Key2, String keyValue2) {

        return "SELECT " + column + " FROM " + table + " WHERE " + Key1 + " = " + keyValue1 + " AND " + Key2 + " = " + keyValue2 +" ORDER BY id ASC";
    }

    public String generateSelectSqlCommand(String table, String column, String Key1, String keyValue1, String Key2, String keyValue2, String Key3, String keyValue3) {

        return "SELECT " + column + " FROM " + table + " WHERE " + Key1 + " = " + keyValue1 + " AND " + Key2 + " = " + keyValue2 + " AND " +  Key3 + " = " + keyValue3 +  " ORDER BY id ASC";
    }

    public String generateSelectSqlCommand(String table, String Key, String keyValue) {

        return "SELECT * FROM " + table + " WHERE " + Key + " = " + keyValue + " ORDER BY id ASC";
    }

    public String generateSelectSqlCommand(String table, String column) {
        return "SELECT " + column + " FROM " + table + " ORDER BY id ASC";
    }

    public String generateUpdateSqlCommand(String table, Object[][] updateData, String key, Object keyValue)
            throws SQLException {
        sqlCommand = "UPDATE " + table + " SET ";

        for (Object[] data : updateData) {
            sqlCommand = sqlCommand + data[0] + "=?";

            if (data[0].equals(updateData[updateData.length - 1][0])) {
                sqlCommand = sqlCommand + " WHERE " + key + " =?";
            } else {
                sqlCommand = sqlCommand + ", ";
            }
        }

        return sqlCommand;
    }

    public String generateDeleteSqlCommand(String table, String key, String keyValue) {
        if (keyValue == null) {
            return "DELETE FROM " + table + " WHERE " + key + " IS NULL";

        } else {
            return "DELETE FROM " + table + " WHERE " + key + "=" + keyValue;
        }
    }

    public PreparedStatement addCommandValues(Connection connection, String table, String sqlCommand, Object[][] columnData) throws SQLException{
        preparedStatement = connection.prepareStatement(sqlCommand);
        parametersCounter=0;

        //Achar forma de utilizar essa função com INSERT e UPDATE
          for(int i=0;i<sqlCommand.length();i++){
            if(sqlCommand.charAt(i)=='?'){
                parametersCounter++;
            }
        }

        for(int i=1;i<=parametersCounter;i++){

            preparedStatement = addValueType(preparedStatement, table, getColumnType(connection, table, columnData[i-1][0].toString()), i, columnData[i-1][1]);

        }
        
        return preparedStatement;
    }

    public PreparedStatement addCommandValues(Connection connection, String table, String sqlCommand, String key, Object keyValue, Object[][] columnData) throws SQLException{
        preparedStatement = connection.prepareStatement(sqlCommand);
        parametersCounter=0;

        for(int i=0;i<sqlCommand.length();i++){
            if(sqlCommand.charAt(i)=='?'){
                parametersCounter++;
            }
        }

        for(int i=1;i<=parametersCounter;i++){
            if(i==parametersCounter){
                addValueType(preparedStatement, table, getColumnType(connection, table, key), i, keyValue);
            }else{
                preparedStatement = addValueType(preparedStatement, table, getColumnType(connection, table, columnData[i-1][0].toString()), i, columnData[i-1][1]);
            }
        }
        
        return preparedStatement;

    }

    public PreparedStatement addValueType(PreparedStatement preparedStatement, String table, String columnType,
            int index, Object columnData) throws NumberFormatException, SQLException {
        switch (columnType) {
            case "TEXT":
                preparedStatement.setString(index, columnData.toString());
                break;

            case "INTEGER":
                preparedStatement.setInt(index, Integer.parseInt(columnData.toString()));
                break;

            case "DECIMAL":
                preparedStatement.setDouble(index, Double.parseDouble(columnData.toString()));
                break;

            default:
                break;
        }

        return preparedStatement;
    }

    public String getColumnType(Connection connection, String table, String column) throws SQLException {
        resultSet = connection.getMetaData().getColumns(null, null, table, null);

        while (resultSet.next()) {
            if (resultSet.getString(4).equals(column)) {
                return resultSet.getString(6);
            }
        }
        return null;

    }

}
