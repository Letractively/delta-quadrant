
Ext.define('Metang.main.Services', {

	statics: {

		services: [],

		add: function(type, uri){

			this.services.push({

				type: type,
				uri: uri

			});

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
