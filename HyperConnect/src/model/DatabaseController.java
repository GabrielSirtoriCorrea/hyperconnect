package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseController {
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private SQLCommandGenerator generator;
    private ResultSet resultSet;
    private String command;
    private ArrayList<Object> result;

    public DatabaseController(String path) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            statement = connection.createStatement();
        } catch (SQLException sqlException) {
            sqlException.getErrorCode();
        }
    }

    public void CreateTables() {
        try {
            String[][] COD_OPERACAO = { { "ID", "integer PRIMARY KEY AUTOINCREMENT" }, { "MATERIAL", "TEXT" }, {
                    "ESPESSURA", "DECIMAL" },
                    { "CORRENTE", "integer" },
                    { "GAS_CORTAR", "TEXT" }, { "GAS_MARCAR", "TEXT" }, { "COD_CORTAR", "TEXT" }, {
                            "COD_MARCAR", "TEXT" } };

            String[][] ERROS = { { "ID", "integer" }, { "DESCRICAO", "TEXT" } };
            String[][] ESTADOS = { { "ID", "integer" }, { "DESCRICAO", "TEXT" } };

            generator = new SQLCommandGenerator();

            statement.execute(generator.generateTableSqlCommand("COD_OPERACAO", COD_OPERACAO));
            statement.execute(generator.generateTableSqlCommand("ERROS", ERROS));
            statement.execute(generator.generateTableSqlCommand("ESTADOS", ESTADOS));
        } catch (SQLException sqlException) {
            sqlException.getErrorCode();
        }
    }

    public void insertData(String table, Object[][] inserObjects) {
        try {
            command = generator.generateInsertSqlCommand(table, inserObjects);
            preparedStatement = generator.addCommandValues(connection, table, command, inserObjects);
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            sqlException.getErrorCode();
        }
    }

    public Object[] getData(String table, String column, String Key, String keyValue){
        try {
            result = new ArrayList<>();
            resultSet = statement.executeQuery(generator.generateSelectSqlCommand(table, column, Key, keyValue));
            
            while (resultSet.next()) {
                result.add(resultSet.getObject(1));
            }
        } catch (SQLException sqlException) {
            sqlException.getErrorCode();
        }
        return result.toArray();
    }

    public Object[] getData(String table, String column, String Key1, String keyValue1, String Key2, String keyValue2){
        try {
            result = new ArrayList<>();
            resultSet = statement.executeQuery(generator.generateSelectSqlCommand(table, column, Key1, keyValue1, Key2, keyValue2));
            
            while (resultSet.next()) {
                result.add(resultSet.getObject(1));
            }
        } catch (SQLException sqlException) {
            sqlException.getErrorCode();
        }
        return result.toArray();
    }

    public Object[] getData(String table, String column, String Key1, String keyValue1, String Key2, String keyValue2, String Key3, String keyValue3){
        try {
            result = new ArrayList<>();
            resultSet = statement.executeQuery(generator.generateSelectSqlCommand(table, column, Key1, keyValue1, Key2, keyValue2, Key3, keyValue3));
            
            while (resultSet.next()) {
                result.add(resultSet.getObject(1));
            }
        } catch (SQLException sqlException) {
            sqlException.getErrorCode();
        }
        return result.toArray();
    }

    public Object[] getData(String table, String column){
        try {
            result = new ArrayList<>();
            resultSet = statement.executeQuery(generator.generateSelectSqlCommand(table, column));
            
            while (resultSet.next()) {
                result.add(resultSet.getObject(1));
            }
        } catch (SQLException sqlException) {
            sqlException.getErrorCode();
        }
        return result.toArray();
    }

    public void deleteData(String table, String key, String keyvalue){
        try {
            statement.execute(generator.generateDeleteSqlCommand(table, key, keyvalue));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateData(String table, Object[][] updateData, String key, Object keyValue){
        try {
            command = generator.generateUpdateSqlCommand(table, updateData, key, keyValue);
            System.out.println(command);
            preparedStatement = generator.addCommandValues(connection, table, command, key, keyValue,  updateData);
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            sqlException.getErrorCode();
        }
    }

}
