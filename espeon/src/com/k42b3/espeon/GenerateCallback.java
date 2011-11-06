package com.k42b3.espeon;

import java.util.ArrayList;
import java.util.HashMap;

public interface GenerateCallback 
{
	public void onGenerate(ArrayList<String> templates, HashMap<String, HashMap<String, Object>> tables) throws Exception;
}
