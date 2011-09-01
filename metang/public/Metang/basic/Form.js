
Ext.define('Metang.basic.Form', {
	extend: 'Ext.form.Panel'

	initComponent: function(){

		var config = {

			title: 'View',
			layout: 'fit',
			border: false

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.basic.Grid.superclass.initComponent.apply(this, arguments);


		this.loadForm();

	},

	load: function(){

	},

	build: function(node){

		if(node.class == 'form')
		{
			return this.parseForm(node);
		}
		else if(node.class == 'input')
		{
			return this.parseInput(node);
		}
		else if(node.class == 'textarea')
		{
			return this.parseTextarea(node);
		}
		else if(node.class == 'select')
		{
			return this.parseSelect(node);
		}
		else if(node.class == 'tabbedpane')
		{
			return this.parseTabbedPane(node);
		}
		else if(node.class == 'panel')
		{
			return this.parsePanel(node);
		}
		else if(node.class == 'reference')
		{
			return this.parseReference(node);
		}
		else if(node.class == 'checkboxlist')
		{
			return this.parseChecboxList(node);
		}

		return null;

	},

	parseForm: function(node){

		var form = Ext.create('Ext.form.Panel', {

			url: node.action,
			items: this.parse(node.item),
			buttons: [{

				text: 'Reset',
				handler: function() {

					this.up('form').getForm().reset();

				}

			},{

				text: 'Submit',
				formBind: true,
				disabled: true,
				handler: function() {

					var form = this.up('form').getForm();

					if(form.isValid())
					{
						form.submit({

							success: function(form, action){

								Ext.Msg.alert('Success', action.result.msg);

							},
							failure: function(form, action) {

								Ext.Msg.alert('Failed', action.result.msg);

							}

						});
					}

				}

			}]

		});

		return form;

	},

	parseInput: function(){


	},

	parseTextarea: function(){


	},

	parseSelect: function(){


	},

	parseTabbedPane: function(){


	},

	parsePanel: function(){


	},

	parseReference: function(){


	},

	parseChecboxList: function(){


	},

	loadForm: function(){

		Ext.Ajax.request({

			url: Metang.main.Util.getProxyUrl(this.uri + '&format=json'),
			method: 'GET',
			scope: this,
			disableCaching: true,
			success: function(response){

				var form = Ext.JSON.decode(response.responseText);

				this.build(form);

			},
			failure: function(response){

				Ext.Msg.alert('Error', 'Could not get supported fields.');

			}

		});

	}

});
