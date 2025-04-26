package servlets;

import utils.DBUtil;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;
import org.json.*;

public class StudentDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int studentId = 1; // Session-based later
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject json = new JSONObject();

        try (Connection conn = DBUtil.getConnection()) {

            // Name
            PreparedStatement ps = conn.prepareStatement("SELECT name FROM students WHERE student_id=?");
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) json.put("name", rs.getString("name"));

            // Schedule
            ps = conn.prepareStatement("SELECT subject, time FROM schedules WHERE student_id=?");
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            JSONArray scheduleArray = new JSONArray();
            while (rs.next()) {
                JSONObject entry = new JSONObject();
                entry.put("subject", rs.getString("subject"));
                entry.put("time", rs.getString("time"));
                scheduleArray.put(entry);
            }
            json.put("schedule", scheduleArray);

            // Grades
            ps = conn.prepareStatement("SELECT subject, grade FROM grades WHERE student_id=?");
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            JSONArray gradesArray = new JSONArray();
            while (rs.next()) {
                JSONObject grade = new JSONObject();
                grade.put("subject", rs.getString("subject"));
                grade.put("grade", rs.getString("grade"));
                gradesArray.put(grade);
            }
            json.put("grades", gradesArray);

            // Attendance
            ps = conn.prepareStatement("SELECT present_percent, absent_percent FROM attendance WHERE student_id=?");
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            if (rs.next()) {
                JSONObject attendance = new JSONObject();
                attendance.put("present", rs.getDouble("present_percent"));
                attendance.put("absent", rs.getDouble("absent_percent"));
                json.put("attendance", attendance);
            }

            // Announcements
            ps = conn.prepareStatement("SELECT message FROM announcements WHERE target_group='all' OR target_group='students'");
            rs = ps.executeQuery();
            JSONArray announcements = new JSONArray();
            while (rs.next()) {
                announcements.put(rs.getString("message"));
            }
            json.put("announcements", announcements);

            response.getWriter().write(json.toString());

        } catch (Exception e) {
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
