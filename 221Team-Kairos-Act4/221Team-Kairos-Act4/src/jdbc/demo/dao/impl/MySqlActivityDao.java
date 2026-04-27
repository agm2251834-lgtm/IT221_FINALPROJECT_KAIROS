package jdbc.demo.dao.impl;

import jdbc.demo.config.DatabaseConnection;
import jdbc.demo.dao.ActivityDao;
import jdbc.demo.model.ActivityRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlActivityDao implements ActivityDao {
    // Query for showing all activity records with related names and facility.
    private static final String SELECT_ALL_ACTIVITIES = """
            SELECT a.activityId,
                   CONCAT(req.firstName, ' ', req.lastName) AS requesterName,
                   COALESCE(CONCAT(app.firstName, ' ', app.lastName), 'N/A') AS approvedByName,
                   a.activityName,
                   a.activityType,
                   a.requestDate,
                   a.activityDate,
                   a.status,
                   a.remarks,
                   f.facilityName
            FROM ACTIVITY a
            JOIN `USER` req ON a.requesterId = req.userId
            LEFT JOIN `USER` app ON a.approvedBy = app.userId
            LEFT JOIN ACTIVITYDETAILS ad ON a.activityid = ad.activityid
            LEFT JOIN FACILITY f ON ad.facilityid = f.facilityid
            ORDER BY a.activityId;
            """;

    // Same activity query but for one requester only.
    private static final String SELECT_ACTIVITIES_BY_REQUESTER_ID = """
            SELECT a.activityId,
                   CONCAT(req.firstName, ' ', req.lastName) AS requesterName,
                   COALESCE(CONCAT(app.firstName, ' ', app.lastName), 'N/A') AS approvedByName,
                   a.activityName,
                   a.activityType,
                   a.requestDate,
                   a.activityDate,
                   a.status,
                   a.remarks,
                   f.facilityName
            FROM ACTIVITY a
            JOIN `USER` req ON a.requesterId = req.userId
            LEFT JOIN `USER` app ON a.approvedBy = app.userId
            LEFT JOIN ACTIVITYDETAILS ad ON a.activityid = ad.activityid
            LEFT JOIN FACILITY f ON ad.facilityid = f.facilityid
            WHERE a.requesterId = ?
            ORDER BY a.activityId;
            """;

    @Override
    public List<ActivityRecord> findAllActivities() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ACTIVITIES);
             ResultSet rs = stmt.executeQuery()) {
            return mapActivities(rs);
        }
    }

    @Override
    public List<ActivityRecord> findActivitiesByRequesterId(int requesterId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACTIVITIES_BY_REQUESTER_ID)) {
            stmt.setInt(1, requesterId);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapActivities(rs);
            }
        }
    }

    private List<ActivityRecord> mapActivities(ResultSet rs) throws SQLException {
        List<ActivityRecord> records = new ArrayList<>();
        while (rs.next()) {
            ActivityRecord record = new ActivityRecord(
                    rs.getInt("activityId"),
                    rs.getString("requesterName"),
                    rs.getString("approvedByName"),
                    rs.getString("activityName"),
                    rs.getString("activityType"),
                    rs.getString("requestDate"),
                    rs.getString("activityDate"),
                    rs.getString("status"),
                    rs.getString("facilityName"),
                    rs.getString("remarks")
            );
            records.add(record);
        }
        return records;
    }
}
