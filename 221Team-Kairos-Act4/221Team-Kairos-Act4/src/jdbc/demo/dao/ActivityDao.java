package jdbc.demo.dao;

import jdbc.demo.model.ActivityRecord;

import java.sql.SQLException;
import java.util.List;

public interface ActivityDao {
    // Get all activity records with requester, approver, and facility info.
    List<ActivityRecord> findAllActivities() throws SQLException;

    // Get activities for one requester.
    List<ActivityRecord> findActivitiesByRequesterId(int requesterId) throws SQLException;
}
