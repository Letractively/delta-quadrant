
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
