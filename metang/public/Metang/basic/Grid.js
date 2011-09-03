
Ext.define('Metang.basic.Grid', {
	extend: 'Ext.panel.Panel',

	itemsPerPage: 32,
	supportedFields: null,
	store: null,

	initComponent: function(){

		var config = {

			layout: 'fit',
			border: false

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.basic.Grid.superclass.initComponent.apply(this, arguments);


		this.loadAvailableFields();

	},

	build: function(){

		// define model
		var fields = [];

		for(var i = 0; i < this.supportedFields.length; i++)
		{
			fields.push({

				name: this.supportedFields[i]

			});
		}

		Ext.define(this.uri,{

			extend: 'Ext.data.Model',
			fields: fields

		});


		// build store
		this.store = Ext.create('Ext.data.Store', {

			model: this.uri,
			autoLoad: false,
			pageSize: this.itemsPerPage,
			proxy: {

				type: 'ajax',
				url: Metang.main.Util.getProxyUrl(this.uri + '?format=json'),
				reader: {

					type: 'json',
					root: 'entry'

				}

			}

		});

		// build grid
		var columns = [];

		for(var i = 0; i < this.supportedFields.length; i++)
		{
			columns.push({

				header: this.supportedFields[i],
				dataIndex: this.supportedFields[i]

			});
		}

		var grid = Ext.create('Ext.grid.Panel', {

			store: this.store,
			columns: columns,
			border: false,
			dockedItems: [{

				xtype: 'pagingtoolbar',
				store: this.store,
				dock: 'bottom',
				displayInfo: true

			}]

		});


		this.add(grid);

		this.doLayout();

		this.load();

	},

	load: function(){

		this.store.load({

			params:{

				start: 0,
				limit: this.itemsPerPage

			}

		});

	},

	loadAvailableFields: function(){

		Ext.Ajax.request({

			url: Metang.main.Util.getProxyUrl(this.uri + '/@supportedFields?format=json'),
			method: 'GET',
			scope: this,
			disableCaching: true,
			success: function(response){

				var fields = Ext.JSON.decode(response.responseText);

				this.supportedFields = fields.item;

				this.build();

			},
			failure: function(response){

				Ext.Msg.alert('Error', 'Could not get supported fields.');

			}

		});

	}

});
