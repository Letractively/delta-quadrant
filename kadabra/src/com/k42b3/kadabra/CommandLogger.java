package com.k42b3.kadabra;

import java.util.logging.Logger;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;

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
