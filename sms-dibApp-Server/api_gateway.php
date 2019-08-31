<?php 
include 'functions.php';

error_reporting(E_ALL);

//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE); //convert JSON into array

if(isset($input[ACTION])){	
	switch($input[ACTION]) {
		case ACTION_LOGIN: 
			$response = login($input);
			break;
		case ACTION_GET_ROLE_LIST:
			$response = getRoleList();
			break;
		case ACTION_REGISTRATION:
			$response = registration($input);
			break;
		case ACTION_GET_USER_DETAILS: 
			$response = getUserDetails($input['email']);
			break;
		default: 
			$response = new Response(ACTION_NOT_DEFINED_TEXT, ACTION_NOT_DEFINED_CODE); 
			break;
	}
} else {
	$message = $input[ACTION] + " - " + ACTION_NOT_DEFINED_TEXT;
	$response = new Response($message, ACTION_NOT_DEFINED_CODE); 
}
echo json_encode($response);

?>