package com.k42b3.espeon.cmd;

import com.k42b3.espeon.ConnectCallback;
import com.k42b3.espeon.Espeon;
import com.k42b3.espeon.GenerateCallback;
import com.k42b3.espeon.View;

public class Main implements View
{
	private Espeon inst;

	public Main(Espeon inst)
	{
		// @todo implement no gui mode i.e.
		// java -jar espeon.jar -h localhost -u root -p -d psx -t psx_* -m form.php,handler.php,table.php

		this.inst = inst;
	}

	public void setConnectCallback(ConnectCallback connectCb) 
	{
	}

	public void setGenerateCallback(GenerateCallback generateCb) 
	{
	}
}
