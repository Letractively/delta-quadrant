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

Ext.require('Ext.data.Store');
Ext.require('Metang.main.Services');
Ext.require('Metang.main.ServiceItem');

/**
 * Metang.main.Util
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   javascript
 * @version    $Revision$
 */
Ext.define('Metang.main.Util', {

	constructor: function(config) {
		this.initConfig(config);

		return this;
	},

	statics: {

		user: null,
		xrds: null,

		getProxyUrl: function(url, params)
		{
			var param = '';

			for(k in params)
			{
				param+= '&' + k + '=' + encodeURIComponent(params[k]);
			}

			return metang_url + 'proxy?url=' + encodeURIComponent(url) + param;
		},

		checkAuth: function(){

			Ext.Ajax.request({

				url: metang_url + 'login/checkAuth',
				method: 'GET',
				scope: this,
				disableCaching: true,
				success: function(response){

					var resp = Ext.JSON.decode(response.responseText);

					if(resp)
					{
						this.fetchAmunServices();
					}
					else
					{
						// load login ui
						Ext.create('Metang.main.Login');
					}

				},
				failure: function(response){

					Ext.Msg.alert('Error', response.responseText);

				}

			});

		},

		fetchAmunServices: function(){

			// get available services
			var services = Ext.create('Ext.data.Store', {

				autoLoad: false,
				model: 'Metang.main.ServiceItem',
				proxy: {

					type: 'ajax',
					url: Metang.main.Util.getProxyUrl(amun_url + 'api/meta/xrds'),
					reader: {

						type: 'xml',
						root: 'XRD',
						record: 'Service'

					}

				}

			});

			services.on('load', function(){

				// add discovered services
				services.each(function(rec){

					Metang.main.Services.add(rec.get('Type'), rec.get('URI'));

				});

				// find my service
				var uri = Metang.main.Services.find('http://ns.amun-project.org/2011/amun/service/my');

				if(uri !== false)
				{
					// check whether user is logged in
					Ext.Ajax.request({

						url: Metang.main.Util.getProxyUrl(uri + '/verifyCredentials?format=json'),
						method: 'GET',
						scope: this,
						disableCaching: true,
						success: function(response){

							Metang.main.Util.user = Ext.JSON.decode(response.responseText);

							Metang.main.Util.doLogin();

						},
						failure: function(response){

							Ext.Msg.alert('Error', 'Couldnt connect to API.');

						}

					});
				}
				else
				{
					Ext.Msg.alert('Error', 'Remote API has not the "my" service installed');
				}

			});

			services.load();

		},

		doLogin: function(){

			if(Metang.main.Util.user.loggedIn == true && Metang.main.Util.user.status == 'Administrator')
			{
				// load ui
				var layout = Ext.create('Metang.main.Layout');

				layout.handlerContentLoader('http://ns.amun-project.org/2011/amun/content/page');


				// get available services and load depending js
				/*
				var uri = Metang.main.Services.find('http://ns.amun-project.org/2011/amun/content/service');

				if(uri !== false)
				{
					Ext.Ajax.request({

						url: Metang.main.Util.getProxyUrl(uri),
						method: 'GET',
						scope: this,
						disableCaching: true,
						success: function(response){

							var resp  = Ext.JSON.decode(response.responseText);
							var entry = Ext.util.Format.defaultValue(resp.entry, []);

							if(entry.length > 0)
							{
								var url = metang_url + 'loader?js=';

								for(var i = 0; i < entry.length; i++)
								{
									url+= 'service_' + entry[i].name + '|';
								}

								Ext.core.DomHelper.append(Ext.core.DomQuery.selectNode('head'), {

									tag: 'script',
									src: url

								});
							}




						},
						failure: function(response){

							Ext.Msg.alert('Error', 'Couldnt request services.');

						}

					});
				}
				else
				{
					Ext.Msg.alert('Error', 'Couldnt request services.');
				}
				*/
			}
			else
			{
				Ext.Msg.alert('Error', 'Couldnt request services.');
			}

		}

	}

});

