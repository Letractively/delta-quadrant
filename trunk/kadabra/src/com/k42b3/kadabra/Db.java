package com.k42b3.kadabra;

import java.io.File;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class Db
{
	private static Db instance;

	private SQLiteConnection db;

	private Db() throws SQLiteException
	{
		db = new SQLiteConnection(new File("projects"));
		db.open(true);
	}

	public SQLiteStatement query(String sql) throws SQLiteException
	{
		return db.prepare(sql);
	}

	public void exec(String sql) throws SQLiteException
	{
		db.exec(sql);
	}

	public static Db getInstance() throws SQLiteException
	{
		if(Db.instance == null)
		{
			Db.instance = new Db();
		}

		return Db.instance;
	}
}
