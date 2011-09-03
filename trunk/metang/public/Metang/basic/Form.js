
Ext.define('Metang.basic.Form', {
	extend: 'Ext.form.Panel',

	initComponent: function(){

		var config = {

			layout: 'fit',
			border: false

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.basic.Form.superclass.initComponent.apply(this, arguments);

	},

	load: function(){

		this.loadForm();

	},

	parse: function(node){

		var type = '';

		if(typeof node['class'] != 'undefined')
		{
			type = node['class'].toLowerCase();
		}

		if(type == 'form')
		{
			return this.parseForm(node);
		}
		else if(type == 'input')
		{
			return this.parseInput(node);
		}
		else if(type == 'textarea')
		{
			return this.parseTextarea(node);
		}
		else if(type == 'select')
		{
			return this.parseSelect(node);
		}
		else if(type == 'tabbedpane')
		{
			return this.parseTabbedPane(node);
		}
		else if(type == 'panel')
		{
			return this.parsePanel(node);
		}
		else if(type == 'reference')
		{
			return this.parseReference(node);
		}
		else if(type == 'checkboxlist')
		{
			return this.parseChecboxList(node);
		}

		return null;

	},

	parseForm: function(node){

		var el = Ext.create('Ext.form.Panel', {

			url: Metang.main.Util.getProxyUrl(node.action),
			items: this.parse(node.item),
			border: false,
			buttons: [{

				text: 'Reset',
				handler: function(){

					this.up('form').getForm().reset();

				}

			},{

				text: 'Submit',
				handler: function(){

					var form = this.up('form').getForm();

					if(form.isValid())
					{
						form.submit({

							clientValidation: true,
							success: function(form, action){

								Ext.Msg.alert('Success', action.result.text);

							},
							failure: function(form, action) {

								Ext.Msg.alert('Failed', action.result.text);

							}

						});
					}

				}

			}]

		});

		return el;

	},

	parseInput: function(node){

		var xtype = 'textfield';

		if(node.type == 'hidden')
		{
			xtype = 'hidden';
		}
		else if(node.type == 'date' || node.type == 'datetime')
		{
			xtype = 'datefield';
		}

		var el = {

			xtype: xtype,
			fieldLabel: node.label,
			name: node.ref,
			value: node.value,
			disabled: node.disabled

		};

		return el;

	},

	parseSelect: function(node){

		var data = [];

		for(var i = 0; i < node.children.item.length; i++)
		{
			data.push({

				label: node.children.item[i].label,
				value: node.children.item[i].value,

			});
		}

		var store = Ext.create('Ext.data.Store', {

			fields: ['label', 'value'],
			data: data

		});

		var el = Ext.create('Ext.form.ComboBox', {

			fieldLabel: node.label,
			name: node.ref,
			store: store,
			queryMode: 'local',
			displayField: 'label',
			valueField: 'value',
			value: node.value

		});

		return el;

	},

	parseTextarea: function(node){

		var el = {

			xtype: 'textarea',
			fieldLabel: node.label,
			name: node.ref,
			value: node.value

		};

		return el;

	},

	parseTabbedPane: function(node){

		var items = [];

		for(var i = 0; i < node.children.item.length; i++)
		{
			var panel = this.parse(node.children.item[i])

			if(panel != null && typeof panel === 'object')
			{
				items.push(panel);
			}
		}

		var el = Ext.create('Ext.tab.Panel', {

			border: false,
			items: items

		});

		return el;

	},

	parsePanel: function(node){

		var items = [];

		for(var i = 0; i < node.children.item.length; i++)
		{
			var panel = this.parse(node.children.item[i])

			if(panel != null && typeof panel == 'object')
			{
				items.push(panel);
			}
		}

		var el = Ext.create('Ext.panel.Panel', {

			title: node.label,
			border: false,
			bodyPadding: 6,
			items: items

		});

		return el;

	},

	parseReference: function(){

		return null;

	},

	parseChecboxList: function(){

		return null;

	},

	loadForm: function(){

		this.setLoading(true);

		Ext.Ajax.request({

			url: Metang.main.Util.getProxyUrl(this.uri + '&format=json'),
			method: 'GET',
			scope: this,
			disableCaching: true,
			success: function(response){

				var form = Ext.JSON.decode(response.responseText);

				this.add(this.parse(form));

				this.doLayout();

				this.setLoading(false);

			},
			failure: function(response){

				Ext.Msg.alert('Error', 'Could not get supported fields.');

			}

		});

	}

});
