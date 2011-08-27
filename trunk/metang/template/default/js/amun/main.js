/*
 *  $Id: main.js 207 2011-03-15 13:03:17Z k42b3.x $
 *
 * amun
 * A social content managment system based on the psx framework. For
 * the current version and informations visit <http://amun.phpsx.org>
 *
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of amun. amun is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * amun is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with amun. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * amun.main
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 207 $
 */

Ext.define('Metang.main.Header', {
	extend: 'Ext.Panel',

	initComponent: function(){

		var config = {

			title: 'Amun',
			html: '<div class="bg_header"></div>',
			minSize: 100,
			maxSize: 100,
			region: 'north',
			margins: '5 0 5 0',
			border: false,
			tbar: [{

				text: 'Content',
				scale: 'medium',
				menu: [{

					text: 'Page',
					iconCls: 'page',
					handler: function(){

						metang.main.instance.handlerContentLoader('content_page');

					}

				},{

					text: 'Gadget',
					iconCls: 'gadget',
					handler: function(){

						metang.main.instance.handlerContentLoader('content_gadget');

					}

				},{

					text: 'Service',
					iconCls: 'service',
					handler: function(){

						metang.main.instance.handlerContentLoader('content_service');

					}

				},{

					text: 'Media',
					iconCls: 'media',
					handler: function(){

						metang.main.instance.handlerContentLoader('content_media');

					}

				}]

			},{

				text: 'System',
				scale: 'medium',
				menu: [{

					text: 'API',
					iconCls: 'api',
					handler: function(){

						metang.main.instance.handlerContentLoader('system_api');

					}

				},{

					text: 'Approval',
					iconCls: 'approval',
					handler: function(){

						metang.main.instance.handlerContentLoader('system_approval');

					}

				},{

					text: 'Country',
					iconCls: 'country',
					handler: function(){

						metang.main.instance.handlerContentLoader('system_country');

					}

				},{

					text: 'Event',
					iconCls: 'event',
					handler: function(){

						metang.main.instance.handlerContentLoader('system_event');

					}

				},{

					text: 'Logging',
					iconCls: 'log',
					handler: function(){

						metang.main.instance.handlerContentLoader('system_log');

					}

				},{

					text: 'Vars',
					iconCls: 'vars',
					handler: function(){

						metang.main.instance.handlerContentLoader('system_vars');

					}

				}]

			},{

				text: 'User',
				scale: 'medium',
				menu: [{

					text: 'Account',
					iconCls: 'account',
					handler: function(){

						metang.main.instance.handlerContentLoader('user_account');

					}

				},{

					text: 'Activity',
					iconCls: 'activity',
					handler: function(){

						metang.main.instance.handlerContentLoader('user_activity');

					}

				},{

					text: 'Friend',
					iconCls: 'friend',
					handler: function(){

						metang.main.instance.handlerContentLoader('user_friend');

					}

				},{

					text: 'Group',
					iconCls: 'group',
					handler: function(){

						metang.main.instance.handlerContentLoader('user_group');

					}

				},{

					text: 'Level',
					iconCls: 'level',
					handler: function(){

						metang.main.instance.handlerContentLoader('user_level');

					}

				},{

					text: 'Right',
					iconCls: 'right',
					handler: function(){

						metang.main.instance.handlerContentLoader('user_right');

					}

				}]

			},{

				text: 'Help',
				scale: 'medium',
				menu: [{

					text: 'About',
					iconCls: 'about',
					handler: function(){

						Ext.Msg.show({

							title: 'About',
							msg: 'metang version 0.0.1',
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

			},'->',{

				id: 'status',
				xtype: 'label',
				text: 'connecting ... '

			},'-',{

				text: 'Logout',
				iconCls: 'logout',
				handler: function(){

					var uri = Metang.main.Services.find('http://ns.amun.org/2010/amun/my');
					var con = new Ext.data.Connection();

					if(uri !== false)
					{
						con.request({

							url: Metang.common.getProxyUrl(uri + '/endSession?format=json'),
							method: 'GET',
							scope: this,
							success: function(response){

								window.location = metang_url + 'login';

							}

						});
					}

				}

			}]

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Header.superclass.initComponent.apply(this, arguments);

	}

});


Ext.define('Metang.main.Nav', {
	extend: 'Ext.Panel',

	objWebsite: null,
	store: null,

	initComponent: function() {

		// website tree panel
		this.store = Ext.create('Ext.data.TreeStore', {

			root: {
				expanded: true,
				children: {}
			}

		});


		this.objWebsite = Ext.create('Ext.tree.TreePanel', {

			title: 'Website',
			store: this.store,
			collapsible: false,
			rootVisible: false,
			singleExpand: false,
			autoScroll: true,
			containerScroll: true,
			useArrows: false,
			enableDD: true,
			border: false

		});


		this.loadTree();


		var config = {

			title: 'Navigation',
			region: 'west',
			margins: '0 5 0 0',
			width: 200,
			border: true,
			layout: 'accordion',
			items: [this.objWebsite]

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Nav.superclass.initComponent.apply(this, arguments);

	},

	buildTree: function(node, entries){

		for(var i = 0; i < entries.length; i++)
		{
			var child = node.appendChild({

				id: entries[i].id,
				text: entries[i].text,
				iconCls: 'page'

			});

			if(typeof(entries[i].children) != 'undefined')
			{
				this.buildTree(node, entries[i].children);
			}
		}

	},

	loadTree: function(){

		var uri = Metang.main.Services.find('http://ns.amun-project.org/2011/amun/content/page');

		if(uri !== false)
		{
			this.objWebsite.getRootNode().removeAll();

			Ext.Ajax.request({

				url: Metang.main.Util.getProxyUrl(uri + '/buildTree?format=json'),
				method: 'GET',
				scope: this,
				disableCaching: true,
				success: function(response){

					var resp = Ext.JSON.decode(response.responseText);

					this.buildTree(this.store.getRootNode(), resp.entry);

				},
				failure: function(response){

					Ext.Msg.alert('Error', 'Couldnt load tree.');

				}

			});
		}

	}

});


Ext.define('Metang.main.Content', {
	extend: 'Ext.Panel',

	services: [],
	pos: 0,

	initComponent: function(){

		var config = {

			title: 'Content',
			region: 'center',
			layout: 'card',
			margins: '0 0 0 0',
			border: true

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Content.superclass.initComponent.apply(this, arguments);

	},

	addContainer: function(id, obj){

		this.services.push({

			id: id,
			pos: this.pos

		});

		this.add(obj);

		return this.pos++;
	},

	getContainer: function(id){

		for(var i = 0; i < this.services.length; i++)
		{
			if(this.services[i].id == id)
			{
				return this.services[i].pos;
			}
		}

		return false;

	}

});


Ext.define('Metang.main.Layout', {
	extend: 'Ext.Viewport',

	objHeader: null,
	objNav: null,
	objContent: null,

	initComponent: function() {

		this.objHeader  = Ext.create('Metang.main.Header');
		this.objNav     = Ext.create('Metang.main.Nav');
		this.objContent = Ext.create('Metang.main.Content');

		var config = {

			layout: 'border',
			items: [this.objHeader, this.objNav, this.objContent]

		};


		/*
		this.handlerWebsite();

		this.handlerService();
		*/

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Layout.superclass.initComponent.apply(this, arguments);

	},

	handlerWebsite: function(){

		this.objNav.get(0).on('click', function(n){

				/*
			// load page panel
			this.handlerContentLoader('content_page');

			// load data
			Metang.content.Page.storeGadget.load({

				params: {

					filterBy: 'id',
					filterOp: 'equals',
					filterValue: n.id

				}

			});
			Metang.content.Page.storeRight.load({

				params: {

					filterBy: 'id',
					filterOp: 'equals',
					filterValue: n.id

				}

			});

			// show edit page
			var obj = this.objContent.layout.activeItem;

			obj.panel_update.load_data(get_proxy_url(metang.content.page.url + '&filterBy=id&filterOp=equals&filterValue=' + n.id));

			obj.panel_update.set_active_tab(0);

			obj.layout.setActiveItem(2);
			*/

		}, this);

		/*
		this.o_nav.get(0).on('startdrag', function(tree, node, event){

			this.o_nav.get(0).oldPosition    = node.parentNode.indexOf(node);
			this.o_nav.get(0).oldNextSibling = node.nextSibling;

		}, this);

		this.o_nav.get(0).on('movenode', function(tree, node, oldParent, newParent, position){

			if(oldParent == newParent){

				var url    = amun_url + 'api/content/page/reorderPage?format=json';
				var params = {'pageId': node.id, 'delta': (position - this.o_nav.get(0).oldPosition)};

			} else {

				Ext.MessageBox.alert('Information', 'You can only change the parent of a node by editing the node');

				return false;

				var url    = amun_url + 'api/content/page/reparentPage?format=json';
				var params = {'pageId': node.id, 'parentId': newParent.id, 'position': position};

			}

			this.o_nav.get(0).disable();

			var con = new Ext.data.Connection();

			con.request({

				url: get_proxy_url(url),
				method: 'POST',
				jsonData: params,
				scope: this,
				headers: {

					'Content-type': 'application/json',
					'X-Http-Method-Override': 'POST'

				},
				success: function(response){

					var response = Ext.JSON.decode(response.responseText);

					if(response.success)
					{
						this.o_nav.get(0).enable();
					}
					else
					{
						this.o_nav.get(0).enable();

						Ext.MessageBox.alert('Error', response.message);
					}

				},
				failure: function(){

					this.o_nav.get(0).enable();

					Ext.MessageBox.alert('Error', 'Error while saving your changes');

				}

			});

		}, this);
		*/

	},

	handlerService: function(){

		this.o_nav.get(1).on('rowclick', function(grid, row, e){

			var data   = grid.getStore();
			var record = data.getAt(row);
			var name   = record.get('name');

			this.handlerContentLoader('service_' + name);

		}, this);

	},

	handlerContentLoader: function(key){

		return null;

		pos = this.objContent.getContainer(key);

		if(pos === false)
		{
			this.objContent.disable();

			var ns  = key.replace(/_/g, '.');
			var cls = 'Metang.' + ns + '.Panel';

			try
			{
				obj = Ext.create(cls);


				obj.addEvents('help', 'reloadtree');


				obj.on('added', function(obj, ownerCt, index){

					this.objContent.layout.setActiveItem(index);

					this.objContent.enable();

				}, this);

				obj.on('help', function(id){

					this.objAbout.showHelp(id);

				}, this);

				obj.on('reloadtree', function(){

					this.objNav.loadNavWebsite();

				}, this);


				this.objContent.addContainer(key, obj);
			}
			catch(e)
			{
				Ext.Msg.alert('Exception', e);

				this.objContent.enable();
			}
		}
		else
		{
			this.objContent.layout.setActiveItem(pos);
		}

	}

});


Ext.define('Metang.main.Services', {

	statics: {

		services: [],

		add: function(type, uri){

			this.services.push({type: type, uri: uri});

		},

		find: function(type){

			for(var i = 0; i < this.services.length; i++)
			{
				if(this.services[i].type == type)
				{
					return this.services[i].uri;
				}
			}

			return false;

		}

	}

});


Ext.define('Metang.main.ServiceItem', {
	extend: 'Ext.data.Model',

	fields: ['Type', 'URI']

});


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
						window.location.href = metang_url + 'login';
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
				// get available services and load depending js
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


							// load ui
							Metang.main.Instance = Ext.create('Metang.main.Layout');

							Metang.main.Instance.handlerContentLoader('content_page');

							//metang.main.instance.objHeader.getTopToolbar().get(5).setText('Logged in as <a href="' + metang.main.user.profileUrl + '">' + metang.main.user.name + '</a> (' + metang.main.user.group + ') ' + metang.main.user.timezone, false);

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
			}
			else
			{
				Ext.Msg.alert('Error', 'Couldnt request services.');
			}

		}

	}

});






/*
Ext.application({

	name: 'metang',
	launch: metang.main.checkAuth()

});
*/

Ext.onReady(function(){

	Metang.main.Util.checkAuth();

});

