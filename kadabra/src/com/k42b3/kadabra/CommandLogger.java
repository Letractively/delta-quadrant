/**
 * Kadabra
 * 
 * Kadabra is an application to mirror a local folder to an FTP server.
 * You can create multiple projects wich are stored in an SQLite database.
 * With the option --status [id] you can see wich changes are made and
 * with --release [id] you can upload the changes to the FTP server.
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

package com.k42b3.kadabra;

import java.util.logging.Logger;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;

/**
 * CommandLogger
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class CommandLogger implements ProtocolCommandListener
{
	private Logger logger;

	public CommandLogger()
	{
		this.logger = Logger.getLogger("com.k42b3.kadabra");
	}

	public void protocolCommandSent(ProtocolCommandEvent e) 
	{
		logger.fine(e.getMessage());
	}

	public void protocolReplyReceived(ProtocolCommandEvent e) 
	{
		logger.fine(e.getMessage());
	}
}
