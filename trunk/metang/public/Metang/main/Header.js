
Ext.define('Metang.main.Header', {
	extend: 'Ext.Panel',

	initComponent: function(){

		var tbar = [];

		tbar.push(this.buildMenu('http://ns.amun-project.org/2011/amun/content'));
		tbar.push(this.buildMenu('http://ns.amun-project.org/2011/amun/system'));
		tbar.push(this.buildMenu('http://ns.amun-project.org/2011/amun/user'));
		tbar.push(this.buildMenu('http://ns.amun-project.org/2011/amun/service'));
		tbar.push(this.buildHelpMenu());
		tbar.push('->');
		tbar.push(this.buildStatus());
		tbar.push('-');
		tbar.push(this.buildLogout());


		var config = {

			title: 'metang (' + metang_version + ')',
			html: '',
			region: 'north',
			height: 63,
			margins: '5 0 5 0',
			border: false,
			tbar: tbar

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Header.superclass.initComponent.apply(this, arguments);

	},

	buildMenu: function(baseUrl){

		var baseDeep = this.charCount('/', baseUrl) + 1;
		var baseTitle = baseUrl.substring(baseUrl.lastIndexOf("/") + 1);
		baseTitle = (baseTitle.charAt(0) + "").toUpperCase() + baseTitle.substring(1).toLowerCase();

		var services = Metang.main.Services.services;
		var menu = {

			text: baseTitle,
			scale: 'medium',
			menu: []

		};

		for(var i = 0; i < services.length; i++)
		{
			var item = services[i];
			var type = item.type;

			if(type.substring(0, baseUrl.length) == baseUrl)
			{
				var deep = this.charCount('/', type);
				var title = type.substring(type.lastIndexOf("/") + 1);
				title = (title.charAt(0) + "").toUpperCase() + title.substring(1).toLowerCase();

				if(deep == baseDeep)
				{
					var childMenu = this.buildMenu(type);

					if(childMenu.menu.length == 0)
					{
						menu.menu.push({

							text: title,
							iconCls: title,
							handler: function(){

								Metang.main.Instance.handlerContentLoader(item.uri);

							}

						});
					}
					else
					{
						/*
						var tmpMenu = [{

							text: title,
							iconCls: title,
							handler: function(){

								Metang.main.Instance.handlerContentLoader(item.uri);

							}

						}];

						childMenu.menu = tmpMenu.concat(childMenu.menu);
						*/

						menu.menu.push(childMenu);
					}
				}
			}

		}

		return menu;

	},

	buildHelpMenu: function(){

		return {

			text: 'Help',
			scale: 'medium',
			menu: [{

				text: 'About',
				iconCls: 'about',
				handler: function(){

					Ext.Msg.show({

						title: 'About',
						msg: 'metang (' + metang_version + ')',
						buttons: Ext.Msg.OK,
						icon: Ext.MessageBox.INFO

					});

				}

			},{

				text: 'Report bug',
				iconCls: 'bug',
				handler: function(){

					window.location = 'mailto:phpsx@googlegroups.com';

				}

			},{

				text: 'Donate',
				iconCls: 'donate',
				handler: function(){

					window.location = 'https://sourceforge.net/project/project_donations.php?group_id=220002';

				}

			},{

				text: 'Website',
				iconCls: 'website',
				handler: function(){

					window.location = 'http://amun.phpsx.org';

				}

			}]

		};

	},

	buildStatus: function(){

		return {

			id: 'status',
			xtype: 'label',
			html: 'Logged in as <a href="' + Metang.main.Util.user.profileUrl + '">' + Metang.main.Util.user.name + '</a> (' + Metang.main.Util.user.group + ') ' + Metang.main.Util.user.timezone

		};

	},

	buildLogout: function(){

		return {

			text: 'Logout',
			iconCls: 'logout',
			handler: function(){

				var uri = Metang.main.Services.find('http://ns.amun.org/2010/amun/my');

				if(uri !== false)
				{
					Ext.Ajax.request({

						url: Metang.main.Util.getProxyUrl(uri + '/endSession?format=json'),
						method: 'GET',
						scope: this,
						success: function(response){

							window.location = metang_url + 'login';

						}

					});
				}

			}

		};

	},

	charCount: function(c, content){

		var j = 0;

		for(var i = 0; i < content.length; i++)
		{
			if(content.charAt(i) == c)
			{
				j++;
			}
		}

		return j;

	}

});
