/**
 * oat
 * 
 * An application to send raw http requests to any host. It is designed to
 * debug and test web applications. You can apply filters to the request and
 * response wich can modify the content.
 * 
 * Copyright (c) 2010, 2011 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of oat. oat is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * oat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with oat. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.oat.filter.request;

import java.util.Properties;

import sun.misc.BASE64Encoder;

import com.k42b3.oat.filter.RequestFilterInterface;
import com.k42b3.oat.http.Request;

/**
 * BasicAuth
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class BasicAuth implements RequestFilterInterface
{
	private Properties config = new Properties();

	public void exec(Request request)
	{
		String user = this.config.getProperty("user");
		String pw = this.config.getProperty("pw");

		String auth = new BASE64Encoder().encode((user + ":" + pw).getBytes());

		request.setHeader("Authorization", "Basic " + auth);
	}

	public void setConfig(Properties config)
	{
		this.config = config;
	}
}
