<?php

class login extends PSX_ModuleAbstract
{
	private $validate;
	private $session;

	public function onLoad()
	{
		$this->validate = new PSX_Validate();

		$this->session = new PSX_Session('metang', $this->validate);

		$this->template->set('login.tpl');
	}

	public function onPost()
	{
		try
		{
			$http  = new PSX_Http(new PSX_Http_Handler_Curl());
			$oauth = new PSX_Oauth($http);

			$url      = new PSX_Url($this->config['metang_url'] . $this->config['metang_request']);
			$callback = $this->config['psx_url'] . '/' . $this->config['psx_dispatch'] . 'callback';
			$response = $oauth->requestToken($url, $this->config['metang_consumer_key'], $this->config['metang_consumer_secret'], 'HMAC-SHA1', $callback);

			$token       = $response->getToken();
			$tokenSecret = $response->getTokenSecret();

			if(!empty($token) && !empty($tokenSecret))
			{
				// @todo save token and token secret probably in file
				// or db
				$this->session->set('token', $token);
				$this->session->set('tokenSecret', $tokenSecret);
				$this->session->set('authed', true);


				// send redirect url
				$url = new PSX_Url($this->config['metang_url'] . $this->config['metang_authorization']);
				$url->addParam('oauth_token', $token);

				$resp = array(

					'success' => true,
					'message' => 'Request successful',
					'url'     => (string) $url

				);

				echo json_encode($resp);
			}
			else
			{
				$response = PSX_Http_Response::convert($http->getResponse());

				throw new PSX_Exception($response->getBody());
			}
		}
		catch(Exception $e)
		{
			$resp = array(

				'success' => false,
				'message' => $e->getMessage(),

			);

			echo json_encode($resp);
		}
	}
}


