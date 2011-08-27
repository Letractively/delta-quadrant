
Ext.require('Metang.main.Header');
Ext.require('Metang.main.Nav');
Ext.require('Metang.main.Content');

Ext.define('Metang.main.Layout', {
	extend: 'Ext.container.Viewport',

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
