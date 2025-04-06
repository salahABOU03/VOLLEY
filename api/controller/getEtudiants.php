<?php
header("Content-Type: application/json");
$conn = new PDO("mysql:host=localhost;dbname=your_db", "root", "");
$stmt = $conn->prepare("SELECT nom, prenom, ville, sexe FROM etudiant");
$stmt->execute();
$rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
echo json_encode($rows);
