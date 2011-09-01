
Ext.define('Metang.main.Content', {
	extend: 'Ext.panel.Panel',

	services: [],
	pos: 0,

	initComponent: function(){

		var config = {

			id: 'content',
			region: 'center',
			layout: 'card',
			margins: '0 0 0 0',
			border: false

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Content.superclass.initComponent.apply(this, arguments);

	},

	addContainer: function(ns, obj){

		this.services.push({

			ns: ns,
			pos: this.pos

		});

		this.getLayout().setActiveItem(obj);

		return this.pos++;
	},

	getPos: function(ns){

		for(var i = 0; i < this.services.length; i++)
		{
			if(this.services[i].ns == ns)
			{
				return this.services[i].pos;
			}
		}

		return false;

	},

	getContainer: function(ns){

		var items = this.getLayout().getLayoutItems();
		var pos = this.getPos(ns);

		if(pos !== false)
		{
			this.getLayout().setActiveItem(pos);

			return items[pos];
		}
		else
		{
			return false;
		}

	}

});
