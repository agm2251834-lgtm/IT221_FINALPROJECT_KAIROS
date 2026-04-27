package jdbc.demo.dao.impl;

import jdbc.demo.config.DatabaseConnection;
import jdbc.demo.dao.BorrowDao;
import jdbc.demo.model.BorrowRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlBorrowDao implements BorrowDao {

    // Query for all borrow records with borrower, custodian, activity, and item.
    private static final String SELECT_ALL_BORROW_RECORDS = """
            SELECT b.borrowid,
                   CONCAT(bu.firstName, ' ', bu.lastName) AS borrowerName,
                   CONCAT(cu.firstName, ' ', cu.lastName) AS custodianName,
                   a.activityName,
                   i.itemName,
                   b.dateBorrowed,
                   b.timeBorrowed,
                   b.dateReturned,
                   b.timeReturned,
                   b.status,
                   b.remarks
            FROM BORROW b
            JOIN `USER` bu ON b.borrowerId = bu.userId
            JOIN `USER` cu ON b.custodianId = cu.userId
            JOIN ACTIVITY a ON b.activityId = a.activityId
            JOIN BORROWDETAILS bd ON b.borrowid = bd.borrowid
            JOIN ITEM i ON bd.itemid = i.itemid
            ORDER BY b.borrowid;
            """;

    // Same borrow query, but filtered by status using ?.
    private static final String SELECT_BORROW_BY_STATUS = """
            SELECT b.borrowid,
                   CONCAT(bu.firstName, ' ', bu.lastName) AS borrowerName,
                   CONCAT(cu.firstName, ' ', cu.lastName) AS custodianName,
                   a.activityName,
                   i.itemName,
                   b.dateBorrowed,
                   b.timeBorrowed,
                   b.dateReturned,
                   b.timeReturned,
                   b.status,
                   b.remarks
            FROM BORROW b
            JOIN `USER` bu ON b.borrowerId = bu.userId
            JOIN `USER` cu ON b.custodianId = cu.userId
            JOIN ACTIVITY a ON b.activityId = a.activityId
            JOIN BORROWDETAILS bd ON b.borrowid = bd.borrowid
            JOIN ITEM i ON bd.itemid = i.itemid
            WHERE b.status = ?
            ORDER BY b.borrowid;
            """;

    @Override
    public List<BorrowRecord> findAllBorrowRecords() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_BORROW_RECORDS);
             ResultSet rs = stmt.executeQuery()) {
            return mapBorrowRecords(rs);
        }
    }

    @Override
    public List<BorrowRecord> findBorrowRecordsByStatus(String status) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BORROW_BY_STATUS)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapBorrowRecords(rs);
            }
        }
    }

    private List<BorrowRecord> mapBorrowRecords(ResultSet rs) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        while (rs.next()) {
            BorrowRecord record = new BorrowRecord(
                    rs.getInt("borrowid"),
                    rs.getString("borrowerName"),
                    rs.getString("custodianName"),
                    rs.getString("activityName"),
                    rs.getString("itemName"),
                    rs.getString("dateBorrowed"),
                    rs.getString("timeBorrowed"),
                    rs.getString("dateReturned"),
                    rs.getString("timeReturned"),
                    rs.getString("status"),
                    rs.getString("remarks")
            );
            records.add(record);
        }
        return records;
    }
}
