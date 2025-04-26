package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.DBUtil;

public class StudentScheduleServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("Connected to DB!");

            PreparedStatement stmt = conn.prepareStatement(
                "SELECT day_of_week, period_number, subject FROM weekly_schedule WHERE student_id = ?"
            );
            stmt.setInt(1, 1); // hardcoded student_id for now

            ResultSet rs = stmt.executeQuery();

            JSONObject schedule = new JSONObject();
            schedule.put("Monday", new JSONArray(new String[]{"-", "-", "-", "-"}));
            schedule.put("Tuesday", new JSONArray(new String[]{"-", "-", "-", "-"}));
            schedule.put("Wednesday", new JSONArray(new String[]{"-", "-", "-", "-"}));
            schedule.put("Thursday", new JSONArray(new String[]{"-", "-", "-", "-"}));

            boolean dataFound = false;

            while (rs.next()) {
                dataFound = true;
                String day = rs.getString("day_of_week");
                int period = rs.getInt("period_number");
                String subject = rs.getString("subject");

                System.out.println("Fetched: " + day + " Period " + period + " -> " + subject);

                if (schedule.has(day)) {
                    JSONArray periods = schedule.getJSONArray(day);
                    periods.put(period - 1, subject);
                }
            }

            if (!dataFound) {
                System.out.println("No schedule data found for student_id 1.");
            }

            responseJson.put("name", "Muhiminul");
            responseJson.put("schedule", schedule);

            out.print(responseJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
