<!DOCTYPE html>
<html>
<body>
<?php
session_start();

if (isset($_SESSION["Dean"]))
	foreach	($_SESSION["Dean"] as $index => $info)
		if ($info["username"] == $_SESSION["currentUsername"]) {
			$id = $index;
			break;
		}
if (!isset($id))
	exit();

$username = $_SESSION["Dean"][$id]["username"];
echo "<title>".$_SESSION["Dean"][$id]["name"]."</title>";
$servername = "localhost";
$server_username = "root";
$password = "mysql";
$dbname = "exam_planning";

$conn = mysqli_connect($servername, $server_username, $password, $dbname);

if (!$conn)
	die("Connection failed: " . mysqli_connect_error());

$result = mysqli_query($conn, "SELECT faculty_name FROM faculty") or die("Error");

if (mysqli_num_rows($result) > 0) {
	echo "<form action='Dean.php' method='post'>";
	echo "<table border='1'>";
	echo "<tr><td align='center' valign='middle'>faculty</td>";
	echo "<td align='center' valign='middle'>department</td></tr><tr><td>";
	if (!isset($_POST["selectFaculty"])) {
		echo "<select name='faculty'>";
		while($row = mysqli_fetch_array($result))
			echo "<option value='" . $row["faculty_name"] . "'>".$row["faculty_name"]."</option>";
		echo '</select><input type="submit" name="selectFaculty" value="Select Faculty">';
	} else {
		$_SESSION["Selected_faculty"] = $_POST["faculty"];
		echo $_POST["faculty"];
		echo '<input type="submit" value="Refresh">';
	}
	echo '</td><td>';
	if (isset($_POST["selectFaculty"])) {
		echo '<select name="department">';
		$result = mysqli_query($conn, "SELECT department_name FROM department WHERE faculty_id = ".$_SESSION["faculty"][$_POST["faculty"]]." ") or die("Error");
		if (mysqli_num_rows($result) > 0)
			while($row = mysqli_fetch_array($result))
				echo "<option value='" . $row["department_name"] . "'>".$row["department_name"]."</option>";
		echo "</select>";
	}
	echo '</td><td><input type="submit" name="displayExamList" value="Display Exam List"></td></tr></table></form>';
}

if (isset($_POST["displayExamList"])) {
	if (!isset($_POST["department"]))
		echo "The department field cannot be left blank<br>";
	else {
		$result = mysqli_query($conn, "SELECT department_id FROM department WHERE department_name = '".$_POST["department"]."'") or die("Error");
		$department_id = (int) mysqli_fetch_array($result)["department_id"];
		$sql = "SELECT exam_date, exam_time, courses_name FROM exam, employee ";
		$sql .= "WHERE employee.employee_id = exam.employee_id and ";
		$sql .= "department_id = $department_id ";
		$sql .= "GROUP BY exam_date, exam_time, courses_name";
		$result = mysqli_query($conn, $sql) or die("Error");


		if (mysqli_num_rows($result) > 0) {
			$index = 0;
			while($row = mysqli_fetch_array($result)) {
				$exams[$index]["exam_date"] = $row["exam_date"];
				$exams[$index]["exam_time"] = $row["exam_time"];
				$exams[$index++]["courses_name"] = $row["courses_name"];
			}
			if (isset($exams)) {
				for ($i = 0; $i < $index - 1; $i++) {
					$date1 = strtotime($exams[$i]["exam_date"]);
					$time1 = strtotime($exams[$i]["exam_time"]); 
					for ($j = $i + 1; $j < $index; $j++) {
						$date2 = strtotime($exams[$j]["exam_date"]);
						$time2 = strtotime($exams[$j]["exam_time"]);
						if 	($date1 > $date2 || ($date1 == $date2 && $time1 > $time2)) {
							$date1 = $date2;
							$time1 = $time2;
							$tmp = $exams[$i];
							$exams[$i] = $exams[$j];
							$exams[$j] = $tmp;
						}
					}
				}
				echo "<table border='1'>";
				echo "<tr><th>Date</th><th>Time</th><th>Course</th></tr>";
				for ($i = 0; $i < $index; $i++)
					echo "<tr><td>".$exams[$i]["exam_date"]."</td><td>".$exams[$i]["exam_time"]."</td><td>".$exams[$i]["courses_name"]."</td></tr>";
				echo "</table>";
			}
		}
	}
}

echo "<form action='Start Screen.php' method='post'>";
echo '<input type="submit" value="Return Start Screen"></form>';

mysqli_close($conn);
?>
</body>
</html>