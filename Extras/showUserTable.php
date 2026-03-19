<?php
echo "<table border='1'>";
echo "<tr><td>id</td><td>name</td><td>username</td><td>password</td><td>role</td><td>department_id</td></tr>";
session_start();
if (isset($_SESSION["Assistant"]))
	foreach	($_SESSION["Assistant"] as $id => $info) {
		echo "<tr>";
		echo "<td>" . $id . "</td><td>" . $info["name"]. "</td><td>" . $info["username"]. "</td><td>" . $info["password"]."</td><td>" . $info["role"]. "</td><td>" . $info["department_id"]. "</td>";
		echo "</tr>";
	}
if (isset($_SESSION["Secretary"]))
	foreach	($_SESSION["Secretary"] as $id => $info) {
		echo "<tr>";
		echo "<td>" . $id . "</td><td>" . $info["name"]. "</td><td>" . $info["username"]. "</td><td>" . $info["password"]."</td><td>" . $info["role"]. "</td><td>" . $info["department_id"]. "</td>";
		echo "</tr>";
	}
echo "</table>";
echo "<table border='1'>";
echo "<tr><td>id</td><td>name</td><td>username</td><td>password</td><td>role</td></tr>";
if (isset($_SESSION["Head of Department"]))
	foreach	($_SESSION["Head of Department"] as $id => $info) {
		echo "<tr>";
		echo "<td>" . $id . "</td><td>" . $info["name"]. "</td><td>" . $info["username"]. "</td><td>" . $info["password"]."</td><td>Head of Department</td>";
		echo "</tr>";
	}
if (isset($_SESSION["Head of Secretary"]))
	foreach	($_SESSION["Head of Secretary"] as $id => $info) {
		echo "<tr>";
		echo "<td>" . $id . "</td><td>" . $info["name"]. "</td><td>" . $info["username"]. "</td><td>" . $info["password"]."</td><td>Head of Secretary</td>";
		echo "</tr>";
	}
if (isset($_SESSION["Dean"]))
	foreach	($_SESSION["Dean"] as $id => $info) {
		echo "<tr>";
		echo "<td>" . $id . "</td><td>" . $info["name"]. "</td><td>" . $info["username"]. "</td><td>" . $info["password"]."</td><td>Dean</td>";
		echo "</tr>";
	}
echo "</table>";

?>