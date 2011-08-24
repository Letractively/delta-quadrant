<?php

class callback extends PSX_ModuleAbstract
{
	public function onLoad()
	{
		$this->validate = new PSX_Validate();

		$this->session = new PSX_Session('metang', $this->validate);

		$this->get = new PSX_Get($this->validate);

		try
		{
			$http  = new PSX_Http(new PSX_Http_Handler_Curl());
			$oauth = new PSX_Oauth($http);

			$token       = $this->session->token;
			$tokenSecret = $this->session->tokenSecret;
			$verifier    = $this->get->oauth_verifier('string');
			$error       = $this->get->x_oauth_error('string');

			if(!empty($error))
			{
				throw new PSX_Exception('Request was denied');
			}

			if(empty($verifier))
			{
				throw new PSX_Exception('Verifier not set');
			}

			$url = new PSX_Url($this->config['metang_url'] . $this->config['metang_access']);

			$response = $oauth->accessToken($url, $this->config['metang_consumer_key'], $this->config['metang_consumer_secret'], $token, $tokenSecret, $verifier);

			$token       = $response->getToken();
			$tokenSecret = $response->getTokenSecret();

			if(!empty($token) && !empty($tokenSecret))
			{
				// @todo save token and token secret probably in file
				// or db
				$this->session->set('token', $token);
				$this->session->set('tokenSecret', $tokenSecret);
				$this->session->set('authed', true);


				// redirect to url
				header('Location: ' . $this->config['psx_url']);

				exit;
			}
			else
			{
				throw new PSX_Exception('Token or token secret not set');
			}
		}
		catch(Exception $e)
		{
			header('HTTP/1.1 500 Internal Server Error');
			header('Content-type: text/plain');

			echo $e->getMessage();
		}
	}
}


