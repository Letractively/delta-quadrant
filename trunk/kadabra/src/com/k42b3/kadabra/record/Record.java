package com.k42b3.kadabra.record;

import com.k42b3.kadabra.Db;

public abstract class Record 
{
	protected Db db;

	public Record() throws Exception
	{
		this.db = Db.getInstance();
	}
	
	abstract public void insert() throws Exception;
	abstract public void update() throws Exception;
	abstract public void delete() throws Exception;
}
