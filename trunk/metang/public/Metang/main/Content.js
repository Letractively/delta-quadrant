/*
 *  $Id$
 *
 * metang
 * An web application to access the API of amun.
 *
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of metang. metang is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * metang is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with metang. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Metang.main.Content
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   javascript
 * @version    $Revision$
 */
Ext.define('Metang.main.Content', {
	extend: 'Ext.panel.Panel',

	services: [],
	pos: 0,

	initComponent: function(){

		var config = {

			id: 'content',
			region: 'center',
			layout: 'card',
			margins: '0 0 0 0',
			border: false

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Content.superclass.initComponent.apply(this, arguments);

	},

	addContainer: function(ns, obj){

		this.services.push({

			ns: ns,
			pos: this.pos

		});

		this.getLayout().setActiveItem(obj);

		return this.pos++;
	},

	getPos: function(ns){

		for(var i = 0; i < this.services.length; i++)
		{
			if(this.services[i].ns == ns)
			{
				return this.services[i].pos;
			}
		}

		return false;

	},

	getContainer: function(ns){

		var items = this.getLayout().getLayoutItems();
		var pos = this.getPos(ns);

		if(pos !== false)
		{
			this.getLayout().setActiveItem(pos);

			return items[pos];
		}
		else
		{
			return false;
		}

	}

});
