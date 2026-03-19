<!DOCTYPE html>
<html>
<body>
<?php
session_start();

if (isset($_SESSION["Head of Secretary"]))
	foreach	($_SESSION["Head of Secretary"] as $index => $info)
		if ($info["username"] == $_SESSION["currentUsername"]) {
			$id = $index;
			break;
		}
if (!isset($id))
	exit();

$username = $_SESSION["Head of Secretary"][$id]["username"];
echo "<title>".$_SESSION["Head of Secretary"][$id]["name"]."</title>";
$servername = "localhost";
$server_username = "root";
$password = "mysql";
$dbname = "exam_planning";

$conn = mysqli_connect($servername, $server_username, $password, $dbname);

if (!$conn)
	die("Connection failed: " . mysqli_connect_error());

$sql = "SELECT courses_name FROM employee, department, courses";
$sql .= " WHERE employee.department_id = department.department_id";
$sql .= " and courses.department_id = department.department_id";
$sql .= " GROUP BY courses_name";
$result = mysqli_query($conn, $sql) or die("Error");

if (mysqli_num_rows($result) > 0) {
	echo "<table border='1'>";
	echo "<form action='Head of Secretary.php' method='post'>";
	echo "<tr><td align='center' valign='middle'>course</td>";
	echo "<td align='center' valign='middle'>date</td>";
	echo "<td align='center' valign='middle'>time</td>";
	echo "<td align='center' valign='middle'>num of assistant</td>";
	echo "</tr><tr><td><select name='course'>";
    while($row = mysqli_fetch_array($result))
		echo "<option value='" . $row["courses_name"] . "'>".$row["courses_name"]."</option>";
	echo '</select></td><td><input type="text" name="date" value=""></td>';
	echo '<td><select name="time">';
	$times = ["09:00:00", "11:00:00", "13:00:00", "15:00:00"];
	foreach ($times as $time)
		echo "<option value='".$time."'>".$time."</option>";
	echo '</select></td><td><input type="text" name="num_of_assistant" value=""></td>';
	echo '<td><input type="submit" name="select_exam" value="Select Exam"></td></tr></form>';
	if (!isset($_POST["select_exam"]))
		echo '</table>';
}

function containsValidDate($dateString) {
    try {
        $date = new DateTime($dateString);
        return true;
    } catch (Exception $e) {
        return false;
    }
}

function getIntFromString($string) {
    if (is_numeric($string) && strpos($string, '.') === false)
        return $string;
    return null;
}

if (isset($_POST["select_exam"])) {
	if ($_POST["date"] == "")
		echo "</table>The date field cannot be left blank<br>";
	else if ($_POST["num_of_assistant"] == "")
		echo "</table>The num of assistant field cannot be left blank<br>";
	else if (containsValidDate($_POST["date"])) {
		$num_of_assistant = getIntFromString($_POST["num_of_assistant"]);
		if ($num_of_assistant == null)
			echo "</table>The num of assistant is in the wrong format<br>";
		else if ((int) $num_of_assistant <= 0)
			echo "</table>The entered number is meaningless";
		else {
			$num_of_assistant = (int) $num_of_assistant;
			$date = (new DateTime($_POST["date"]))->format('Y-m-d');
			$course = $_POST["course"];

			$result = mysqli_query($conn, "SELECT department_id FROM courses WHERE courses_name = '$course'") or die("Error");
			$department_id = (int) mysqli_fetch_array($result)["department_id"];

			$time = $_POST["time"];
			echo "<tr><td>$course</td><td>$date</td><td>$time</td><td>$num_of_assistant</td></tr></table>";
			$sql = "SELECT employee_id FROM employee, department";
			$sql .= " WHERE employee.department_id = department.department_id";
			$sql .= " and role = 'Assistant' and employee.department_id = $department_id";
			
			$result = mysqli_query($conn, $sql) or die("Error");
			
			if (mysqli_num_rows($result) > 0) {
				$index = 0;
				while($row = mysqli_fetch_array($result)) {
					$assistant = $_SESSION["Assistant"][$row["employee_id"]];
					$assistant["$id"] = $row["employee_id"];
					if (!isset($assistant["plan"][$date][$time]) &&
						!isset($assistant["plan"][(new DateTime($date))->format('l')][$time]))
						$assistants[$index++] = $assistant;
				}
				if ($index != 0) {
					for ($i = 0; $i < $index - 1; $i++)
						for ($j = $i + 1; $j < $index; $j++)
							if ($assistants[$j]["point"] < $assistants[$i]["point"]) {
								$tmp = $assistants[$j];
								$assistants[$j] = $assistants[$i];
								$assistants[$i] = $tmp;
							}
					$result = mysqli_query($conn, "SELECT * FROM exam") or die("Error");
					$num_of_exam = 0;
					if (mysqli_num_rows($result) > 0) 
						while($row = mysqli_fetch_array($result))
							$num_of_exam++;
					echo "<table border='1'>";
					echo "<tr><th>Assistants</th></tr>";
					for ($i = 0; $i < $num_of_assistant && $i < $index; $i++) {
						$sql = "INSERT INTO exam (exam_id, exam_date, exam_time, courses_name, employee_id) VALUES";
						$sql .= " (".++$num_of_exam.", '$date', '$time', '$course', ".$assistants[$i]["$id"].")";
						mysqli_query($conn, $sql) or die("Error");
						echo "<tr><td>".$assistants[$i]["name"]."</td></tr>";
						$_SESSION["Assistant"][$assistants[$i]["$id"]]["point"]++;
						$_SESSION["Assistant"][$assistants[$i]["$id"]]["plan"][$date][$time] = $course." exam";
					}
					echo "</table>";
				}
			}
		}
	} else
		echo "</table>The date is in the wrong format<br>";
}

$result = mysqli_query($conn, "SELECT faculty_name FROM faculty") or die("Error");

if (mysqli_num_rows($result) > 0) {
	echo "<form action='Head of Secretary.php' method='post'>";
	echo "<table border='1'>";
	echo "<tr><td align='center' valign='middle'>faculty</td>";
	echo "<td align='center' valign='middle'>department</td>";
	echo "<td align='center' valign='middle'>course</td>";
	echo "<td align='center' valign='middle'>day</td>";
	echo "<td align='center' valign='middle'>time</td></tr><tr><td>";
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
	echo '</td>';
	echo '<td><input type="text" name="course" value=""></td><td><select name="day">';
	$days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
	foreach ($days as $day)
		echo "<option value='".$day."'>".$day."</option>";
	echo '</select></td><td><select name="time">';
	$times = ["09:00:00", "11:00:00", "13:00:00", "15:00:00"];
	foreach ($times as $time)
		echo "<option value='".$time."'>".$time."</option>";
	echo '</select></td><td><input type="submit" name="select_course" value="Select Course"></td></tr></table></form>';
}

if (isset($_POST["select_course"])) {
	if ($_POST["course"] == "")
		echo "The course field cannot be left blank<br>";
	else if (!isset($_POST["department"]))
		echo "The department field cannot be left blank<br>";
	else {
		$result = mysqli_query($conn, "SELECT department_name FROM department WHERE faculty_id = ".$_SESSION["faculty"][$_SESSION["Selected_faculty"]]." ") or die("Error");
		$isOkey = false;
		if (mysqli_num_rows($result) > 0)
			while($row = mysqli_fetch_array($result))
				if ($row["department_name"] == $_POST["department"]) {
					$isOkey = true;
					break;
				}
		if ($isOkey) {
			$result = mysqli_query($conn, "SELECT * FROM courses") or die("Error");
			$num_of_courses = 0;
			if (mysqli_num_rows($result) > 0) 
				while($row = mysqli_fetch_array($result))
					$num_of_courses++;
			$sql = "INSERT INTO courses (courses_id, courses_name, courses_day, courses_time, department_id) VALUES";
			$sql .= " (".++$num_of_courses.", '".$_POST["course"]."', '".$_POST["day"]."', '".$_POST["time"]."', ".$_SESSION["faculty"][$_SESSION["Selected_faculty"]].")";
			mysqli_query($conn, $sql) or die("Error");
		} else
			echo "Department and faculty are incompatible<br>";
	}
}
echo "<form action='Head of Secretary.php' method='post'>";
echo '<input type="submit" name="displayScores" value="Display Scores"></form>';

if (isset($_POST["displayScores"])) {
	$result = mysqli_query($conn, "SELECT employee_id FROM employee WHERE role = 'Assistant'") or die("Error");
	if (mysqli_num_rows($result) > 0)
		while($row = mysqli_fetch_array($result))
			echo $_SESSION["Assistant"][$row["employee_id"]]["name"]." => ".$_SESSION["Assistant"][$row["employee_id"]]["point"]."<br>";
}

echo "<form action='Start Screen.php' method='post'>";
echo '<input type="submit" value="Return Start Screen"></form>';

mysqli_close($conn);

?>
</body>
</html>