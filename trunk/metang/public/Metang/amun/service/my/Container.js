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
 * Metang.basic.Container
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   javascript
 * @version    $Revision$
 */
Ext.define('Metang.amun.service.my.Container', {
	extend: 'Ext.panel.Panel',

	initComponent: function(){

		var config = {

			title: 'Content',
			region: 'center',
			margins: '0 0 0 0',
			border: true,
			layout: 'fit',
			items: []

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.amun.service.my.Container.superclass.initComponent.apply(this, arguments);

	},

	onLoad: function(uri){

		Ext.Ajax.request({

			url: Metang.main.Util.getProxyUrl(uri + '/verifyCredentials?format=json'),
			method: 'GET',
			scope: this,
			disableCaching: true,
			success: function(response){

				var resp = Ext.JSON.decode(response.responseText);
				var html = '';

				html+= '<div>';
				html+= '	<img src="' + resp.thumbnailUrl + '" style="float:left;" />';
				html+= '	<h1 style="float:left;font-size:2em;margin:8px;"><a href="' + resp.profileUrl + '">' + resp.name + '</a></h1>';
				html+= '</div>';

				var panel = Ext.create('Ext.panel.Panel', {

					border: false,
					bodyPadding: 8,
					html: html

				});

				this.add(panel);
				this.doLayout();

			},
			failure: function(response){

			}

		});

	}

});
