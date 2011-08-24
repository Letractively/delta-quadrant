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

Ext.ns('metang.main');

/**
 * amun.main
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 207 $
 */

metang.main.header = Ext.extend(Ext.Panel, {

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

						metang.main.instance.handler_content_loader('content_page');

					}

				},{

					text: 'Gadget',
					iconCls: 'gadget',
					handler: function(){

						metang.main.instance.handler_content_loader('content_gadget');

					}

				},{

					text: 'Service',
					iconCls: 'service',
					handler: function(){

						metang.main.instance.handler_content_loader('content_service');

					}

				},{

					text: 'Media',
					iconCls: 'media',
					handler: function(){

						metang.main.instance.handler_content_loader('content_media');

					}

				}]

			},{

				text: 'System',
				scale: 'medium',
				menu: [{

					text: 'API',
					iconCls: 'api',
					handler: function(){

						metang.main.instance.handler_content_loader('system_api');

					}

				},{

					text: 'Approval',
					iconCls: 'approval',
					handler: function(){

						metang.main.instance.handler_content_loader('system_approval');

					}

				},{

					text: 'Country',
					iconCls: 'country',
					handler: function(){

						metang.main.instance.handler_content_loader('system_country');

					}

				},{

					text: 'Event',
					iconCls: 'event',
					handler: function(){

						metang.main.instance.handler_content_loader('system_event');

					}

				},{

					text: 'Logging',
					iconCls: 'log',
					handler: function(){

						metang.main.instance.handler_content_loader('system_log');

					}

				},{

					text: 'Vars',
					iconCls: 'vars',
					handler: function(){

						metang.main.instance.handler_content_loader('system_vars');

					}

				}]

			},{

				text: 'User',
				scale: 'medium',
				menu: [{

					text: 'Account',
					iconCls: 'account',
					handler: function(){

						metang.main.instance.handler_content_loader('user_account');

					}

				},{

					text: 'Activity',
					iconCls: 'activity',
					handler: function(){

						metang.main.instance.handler_content_loader('user_activity');

					}

				},{

					text: 'Friend',
					iconCls: 'friend',
					handler: function(){

						metang.main.instance.handler_content_loader('user_friend');

					}

				},{

					text: 'Group',
					iconCls: 'group',
					handler: function(){

						metang.main.instance.handler_content_loader('user_group');

					}

				},{

					text: 'Level',
					iconCls: 'level',
					handler: function(){

						metang.main.instance.handler_content_loader('user_level');

					}

				},{

					text: 'Right',
					iconCls: 'right',
					handler: function(){

						metang.main.instance.handler_content_loader('user_right');

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
							msg: 'Amun Backend version 0.0.1',
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

					var uri = metang.main.services.find('http://ns.amun.org/2010/amun/my');
					var con = new Ext.data.Connection();

					if(uri !== false)
					{
						con.request({

							url: get_proxy_url(uri + '/endSession?format=json'),
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

		metang.main.header.superclass.initComponent.apply(this, arguments);

	}

});


metang.main.nav = Ext.extend(Ext.Panel, {

	o_website: null,
	o_service: null,

	initComponent: function() {

		// website tree panel
		/*
		this.o_website = new Ext.tree.TreePanel({

			title: 'Website',
			collapsible: false,
			rootVisible: false,
			singleExpand: false,
			autoScroll: true,
			containerScroll: true,
			useArrows: false,
			enableDD: true,
			border: false,
			root: new Ext.tree.TreeNode()

		});

		this.load_nav_website();
		*/

		// service table
		/*
		this.o_service = new Ext.grid.GridPanel({

			title: 'Services',
			store: metang.content.service.store,
			margins: '0 0 0 0',
			border: false,
			trackMouseOver: false,
			hideHeaders: true,
			autoExpandColumn: 'name',
			autoScroll: false,
			viewConfig: {

				forceFit: true,
				scrollOffset: 0

			},
			columns: [

				{id: 'name', dataIndex: 'name', menuDisabled: true, sortable: true}

			]

		});

		metang.content.service.store.load();
		*/

		var config = {

			title: 'Navigation',
			region: 'west',
			margins: '0 5 0 0',
			width: 200,
			border: true,
			layout: 'accordion',
			items: [this.o_website, this.o_service]

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		metang.main.nav.superclass.initComponent.apply(this, arguments);

	},

	onRender: function(){

		metang.main.nav.superclass.onRender.apply(this, arguments);

	},

	load_nav_website: function(){

		this.o_website.getRootNode().removeAll();

		var con = new Ext.data.Connection();

		con.request({

			url: get_proxy_url(amun_url + 'api/content/page/buildTree?format=json'),
			method: 'GET',
			scope: this,
			success: function(response){

				var resp  = Ext.JSON.decode(response.responseText);
				var entry = Ext.util.Format.defaultValue(resp.entry, []);

				this.parse_rec_tree(this.o_website.getRootNode(), entry, 0);

				this.o_website.expandPath(this.o_website.getNodeById(1).getPath());

			}

		});

	},

	parse_rec_tree: function(node, entries, deep){

		for(var i = 0; i < entries.length; i++)
		{
			var child = node.appendChild(new Ext.tree.TreeNode({

				id: entries[i].id,
				text: entries[i].text,
				iconCls: deep == 0 ? 'website' : 'page'

			}));

			if(typeof(entries[i].children) != 'undefined')
			{
				this.parse_rec_tree(child, entries[i].children, deep + 1);
			}
		}

	}

});


metang.main.content = Ext.extend(Ext.Panel, {

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

		metang.main.content.superclass.initComponent.apply(this, arguments);

	},

	add_container: function(id, obj){

		this.services.push({

			id: id,
			pos: this.pos

		});

		this.add(obj);

		return this.pos++;
	},

	get_container: function(id){

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


metang.main.layout = Ext.extend(Ext.Viewport, {

	o_header: null,
	o_nav: null,
	o_content: null,

	initComponent: function() {

		this.o_header  = new metang.main.header();
		this.o_nav     = new metang.main.nav();
		this.o_content = new metang.main.content();

		var config = {

			layout: 'border',
			items: [this.o_header, this.o_nav, this.o_content]

		};


		this.handler_website();

		this.handler_service();


		Ext.apply(this, Ext.apply(this.initialConfig, config));

		metang.main.layout.superclass.initComponent.apply(this, arguments);


		// dashboard panel
		/*
		this.o_content.add_container('dashboard', new amun.dashboard.panel({

			margins: '0 0 0 0',
			border: false,

		}));
		*/

	},

	onRender: function(){

		metang.main.layout.superclass.onRender.apply(this, arguments);

	},

	handler_website: function(){

		this.o_nav.get(0).on('click', function(n){

			// load page panel
			this.handler_content_loader('content_page');

			// load data
			metang.content.page.store_gadget.load({

				params: {

					filterBy: 'id',
					filterOp: 'equals',
					filterValue: n.id

				}

			});
			metang.content.page.store_right.load({

				params: {

					filterBy: 'id',
					filterOp: 'equals',
					filterValue: n.id

				}

			});

			// show edit page
			var obj = this.o_content.layout.activeItem;

			obj.panel_update.load_data(get_proxy_url(metang.content.page.url + '&filterBy=id&filterOp=equals&filterValue=' + n.id));

			obj.panel_update.set_active_tab(0);

			obj.layout.setActiveItem(2);

		}, this);

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

				/*
				var url    = amun_url + 'api/content/page/reparentPage?format=json';
				var params = {'pageId': node.id, 'parentId': newParent.id, 'position': position};
				*/

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

	},

	handler_service: function(){

		this.o_nav.get(1).on('rowclick', function(grid, row, e){

			var data   = grid.getStore();
			var record = data.getAt(row);
			var name   = record.get('name');

			this.handler_content_loader('service_' + name);

		}, this);

	},

	handler_content_loader: function(key){

		pos = this.o_content.get_container(key);

		if(pos === false)
		{
			this.o_content.disable();

			var c   = key.replace(/_/g, '.');
			var cls = 'metang.' + c + '.panel';

			try
			{
				obj = eval('new ' + cls);


				obj.addEvents('help', 'reloadtree');


				obj.on('added', function(obj, ownerCt, index){

					this.o_content.layout.setActiveItem(index);

					this.o_content.enable();

				}, this);

				obj.on('help', function(id){

					this.o_about.show_help(id);

				}, this);

				obj.on('reloadtree', function(){

					this.o_nav.load_nav_website();

				}, this);


				this.o_content.add_container(key, obj);
			}
			catch(e)
			{
				Ext.Msg.alert('Notice', e);

				this.o_content.enable();
			}
		}
		else
		{
			this.o_content.layout.setActiveItem(pos);
		}

	}

});


metang.main.services = {

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

};


Ext.define('Service', {

	extend: 'Ext.data.Model',
	fields: ['Type', 'URI']

});


metang.main.parseUser = function(user){

	metang.main.user = user;

	if(metang.main.user.loggedIn == true && metang.main.user.status == 'Administrator')
	{
		// get available services and load depending js
		var uri = metang.main.services.find('http://ns.amun-project.org/2011/amun/content/service');

		if(uri !== false)
		{
			Ext.Ajax.request({

				url: metang.common.getProxyUrl(uri),
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
					metang.main.instance = new metang.main.layout();

					metang.main.instance.handler_content_loader('content_page');

					metang.main.instance.o_header.getTopToolbar().get(5).setText('Logged in as <a href="' + metang.main.user.profileUrl + '">' + metang.main.user.name + '</a> (' + metang.main.user.group + ') ' + metang.main.user.timezone, false);

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


metang.main.fetchAmunServices = function(){

	// get available services
	var proxy = new Ext.data.proxy.Ajax({

		url: metang.common.getProxyUrl(amun_url + 'api/meta/xrds'),
		reader: {
			type: 'xml',
			model: 'Service',
			record: 'Service'
		}

	});

	var services = Ext.create('Ext.data.Store', {

		model: 'Service',
		proxy: proxy,
		autoLoad: false

	});

	services.on('load', function(){

		// add discovered services
		services.each(function(rec){

			metang.main.services.add(rec.get('Type'), rec.get('URI'));

		});

		// find my service
		var uri = metang.main.services.find('http://ns.amun-project.org/2011/amun/service/my');

		if(uri !== false)
		{
			// check whether user is logged in
			Ext.Ajax.request({

				url: metang.common.getProxyUrl(uri + '/verifyCredentials?format=json'),
				method: 'GET',
				scope: this,
				disableCaching: true,
				success: function(response){

					var user = Ext.JSON.decode(response.responseText);

					metang.main.parseUser(user);

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

	}, this);

	services.load();

}

metang.main.checkAuth = function(){

	Ext.Ajax.request({

		url: metang.common.getProxyUrl(amun_url + 'api/meta/xrds'),
		method: 'GET',
		scope: this,
		disableCaching: true,
		success: function(response){

			var contentType = response.getResponseHeader('Content-Type')

			if(contentType == 'application/xrds+xml')
			{
				metang.main.fetchAmunServices();
			}
			else
			{
				window.location.href = metang_url + 'login';
			}

		},
		failure: function(response){

			window.location.href = metang_url + 'login';

		}

	});

}

Ext.onReady(function(){

	/*
	Ext.QuickTips.init();

	Ext.form.Field.prototype.msgTarget = 'qtip';
	*/

	//
	metang.main.checkAuth();

});


