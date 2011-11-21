package com.k42b3.kadabra.record;

import com.almworks.sqlite4java.SQLiteStatement;
import com.k42b3.kadabra.Db;

public class Release extends Record
{
	private int id;
	private int projectId;
	private String date;

	public Release() throws Exception
	{
		super();
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getProjectId()
	{
		return projectId;
	}

	public void setProjectId(int projectId)
	{
		this.projectId = projectId;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public void insert() throws Exception
	{
		String sql = "INSERT INTO " +
			"releases " +
		"SET " +
			"projectId = ?, " +
			"date = NOW()";

		SQLiteStatement st = Db.getInstance().query(sql);

		st.bind(1, this.getProjectId());

		st.step();
	}

	public void update() throws Exception
	{
		String sql = "UPDATE " +
			"releases " +
		"SET " +
			"projectId = ?, " +
			"date = ? " +
		"WHERE " +
			"id = " + this.getId();

		SQLiteStatement st = Db.getInstance().query(sql);

		st.bind(1, this.getProjectId());
		st.bind(2, this.getDate());

		st.step();
	}

	public void delete() throws Exception
	{
		String sql = "DELETE FROM " +
			"releases " +
		"WHERE " +
			"id = " + this.getId();

		Db.getInstance().exec(sql);
	}

	public static Release getReleaseById(int id) throws Exception
	{
		String sql = "SELECT " +
			"id, " +
			"projectId, " +
			"date " +
		"FROM " +
			"releases " +
		"WHERE " +
			"id = " + id;

		SQLiteStatement st = Db.getInstance().query(sql);

		st.step();

		if(st.hasRow())
		{
			Release release = new Release();

			release.id = st.columnInt(0);
			release.projectId = st.columnInt(1);
			release.date = st.columnString(2);

			return release;
		}
		else
		{
			throw new Exception("Invalid release id");
		}
	}
	
	public static void setupTable() throws Exception
	{
		String sql = "CREATE TABLE IF NOT EXISTS releases (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"projectId INTEGER," +
			"date DATETIME" +
		")";

		Db.getInstance().exec(sql);
	}
}
