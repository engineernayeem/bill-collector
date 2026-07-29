<?php
/**
 * ISP Bill Collector - cPanel REST Sync API
 * 
 * Upload this file (api.php) to your cPanel hosting folder (e.g. public_html/api/api.php)
 * Make sure the folder is writable (permissions 755 or 777) so PHP can write JSON files.
 */

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle pre-flight CORS requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

$request_uri = $_SERVER['REQUEST_URI'];

if (strpos($request_uri, 'customers.json') !== false) {
    $file = 'customers.json';
} elseif (strpos($request_uri, 'packages.json') !== false) {
    $file = 'packages.json';
} elseif (strpos($request_uri, 'payments.json') !== false) {
    $file = 'payments.json';
} else {
    $file = 'sync_full.json';
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (file_exists($file)) {
        echo file_get_contents($file);
    } else {
        echo json_encode([]);
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $input = file_get_contents('php://input');
    if (!empty($input)) {
        file_put_contents($file, $input);
        echo json_encode([
            "success" => true,
            "message" => "Data synced successfully to cPanel server file ($file)",
            "timestamp" => time()
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "No JSON payload provided in request body"
        ]);
    }
} else {
    echo json_encode(["error" => "Method not supported"]);
}
?>
