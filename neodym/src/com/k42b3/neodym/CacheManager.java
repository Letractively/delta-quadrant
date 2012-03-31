package com.k42b3.neodym;

import java.util.ArrayList;
import java.util.Date;

/**
 * Manages the cache objects
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class CacheManager extends ArrayList<Cache>
{
	private boolean disabled = false;

	/**
	 * Returns the cache if an cache object with this key exists and the cache
	 * ist not expired. If no cache object exists null is returned
	 * 
	 * @param String key
	 * @return Cache
	 */
	public Cache get(String key)
	{
		if(this.disabled)
		{
			return null;
		}

		for(int i = 0; i < this.size(); i++)
		{
			if(this.get(i).getKey().equals(key))
			{
				// check whether expired
				if(this.get(i).getExpire().compareTo(new Date()) < 0)
				{
					return null;
				}

				return this.get(i);
			}
		}

		return null;
	}

	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
}
