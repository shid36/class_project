package servlets;

import utils.DBUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class StudentGradesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        JSONObject responseData = new JSONObject();
        String studentName = "Muhiminul"; // hardcoded for now

        try (Connection conn = DBUtil.getConnection()) {
            int studentId = 1; // Replace with session-based ID later

            // Fetch detailed subject-wise grades
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT semester, subject, grade_letter, grade_point FROM result WHERE student_id = ? ORDER BY semester, subject"
            );
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            JSONObject semesterGrades = new JSONObject();
            while (rs.next()) {
                String semester = rs.getString("semester");
                JSONObject subjectData = new JSONObject();
                subjectData.put("subject", rs.getString("subject"));
                subjectData.put("gradeLetter", rs.getString("grade_letter"));
                subjectData.put("gradePoint", rs.getDouble("grade_point"));

                if (!semesterGrades.has(semester)) {
                    semesterGrades.put(semester, new JSONArray());
                }
                semesterGrades.getJSONArray(semester).put(subjectData);
            }

            // Fetch semester GPAs
            PreparedStatement gpaStmt = conn.prepareStatement(
                "SELECT semester, semester_gpa FROM semester_results WHERE student_id = ? ORDER BY semester"
            );
            gpaStmt.setInt(1, studentId);
            ResultSet gpaRs = gpaStmt.executeQuery();

            JSONObject semesterGpas = new JSONObject();
            while (gpaRs.next()) {
                semesterGpas.put(gpaRs.getString("semester"), gpaRs.getDouble("semester_gpa"));
            }

            // Final response
            responseData.put("name", studentName);
            responseData.put("grades", semesterGrades);
            responseData.put("gpas", semesterGpas);

        } catch (Exception e) {
            e.printStackTrace();
        }

        out.print(responseData.toString());
    }
}
