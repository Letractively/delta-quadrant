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

package com.k42b3.oat.filter.response;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;

import com.k42b3.oat.filter.ResponseFilterAbstract;
import com.k42b3.oat.http.Response;

/**
 * Gzip
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Gzip extends ResponseFilterAbstract
{
	public void exec(Response response) throws Exception
	{
		if(response.getHeader().containsKey("Content-Encoding"))
		{
			String encoding = response.getHeader().get("Content-Encoding");

			if(encoding.indexOf("gzip") != -1)
			{
				GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(response.getRawBody().getArray()));

				BufferedReader br = new BufferedReader(new InputStreamReader(gis));


				// decode
				StringBuilder buffer = new StringBuilder();
				char[] buf = new char[512];
				int len;
				
				while((len = br.read(buf)) > 0)
				{
					buffer.append(buf, 0, len);
				}


				response.setBody(buffer.toString());
			}
		}
	}
}
