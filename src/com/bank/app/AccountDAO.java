
package com.bank.app;

import com.bank.app.BankAccount;
import java.sql.ResultSet;
import com.bank.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class AccountDAO {

    public boolean createAccount(String holderName,
                                 String pinHash,
                                 double balance) {

        String sql = "INSERT INTO accounts " +
                "(holder_name, pin_hash, balance) " +
                "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, holderName);
            ps.setString(2, pinHash);
            ps.setDouble(3, balance);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public BankAccount findAccount(int accountId) {

        String sql = "SELECT account_id, holder_name, balance " +
                "FROM accounts WHERE account_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BankAccount(
                            rs.getInt("account_id"),
                            rs.getString("holder_name"),
                            rs.getDouble("balance")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean deposit(int accountId, double amount) {

        if (amount <= 0) {
            return false;
        }

        String updateSql = "UPDATE accounts SET balance = balance + ? " +
                "WHERE account_id = ?";
        String insertSql = "INSERT INTO transactions " +
                "(account_id, transaction_type, amount) " +
                "VALUES (?, 'DEPOSIT', ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int updated;

                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setBigDecimal(1, java.math.BigDecimal.valueOf(amount));
                    ps.setInt(2, accountId);
                    updated = ps.executeUpdate();
                }

                if (updated == 0) {
                    con.rollback();
                    return false;
                }

                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    ps.setInt(1, accountId);
                    ps.setBigDecimal(2, java.math.BigDecimal.valueOf(amount));
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean withdraw(int accountId, double amount) {

        if (amount <= 0) {
            return false;
        }

        String updateSql = "UPDATE accounts SET balance = balance - ? " +
                "WHERE account_id = ? AND balance >= ?";
        String insertSql = "INSERT INTO transactions " +
                "(account_id, transaction_type, amount) " +
                "VALUES (?, 'WITHDRAW', ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int updated;

                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setBigDecimal(1, java.math.BigDecimal.valueOf(amount));
                    ps.setInt(2, accountId);
                    ps.setBigDecimal(3, java.math.BigDecimal.valueOf(amount));
                    updated = ps.executeUpdate();
                }

                if (updated == 0) {
                    con.rollback();
                    return false;
                }

                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    ps.setInt(1, accountId);
                    ps.setBigDecimal(2, java.math.BigDecimal.valueOf(amount));
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void showTransactions(int accountId) {

        String sql = "SELECT transaction_type, amount, transaction_date " +
                "FROM transactions WHERE account_id = ? " +
                "ORDER BY transaction_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println(
                            rs.getString("transaction_type") + " | " +
                                    rs.getBigDecimal("amount") + " | " +
                                    rs.getTimestamp("transaction_date")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getStoredPinHash(int accountId) {

        String sql = "SELECT pin_hash FROM accounts WHERE account_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("pin_hash");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}
