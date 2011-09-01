
Ext.require('Ext.data.Store');
Ext.require('Metang.main.Services');
Ext.require('Metang.main.ServiceItem');

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

				url: Metang.main.Util.getProxyUrl(amun_url + 'api/meta/xrds'),
				method: 'GET',
				scope: this,
				disableCaching: true,
				success: function(response){

					var contentType = response.getResponseHeader('Content-Type')

					if(contentType == 'application/xrds+xml')
					{
						var startIndex = response.responseText.indexOf('<XRD>');
						var length = response.responseText.indexOf('</XRD>');

						if(startIndex > 0 && length > 0)
						{
							Metang.main.Util.xrds = response.responseText.substring(startIndex, length + 6);

							Metang.main.Util.fetchAmunServices();
						}
						else
						{
							Ext.Msg.alert('Error', 'Invalid XRDS file');
						}
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

