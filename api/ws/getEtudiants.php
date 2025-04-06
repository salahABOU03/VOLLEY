<?php
header("Content-Type: application/json");
$pdo = new PDO("mysql:host=localhost;dbname=mobileschool", "root", "");

$stmt = $pdo->prepare("SELECT nom, prenom, ville, sexe FROM etudiant");
$stmt->execute();
$results = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode($results);
