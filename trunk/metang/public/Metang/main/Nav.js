
Ext.define('Metang.main.Nav', {
	extend: 'Ext.tree.TreePanel',

	objWebsite: null,
	store: null,

	initComponent: function() {

		// tree panel
		this.store = Ext.create('Ext.data.TreeStore', {

			root: {
				expanded: true,
				children: {}
			}

		});


		// load tree
		this.loadTree();


		var config = {

			title: 'Navigation',
			region: 'west',
			margins: '0 5 0 0',
			width: 200,
			border: true,
			store: this.store,
			collapsible: false,
			rootVisible: false,
			singleExpand: false,
			autoScroll: true

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
			this.getRootNode().removeAll();

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

