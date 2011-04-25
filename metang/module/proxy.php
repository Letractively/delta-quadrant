<?php

class proxy extends PSX_ModuleAbstract
{
	public function onLoad()
	{
		try
		{
			$url = isset($_GET['url']) ? trim($_GET['url']) : '';
			$url = filter_var($url, FILTER_VALIDATE_URL);

			unset($_GET['url']);

			if(empty($url))
			{
				throw new Exception('Invalid url');
			}

			$url = urldecode($url);

			if(isset($_SESSION['authed']) && $_SESSION['authed'] === true)
			{
				$request_headers = get_request_header();

				$body = trim(get_raw_input());
				$body = !empty($body) ? $body : false;

				$root = new psx_type_url(AMUN_URL);
				$url  = new psx_type_url($url);


				// check host
				if(strcasecmp($root->get_host(), $url->get_host()) != 0)
				{
					throw new Exception('URL has an invalid host');
				}


				// add params
				foreach($_GET as $k => $v)
				{
					$url->add_param($k, $v);
				}


				$http = new psx_http(new psx_http_handler_curl());

				$query = http_build_query($url->get_query());
				$query = !empty($query) ? '?' . $query : '';

				$headers = array(

					$_SERVER['REQUEST_METHOD'] . ' ' . $url->get_path() . '' . $query . ' HTTP/1.1',
					'Host: ' . $url->get_host(),
					'Accept: */*',
					'User-Agent: amun backend (v 0.0.1)',
					'Authorization: ' . build_oauth_header($url, $request_headers),

				);

				if(isset($request_headers['content-type']))
				{
					$headers[] = 'Content-type: ' . $request_headers['content-type'];
				}

				if($body !== false)
				{
					$headers[] = 'Content-length: ' . strlen($body);
				}

				if(isset($request_headers['x-http-method-override']))
				{
					$headers[] = 'X-Http-Method-Override: ' . $request_headers['x-http-method-override'];
				}


				$response   = $http->request($url, $headers, $body);
				$last_error = $http->get_last_error();

				/*
				$h = fopen('log.txt', 'a+');
				fwrite($h, $http->get_request() . "\n" . $http->get_response() . "\n\n");
				fclose($h);
				*/

				if(empty($last_error))
				{
					$response = psx_http::parse_response($response);


					header($response['scheme'] . ' ' . $response['code'] . ' ' . $response['message']);

					foreach($response['header'] as $k => $v)
					{
						header($k . ': ' . $v);
					}


					echo $response['body'];
				}
				else
				{
					throw new Exception($last_error);
				}
			}
			else
			{
				throw new Exception('Not authenticated');
			}


			// clear the session where the access token is stored
			if(isset($_GET['logout']))
			{
				$_SESSION['token']        = '';
				$_SESSION['token_secret'] = '';
				$_SESSION['authed']       = false;
			}
		}
		catch(Exception $e)
		{
			echo json_encode(array('success' => false, 'message' => $e->getMessage()));
		}
	}
}

require_once('config.php');




function build_oauth_header(psx_type_url $url, array $request_headers)
{
	$token        = isset($_SESSION['token']) ? $_SESSION['token'] : false;
	$token_secret = isset($_SESSION['token_secret']) ? $_SESSION['token_secret'] : false;
	$method       = 'HMAC-SHA1';

	if(empty($token) || empty($token_secret))
	{
		throw new Exception('Token not set');
	}

	$values = array(

		'oauth_consumer_key'     => AMUN_CONSUMER_KEY,
		'oauth_token'            => $token,
		'oauth_signature_method' => $method,
		'oauth_timestamp'        => psx_net_oauth::get_timestamp(),
		'oauth_nonce'            => psx_net_oauth::get_nonce(),
		'oauth_version'          => psx_net_oauth::get_version(),

	);


	// build the base string
	$request_method = isset($request_headers['x-http-method-override']) ? $request_headers['x-http-method-override'] : $_SERVER['REQUEST_METHOD'];

	$base_string = psx_net_oauth::build_basestring($request_method, strval($url), array_merge($values, $url->get_params()));


	// get the signature object
	$signature = psx_net_oauth::get_signature($method);


	// generate the signature
	$values['oauth_signature'] = $signature->build($base_string, AMUN_CONSUMER_SECRET, $token_secret);


	return 'OAuth realm="psx", ' . psx_net_oauth::build_auth_string($values);
}

function get_raw_input()
{
	return file_get_contents('php://input');
}

function get_request_header($key = false)
{
	static $headers;

	if(empty($headers))
	{
		if(function_exists('apache_request_headers'))
		{
			$headers = apache_request_headers();

			foreach($headers as $k => $v)
			{
				$k = strtolower($k);

				$headers[$k] = $v;
			}
		}
		else
		{
			foreach($_SERVER as $k => $v)
			{
				if(substr($k, 0, 5) == 'HTTP_')
				{
					$k = str_replace('_', '-', strtolower(substr($k, 5)));

					$headers[$k] = $v;
				}
			}
		}
	}

	if($key === false)
	{
		return $headers;
	}
	else
	{
		$key = strtolower($key);

		return isset($headers[$key]) ? $headers[$key] : false;
	}
}

