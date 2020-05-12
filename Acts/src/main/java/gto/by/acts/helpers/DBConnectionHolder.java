package gto.by.acts.helpers;

import android.content.SharedPreferences;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import gto.by.acts.other.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Попытка сделать хранение открытого соединения. Не завершено.
 */
public class DBConnectionHolder {
    private static Connection c = null;
    private static PreparedStatement ps1 = null;

    /**
     * Подготавливает соединение с БД.
     *
     * @param context
     * @return null при успехе, иначе текст ошибки.
     */
    public static String prepareConnection(android.content.Context context) {
        if (c != null && ps1 != null) {
            return null;
        }
        SharedPreferences prefs = context.getSharedPreferences("db", MODE_PRIVATE);
        String userName = prefs.getString(Constants.USERNAME, null);
        String userPassword = prefs.getString(Constants.PASSWORD, null);
        String connString = prefs.getString(Constants.CONNECTIONSTRING, null);
        try {
            c = DriverManager.getConnection(connString, userName, userPassword);
            ps1 = c.prepareStatement("Update [AWP_BTO].[Application].[Act] set [ActStatusID]=? where [number] = ?"); //  and [Period] = ?
            return null;
        } catch (SQLException e) {
            closeResources();
            return e.getMessage();
        }
    }

    public static void closeResources() {
        if (ps1 != null) {
            try {
                ps1.close();
            } catch (SQLException ignored) {
            }
            ps1 = null;
        }
        if (c != null) {
            try {
                c.close();
            } catch (SQLException ignored) {
            }
            c = null;
        }
    }

    public static PreparedStatement getStatement() {
        return ps1;
    }
}
