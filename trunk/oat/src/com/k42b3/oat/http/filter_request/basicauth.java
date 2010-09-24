/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.oat.http.filter_request;

import java.util.Properties;

import sun.misc.BASE64Encoder;

import com.k42b3.oat.irequest_filter;
import com.k42b3.oat.http.request;

/**
 * basicauth
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class basicauth implements irequest_filter
{
	private Properties config = new Properties();

	public void exec(request request) 
	{
		String user = this.config.getProperty("user");
		String pw = this.config.getProperty("pw");

		String auth = new BASE64Encoder().encode((user + ":" + pw).getBytes());

		request.set_header("Authorization", "Basic " + auth);
	}

	public void set_config(Properties config)
	{
		this.config = config;
	}
}
