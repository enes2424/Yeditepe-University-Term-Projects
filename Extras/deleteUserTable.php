<?php
session_start();
if (isset($_SESSION["Assistant"]))
	unset($_SESSION["Assistant"]);
if (isset($_SESSION["Secretary"]))
	unset($_SESSION["Secretary"]);
if (isset($_SESSION["Head of Department"]))
	unset($_SESSION["Head of Department"]);
if (isset($_SESSION["Head of Secretary"]))
	unset($_SESSION["Head of Secretary"]);
if (isset($_SESSION["Dean"]))
	unset($_SESSION["Dean"]);
header("Location: showUserTable.php");
?>