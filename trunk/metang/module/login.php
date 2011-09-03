<?php
/*
 *  $Id$
 *
 * metang
 * An web application to access the API of amun.
 *
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of metang. metang is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * metang is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with metang. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * login
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   module
 * @version    $Revision$
 */
class login extends PSX_ModuleAbstract
{
	private $validate;
	private $session;

	public function onLoad()
	{
		$this->validate = new PSX_Validate();

		$this->session = new PSX_Session('metang', $this->validate);
	}

	public function checkAuth()
	{
		$resp = false;

		if($this->session->authed == false)
		{
			$file = PSX_PATH_CACHE . '/credentials.ser';

			if(PSX_File::isFile($file))
			{
				$content = file_get_contents($file);
				$content = unserialize($content);

				if(is_array($content) && isset($content['token']) && isset($content['tokenSecret']))
				{
					$this->session->set('token', $content['token']);
					$this->session->set('tokenSecret', $content['tokenSecret']);
					$this->session->set('authed', true);

					$resp = true;
				}
			}
		}
		else
		{
			$resp = true;
		}

		echo PSX_Json::encode($resp);
		exit;
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


