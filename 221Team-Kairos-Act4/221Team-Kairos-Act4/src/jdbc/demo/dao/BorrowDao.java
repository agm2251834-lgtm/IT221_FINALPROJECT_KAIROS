package jdbc.demo.dao;

import jdbc.demo.model.BorrowRecord;

import java.sql.SQLException;
import java.util.List;

public interface BorrowDao {
    // Get all borrow records with related names and item info.
    List<BorrowRecord> findAllBorrowRecords() throws SQLException;

    // Get borrow records based on status.
    List<BorrowRecord> findBorrowRecordsByStatus(String status) throws SQLException;
}
