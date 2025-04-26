package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.DBUtil;

public class StudentAttendanceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        JSONObject responseJson = new JSONObject();
        PrintWriter out = response.getWriter();

        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("Connected to DB!");

            if (conn == null) {
                throw new ServletException("Database connection failed!");
            }

            PreparedStatement stmt = conn.prepareStatement(
                "SELECT attendance_date, status FROM attendance_records WHERE student_id = ?"
            );
            stmt.setInt(1, 1); // hardcoded student_id for now

            ResultSet rs = stmt.executeQuery();

            HashMap<String, List<JSONObject>> attendanceByMonth = new HashMap<>();
            boolean dataFound = false;

            while (rs.next()) {
                dataFound = true;
                String attendanceDate = rs.getString("attendance_date"); // updated to correct column
                String status = rs.getString("status");

                if (attendanceDate == null || status == null) {
                    throw new ServletException("Null data in attendance row!");
                }

                String month = getMonthFromDate(attendanceDate);

                JSONObject record = new JSONObject();
                record.put("date", attendanceDate);
                record.put("status", status);

                attendanceByMonth.putIfAbsent(month, new ArrayList<>());
                attendanceByMonth.get(month).add(record);
            }

            if (!dataFound) {
                System.out.println("No attendance data found for student_id 1.");
            }

            JSONObject attendanceData = new JSONObject();
            for (String month : attendanceByMonth.keySet()) {
                JSONArray recordsArray = new JSONArray(attendanceByMonth.get(month));
                attendanceData.put(month, recordsArray);
            }

            responseJson.put("name", "Muhiminul"); // Example
            responseJson.put("attendance", attendanceData);

            out.print(responseJson.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            JSONObject errorJson = new JSONObject();
            errorJson.put("error", e.getMessage());
            out.print(errorJson.toString());
            out.flush();
        }
    }

    private String getMonthFromDate(String date) {
        // Expected date format: YYYY-MM-DD
        String monthNumber = date.substring(5, 7);
        switch (monthNumber) {
            case "01": return "January";
            case "02": return "February";
            case "03": return "March";
            case "04": return "April";
            case "05": return "May";
            case "06": return "June";
            case "07": return "July";
            case "08": return "August";
            case "09": return "September";
            case "10": return "October";
            case "11": return "November";
            case "12": return "December";
            default: return "Unknown";
        }
    }
}
