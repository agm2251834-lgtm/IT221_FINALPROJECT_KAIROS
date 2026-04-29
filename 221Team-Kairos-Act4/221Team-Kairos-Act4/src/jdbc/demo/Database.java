package jdbc.demo;

import jdbc.demo.dao.ActivityDao;
import jdbc.demo.dao.BorrowDao;
import jdbc.demo.dao.ReportDao;
import jdbc.demo.dao.impl.MySqlActivityDao;
import jdbc.demo.dao.impl.MySqlBorrowDao;
import jdbc.demo.dao.impl.MySqlReportDao;
import jdbc.demo.model.ActivityRecord;
import jdbc.demo.model.BorrowRecord;
import jdbc.demo.model.QueryResult;

import java.sql.SQLException;
import java.util.List;

/**
 * Module-style data access facade.
 * This mirrors the "data access class" discussed in the course module,
 * while still delegating to DAO implementations.
 */
public class Database {
    private final BorrowDao borrowDao;
    private final ActivityDao activityDao;
    private final ReportDao reportDao;
    private final UserDao userDao;

    public Database() {
        this.borrowDao = new MySqlBorrowDao();
        this.activityDao = new MySqlActivityDao();
        this.reportDao = new MySqlReportDao();
        this.userDao = new MySqlUserDao();
    }

    public List<BorrowRecord> getAllBorrowRecords() throws SQLException {
        return borrowDao.findAllBorrowRecords();
    }

    public List<BorrowRecord> getBorrowRecordsByStatus(String status) throws SQLException {
        return borrowDao.findBorrowRecordsByStatus(status);
    }

    public List<ActivityRecord> getAllActivities() throws SQLException {
        return activityDao.findAllActivities();
    }

    public List<ActivityRecord> getActivitiesByRequesterId(int requesterId) throws SQLException {
        return activityDao.findActivitiesByRequesterId(requesterId);
    }

    public QueryResult executeReport(int option, List<String> params) throws SQLException {
        return reportDao.executeReport(option, params);
    }
}
