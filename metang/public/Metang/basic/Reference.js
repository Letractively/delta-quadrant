
Ext.define('Metang.basic.Reference', {
	extend: 'Ext.form.field.Trigger',

	trigger2Cls: Ext.baseCSSPrefix + 'form-search-trigger',

	store: null,
	grid: null,
	window: null,

	initComponent: function(){

		this.callParent(arguments);

		this.on('specialkey', function(f, e){

			if(e.getKey() == e.ENTER)
			{
				this.onTrigger2Click();
			}

		}, this);


		// define model
		Ext.define('reference', {

			extend: 'Ext.data.Model',
			fields: [this.valueField, this.labelField]

		});


		// build store
		this.store = Ext.create('Ext.data.Store', {

			model: 'reference',
			autoLoad: false,
			pageSize: 32,
			proxy: {

				type: 'ajax',
				url: Metang.main.Util.getProxyUrl(this.src + '?format=json'),
				reader: {

					type: 'json',
					root: 'entry'

				}

			}

		});


		// build grid
		var columns = [];

		columns.push({

			header: this.labelField,
			dataIndex: this.labelField,
			flex: 1

		});

		this.grid = Ext.create('Ext.grid.Panel', {

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


		// row listener
		this.grid.on('itemdblclick', function(el, rec){

			this.setValue(rec.get(this.valueField));

			this.window.hide();

		}, this);
	},

	afterRender: function(){

		this.callParent();

		this.triggerEl.item(0).setDisplayed('none');

	},

	onTrigger2Click: function(){

		if(this.window == null)
		{
			this.window = Ext.create('Ext.window.Window', {

				title: 'Reference',
				width: 350,
				height: 300,
				model: true,
				layout: 'fit',
				closeAction: 'hide',
				items: this.grid

			});
		}

		this.store.load();

		this.window.show();

	}

});

