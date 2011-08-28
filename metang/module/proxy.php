<?php

class proxy extends PSX_ModuleAbstract
{
	private $validate;
	private $session;
	private $get;

	public function onLoad()
	{
		$this->validate = new PSX_Validate();

		$this->session = new PSX_Session('metang', $this->validate);

		$this->get = new PSX_Get($this->validate);

		try
		{
			$url = $this->get->url('string', array(new PSX_Filter_Length(7, 256), new PSX_Filter_Url(), new PSX_Filter_Urldecode()));

			unset($_GET['url']);

			if(empty($url))
			{
				throw new PSX_Exception('Invalid url');
			}

			if($this->session->authed === true)
			{
				$requestHeaders = PSX_Config::getRequestHeader();

				$body = trim(PSX_Config::getRawInput());
				$body = !empty($body) ? $body : false;

				$root = new PSX_Url($this->config['metang_url']);
				$url  = new PSX_Url($url);


				// check host
				if(strcasecmp($root->getHost(), $url->getHost()) != 0)
				{
					throw new PSX_Exception('URL has an invalid host');
				}


				// add params
				foreach($_GET as $k => $v)
				{
					$url->addParam($k, $v);
				}


				$http = new PSX_Http(new PSX_Http_Handler_Curl());
				$oauth = new PSX_Oauth($http);

				$query = http_build_query($url->getQuery());
				$query = !empty($query) ? '?' . $query : '';

				$token       = $this->session->token;
				$tokenSecret = $this->session->tokenSecret;

				$headers = array(

					'User-Agent' => 'metang ' . $this->config['metang_version'],
					'Authorization' => $oauth->getAuthorizationHeader($url, $this->config['metang_consumer_key'], $this->config['metang_consumer_secret'], $token, $tokenSecret, 'HMAC-SHA1', $_SERVER['REQUEST_METHOD']),

				);

				if(isset($requestHeaders['content-type']))
				{
					$headers['Content-type'] = $requestHeaders['content-type'];
				}

				if($body !== false)
				{
					$headers['Content-Length'] = strlen($body);
				}

				if(isset($requestHeaders['x-http-method-override']))
				{
					$headers['X-Http-Method-Override'] = $requestHeaders['x-http-method-override'];
				}


				switch($_SERVER['REQUEST_METHOD'])
				{
					case 'GET':

						$request = new PSX_Http_GetRequest($url, $headers);

						break;

					case 'POST':

						$request = new PSX_Http_PostRequest($url, $headers, $body);

						break;

					case 'PUT':

						$request = new PSX_Http_PutRequest($url, $headers, $body);

						break;

					case 'DELETE':

						$request = new PSX_Http_DeleteRequest($url, $headers, $body);

						break;

					default:

						break;
				}


				$response  = $http->request($request);
				$lastError = $http->getLastError();

				/*
				$h = fopen(PSX_PATH_CACHE . '/log.txt', 'a+');
				fwrite($h, $http->getRequest() . "\n" . $http->getResponse() . "\n\n");
				fclose($h);
				*/

				if(empty($lastError))
				{
					header($response->getScheme() . ' ' . $response->getCode() . ' ' . $response->getMessage());

					foreach($response->getHeader() as $k => $v)
					{
						header($k . ': ' . $v);
					}


					echo $response->getBody();
				}
				else
				{
					throw new Exception($lastError);
				}
			}
			else
			{
				throw new PSX_Exception('Not authenticated');
			}


			// clear the session where the access token is stored
			if($this->get->logout('string'))
			{
				$this->session->token       = null;
				$this->session->tokenSecret = null;
				$this->session->authed      = false;
			}
		}
		catch(Exception $e)
		{
			echo json_encode(array('success' => false, 'message' => $e->getMessage()));
		}
	}
}

