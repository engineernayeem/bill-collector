<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json; charset=UTF-8");

$request_uri = $_SERVER['REQUEST_URI'];
$method = $_SERVER['REQUEST_METHOD'];

// File paths
$customersFile = __DIR__ . '/customers.json';
$packagesFile = __DIR__ . '/packages.json';
$paymentsFile = __DIR__ . '/payments.json';
$syncFullFile = __DIR__ . '/sync_full.json';
$versionFile = __DIR__ . '/version.json';

// Initialize files if not exist
if (!file_exists($versionFile)) {
    file_put_contents($versionFile, json_encode([
        "versionCode" => 2,
        "versionName" => "1.1.0",
        "apkUrl" => "https://your-domain.com/downloads/app-release.apk",
        "releaseNotes" => "নতুন আপডেট ভার্সন!",
        "forceUpdate" => false,
        "oneSignalAppId" => "YOUR_ONESIGNAL_APP_ID_HERE"
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
}

// Endpoint handling
if (strpos($request_uri, 'version.json') !== false) {
    if ($method === 'GET') {
        echo file_get_contents($versionFile);
        exit;
    }
}

if (strpos($request_uri, 'customers.json') !== false) {
    if ($method === 'GET') {
        echo file_exists($customersFile) ? file_get_contents($customersFile) : json_encode([]);
        exit;
    } elseif ($method === 'POST') {
        $input = file_get_contents('php://input');
        file_put_contents($customersFile, $input);
        echo json_encode(["status" => "success", "message" => "Customers saved successfully", "synced_timestamp" => time() * 1000]);
        exit;
    }
}

if (strpos($request_uri, 'packages.json') !== false) {
    if ($method === 'GET') {
        echo file_exists($packagesFile) ? file_get_contents($packagesFile) : json_encode([]);
        exit;
    } elseif ($method === 'POST') {
        $input = file_get_contents('php://input');
        file_put_contents($packagesFile, $input);
        echo json_encode(["status" => "success", "message" => "Packages saved successfully", "synced_timestamp" => time() * 1000]);
        exit;
    }
}

if (strpos($request_uri, 'payments.json') !== false) {
    if ($method === 'GET') {
        echo file_exists($paymentsFile) ? file_get_contents($paymentsFile) : json_encode([]);
        exit;
    } elseif ($method === 'POST') {
        $input = file_get_contents('php://input');
        file_put_contents($paymentsFile, $input);
        echo json_encode(["status" => "success", "message" => "Payments saved successfully", "synced_timestamp" => time() * 1000]);
        exit;
    }
}

if (strpos($request_uri, 'sync_full.json') !== false) {
    if ($method === 'POST') {
        $input = file_get_contents('php://input');
        file_put_contents($syncFullFile, $input);
        echo json_encode(["status" => "success", "message" => "Full sync saved successfully", "synced_timestamp" => time() * 1000]);
        exit;
    }
}

echo json_encode(["status" => "error", "message" => "Invalid endpoint"]);
?>
