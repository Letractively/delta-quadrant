/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of tajet. tajet is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * tajet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with tajet. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.oat.http.encoder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import com.k42b3.oat.http.iencoder;

public class gzip implements iencoder
{
	public String decode(String encoded) throws Exception
	{
		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(encoded.getBytes()));

		BufferedReader br = new BufferedReader(new InputStreamReader(gis));


		// decode
		StringBuilder buffer = new StringBuilder();
		char[] buf = new char[512];
		
		while(br.read(buf) > 0)
		{
			buffer.append(buf);
		}
		
		
		return buffer.toString();
	}
}
