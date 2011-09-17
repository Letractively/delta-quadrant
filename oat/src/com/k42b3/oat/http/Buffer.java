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

package com.k42b3.oat.http;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Buffer
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Buffer 
{
	private byte[] buffer;
	private int pos = 0;

	public Buffer(int size)
	{
		this.buffer = new byte[size];
	}

	public void add(byte b)
	{
		if(pos == buffer.length)
		{
			resizeBuffer();
		}

		buffer[pos++] = b;
	}

	public void append(byte[] bytes)
	{
		for(int i = 0; i < bytes.length; i++)
		{
			add(bytes[i]);
		}
	}

	public void append(ByteBuffer byteBuffer)
	{
		for(int i = 0; i < byteBuffer.remaining(); i++)
		{
			add(byteBuffer.get(i));
		}
	}

	public void clear()
	{
		buffer = new byte[buffer.length];
		pos = 0;
	}

	public byte[] getArray()
	{
		byte[] tmpBuffer = new byte[pos + 1];

		for(int i = 0; i < tmpBuffer.length; i++)
		{
			tmpBuffer[i] = buffer[i];
		}

		return tmpBuffer;
	}

	public int getSize()
	{
		return pos;
	}

	public ByteBuffer getByteBuffer()
	{
		ByteBuffer bufferTemp = ByteBuffer.allocateDirect(this.getSize());

		bufferTemp.put(buffer, 0, pos);

		bufferTemp.flip();
		
		return bufferTemp;
	}

	/**
	 * Reads from the last position to the next \n and converts the hex value to 
	 * an integer
	 * 
	 * @return integer
	 */
	public int getChunkSize()
	{
		if(this.containsRN())
		{
			// find \n
			int e = pos - 3;
			int s = e;

			while(s > 0 && buffer[s] != 0xA)
			{
				s--;
			}


			// read value
			int j = 0;
			byte[] tmpBuffer = new byte[32];

			for(int i = s; i <= e && j < tmpBuffer.length; i++)
			{
				tmpBuffer[j] = buffer[i];

				j++;
			}


			// decode string
			String raw = new String(tmpBuffer, Charset.forName("UTF-8"));

			if(!raw.trim().isEmpty())
			{
				int len = Integer.decode("0x" + raw.trim());

				return len;
			}
		}

		return 0;
	}

	/**
	 * Checks whether at the end of the buffer contains:
	 * \r\n
	 * 
	 * @return boolean
	 */
	public boolean containsRN()
	{
		if(pos > 2)
		{
			return  buffer[pos - 2] == 0xD && 
					buffer[pos - 1] == 0xA;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Checks whether at the end of the buffer contains:
	 * \r\n\r\n
	 * 
	 * @return boolean
	 */
	public boolean containsRNRN()
	{
		if(pos > 4)
		{
			return  buffer[pos - 4] == 0xD && 
					buffer[pos - 3] == 0xA && 
					buffer[pos - 2] == 0xD &&
					buffer[pos - 1] == 0xA;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Checks whether at the end of the buffer contains:
	 * 0\r\n\r\n
	 * 
	 * @return boolean
	 */
	public boolean contains0RNRN()
	{
		if(pos > 5)
		{
			return buffer[pos - 5] == 0x30 && 
					buffer[pos - 4] == 0xD && 
					buffer[pos - 3] == 0xA && 
					buffer[pos - 2] == 0xD &&
					buffer[pos - 1] == 0xA;
		}
		else
		{
			return false;
		}
	}

	private void resizeBuffer()
	{
		byte[] tmpBuffer = new byte[buffer.length * 2];

		for(int i = 0; i < buffer.length; i++)
		{
			tmpBuffer[i] = buffer[i];
		}

		buffer = tmpBuffer;
	}
}
