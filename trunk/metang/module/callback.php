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
 * callback
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   module
 * @version    $Revision$
 */
class callback extends PSX_ModuleAbstract
{
	public function onLoad()
	{
		$this->validate = new PSX_Validate();

		$this->session = new PSX_Session('metang', $this->validate);

		$this->get = new PSX_Get($this->validate);
	}

	public function onGet()
	{
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
				// save to file
				$file    = PSX_PATH_CACHE . '/credentials.ser';
				$content = serialize(array(

					'token'       => $token,
					'tokenSecret' => $tokenSecret

				));

				file_put_contents($file, $content);


				// write to session
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


