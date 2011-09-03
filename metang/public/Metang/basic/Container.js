
Ext.define('Metang.basic.Container', {
	extend: 'Ext.tab.Panel',

	view: null,
	selectedId: 0,

	initComponent: function(){

		var config = {

			title: 'Content',
			region: 'center',
			margins: '0 0 0 0',
			border: true,
			items: []

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.basic.Container.superclass.initComponent.apply(this, arguments);


		this.on('tabchange', function(el, newCard, oldCard, opt){

			newCard.load(this.selectedId);

		}, this);

	},

	onLoad: function(uri){

		this.view = this.add(Ext.create('Metang.basic.Grid', {

			title: 'View',
			uri: uri

		}));

		this.view.on('selectRow', function(id){

			this.selectedId = id

		}, this);

		this.add(Ext.create('Metang.basic.Form', {

			title: 'Create',
			uri: uri + '/form?method=create'

		}));

		this.add(Ext.create('Metang.basic.Form', {

			title: 'Update',
			uri: uri + '/form?method=update'

		}));

		this.add(Ext.create('Metang.basic.Form', {

			title: 'Delete',
			uri: uri + '/form?method=delete'

		}));

	}

});
