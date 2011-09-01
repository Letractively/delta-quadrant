
Ext.define('Metang.basic.Container', {
	extend: 'Ext.tab.Panel',

	view: null,
	create: null,
	update: null,
	delete: null,

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


		/*
		this.on('activate', function(el, opt){

		}, this);
		*/

	},

	onLoad: function(uri){

		this.view = this.add(Ext.create('Metang.basic.Grid', {

			uri: uri

		}));

		this.view.on('selectRow', function(id){

			this.selectedId = id

		}m this);

		this.create = this.add(Ext.create('Metang.basic.Form', {

			title: 'Create',
			uri: uri + '/form?method=create'

		}));

		this.update = this.add(Ext.create('Metang.basic.Form', {

			title: 'Update',
			uri: uri + '/form?method=update'

		}));

		this.delete = this.add(Ext.create('Metang.basic.Form', {

			title: 'Delete',
			uri: uri + '/form?method=delete'

		}));

	}

});
