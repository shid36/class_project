<%@ page import="java.sql.*, utils.DBUtil" %>
<% 
  int studentId = 1; // Change later based on session
  Connection conn = DBUtil.getConnection();
  String name = "Student";
  PreparedStatement ps;
  ResultSet rs;

  // Get name
  ps = conn.prepareStatement("SELECT name FROM students WHERE student_id=?");
  ps.setInt(1, studentId);
  rs = ps.executeQuery();
  if (rs.next()) name = rs.getString("name");
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Student Dashboard</title>
  <link rel="stylesheet" href="styles.css" />
</head>
<body>
  <header>
    <h1>Student Management System</h1>
    <nav>
      <ul>
        <li><a href="#">Dashboard</a></li>
        <li><a href="#">Schedule</a></li>
        <li><a href="#">Grades</a></li>
        <li><a href="#">Attendance</a></li>
        <li><a href="#">Announcements</a></li>
        <li><a href="login.html" class="logout">Logout</a></li>
      </ul>
    </nav>
  </header>

  <main>
    <section class="welcome">
      <h2>Welcome, <%= name %>!</h2>
      <p>Stay updated with your schedule, grades, and attendance.</p>
    </section>

    <section class="stats">
      <div class="card">
        <h3>Today's Schedule</h3>
        <%
          ps = conn.prepareStatement("SELECT subject, time FROM schedules WHERE student_id=?");
          ps.setInt(1, studentId);
          rs = ps.executeQuery();
          while (rs.next()) {
        %>
          <p><%= rs.getString("subject") %> - <%= rs.getTime("time") %></p>
        <% } %>
      </div>

      <div class="card">
        <h3>Recent Grades</h3>
        <%
          ps = conn.prepareStatement("SELECT subject, grade FROM grades WHERE student_id=?");
          ps.setInt(1, studentId);
          rs = ps.executeQuery();
          while (rs.next()) {
        %>
          <p><%= rs.getString("subject") %>: <%= rs.getString("grade") %></p>
        <% } %>
      </div>

      <div class="card">
        <h3>Attendance</h3>
        <%
          ps = conn.prepareStatement("SELECT present_percent, absent_percent FROM attendance WHERE student_id=?");
          ps.setInt(1, studentId);
          rs = ps.executeQuery();
          if (rs.next()) {
        %>
          <p>Present: <%= rs.getDouble("present_percent") %>%</p>
          <p>Absent: <%= rs.getDouble("absent_percent") %>%</p>
        <% } %>
      </div>

      <div class="card">
        <h3>Announcements</h3>
        <%
          ps = conn.prepareStatement("SELECT message FROM announcements WHERE target_group='all' OR target_group='students'");
          rs = ps.executeQuery();
          while (rs.next()) {
        %>
          <p>📢 <%= rs.getString("message") %></p>
        <% } 
          conn.close();
        %>
      </div>
    </section>
  </main>

  <footer>
    <p>&copy; 2025 Student Management System. All rights reserved.</p>
  </footer>
</body>
</html>
