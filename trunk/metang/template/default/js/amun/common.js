/*
 *  $Id: common.js 193 2011-03-06 02:14:57Z k42b3.x $
 *
 * amun
 * A social content managment system based on the psx framework. For
 * the current version and informations visit <http://amun.phpsx.org>
 *
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of amun. amun is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * amun is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with amun. If not, see <http://www.gnu.org/licenses/>.
 */

Ext.ns('metang.common');

/**
 * amun.common
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 193 $
 */

metang.common.getProxyUrl = function(url, params)
{
	var param = '';

	for(k in params)
	{
		param+= '&' + k + '=' + encodeURIComponent(params[k]);
	}

	return metang_url + 'proxy?url=' + encodeURIComponent(url) + param;
}

/*
metang.common.form = Ext.extend(Ext.form.FormPanel, {

	url: null,
	request_method: null,
	fields: null,

	// default overwriteable config
	labelWidth: 125,
	frame: false,
	border: false,
	bodyStyle: 'padding:5px 5px 0',
	trackResetOnLoad: true,
	defaultType: 'textfield',
	autoScroll: true,
	defaults: {

		width: 250

	},

	initComponent: function(){

		var config = {

			items: this.fields,
			buttons: [{

				text: 'Submit',
				scope: this,
				handler: function(){

					this.sendData(this.url);

				}

			},{

				text: 'Cancel',
				scope: this,
				handler: function(){

					this.fireEvent('canceled');

					this.reset();

				}

			}]

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.common.form.superclass.initComponent.apply(this, arguments);

		this.addEvents('loaded', 'submited', 'canceled', 'failure');

	},

	getFields: function(){

		return this.getForm().getFieldValues();

	},

	mask: function(msg){

		Ext.MessageBox.wait(msg, 'Status');

	},

	unmask: function(){

		Ext.MessageBox.updateProgress(1);
		Ext.MessageBox.hide();

	},

	buildForm: function(fields, panel){

		for(var i = 0; i < fields.length; i++)
		{
			switch(fields[i].class)
			{
				case 'fieldset':

					var tabPanel = new Ext.TabPanel({

						xtype: 'tabpanel',
						autoWidth: true,
						activeTab: 0,
						border: false,
						deferredRender: false,
						defaults: {

							layout: 'form',
							labelWidth: 125,
							frame: false,
							border: false,
							bodyStyle: 'padding:5px 5px 0',
							autoScroll: true,
							trackResetOnLoad: true,
							defaultType: 'textfield',
							hideMode: 'offsets',
							defaults: {

								width: 250

							}

						}

					});

					this.buildForm(tabPanel);

					panel.add(tabPanel);

					break;

				case 'input':

					inputPanel = {

						fieldLabel: fields[i].label,
						name: fields[i].ref,
						allowBlank: false

					};

					if(typeof(fields[i].disabled) != 'undefined')
					{
						inputPanel['disabled'] = true;
					}

					panel.add(inputPanel);

					break;

				case 'select':

					break;
			}
		}

	},

	loadForm: function(url){

		this.mask('Load form ...');

		var con = new Ext.data.Connection();

		con.request({

			url: url,
			method: 'GET',
			scope: this,
			success: function(response){

				var resp = Ext.util.JSON.decode(response.responseText);

				this.unmask();

			},
			failure: function(response){

				this.unmask();

				this.fireEvent('failure');

			}

		});

	},

	loadData: function(url){

		this.mask('Load data ...');

		var con = new Ext.data.Connection();

		con.request({

			url: url,
			method: 'GET',
			scope: this,
			success: function(response){

				var resp = Ext.util.JSON.decode(response.responseText);

				var entry = Ext.util.Format.defaultValue(resp.entry, []);

				if(entry.length > 0)
				{
					this.getForm().setValues(entry[0]);
				}

				this.unmask();

				this.fireEvent('loaded');

			},
			failure: function(response){

				this.unmask();

				this.fireEvent('failure');

			}

		});

	},

	sendData: function(){

		if(this.getForm().isValid())
		{
			this.mask('Submit data ...');

			var fields = this.get_fields();

			var con = new Ext.data.Connection();

			var method = this.request_method == 'GET' ? 'GET' : 'POST';

			con.request({

				url: get_proxy_url(this.url),
				method: method,
				jsonData: fields,
				scope: this,
				headers: {

					'Content-type': 'application/json',
					'X-Http-Method-Override': this.request_method

				},
				success: function(response){

					var resp = Ext.util.JSON.decode(response.responseText);
					var success = Ext.util.Format.defaultValue(resp.success, false);
					var msg = Ext.util.Format.defaultValue(resp.message, '');

					if(success === true)
					{
						this.unmask();

						if(msg != '')
						{
							Ext.MessageBox.alert('Success', msg);
						}

						this.fireEvent('submited');

						this.reset();
					}
					else
					{
						this.unmask();

						if(msg != '')
						{
							Ext.MessageBox.alert('Failure', msg);
						}

						this.fireEvent('failure');
					}

				},
				failure: function(response){

					var resp = Ext.util.JSON.decode(response.responseText);
					var msg = Ext.util.Format.defaultValue(resp.message, '');

					this.unmask();

					if(msg != '')
					{
						Ext.MessageBox.alert('Failure', msg);
					}

					this.fireEvent('failure');

				}

			});
		}

	},

	reset: function(){

		// a form reset doesnt clear the fields if the data
		// is in the value attribute ... so we overwrite with
		// null values and clear the invalid fields
		// this.getForm().reset();
		this.items.each(function(item, i){

			if(typeof item.setValue == 'function'){

				item.setValue(null);

			}

		});

		this.getForm().clearInvalid();

	}

});


metang.common.grid = Ext.extend(Ext.grid.GridPanel, {

	columns: null,
	options: null,
	store: null,

	disabled_buttons: [],
	row_id: -1,

	initComponent: function(){

		this.disabled_buttons = [];

		for(var i = 0; i < this.options.length; i++)
		{
			if(typeof this.options[i].disabled != 'undefined' && this.options[i].disabled == true)
			{
				this.disabled_buttons.push(i);
			}
		}

		//this.build_search();

		if(this.options.length > 0)
		{
			var tbar = new Ext.Toolbar({

				items: this.options

			});
		}

		var bbar = new Ext.PagingToolbar({

			pageSize: 32,
			store: this.store,
			displayInfo: true,
			displayMsg: 'Entries {0} - {1} of {2}',
			emptyMsg: 'No entries to display'

		});

		var config = {

			margins: '0 0 0 0',
			trackMouseOver: false,
			store: this.store,
			border: false,
			stripeRows: true,
			columns: this.columns,
			tbar: tbar,
			bbar: bbar

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.common.grid.superclass.initComponent.apply(this, arguments);


		this.on('rowclick', function(grid, row, e){

			grid.enable();

			var data   = grid.getStore();
			var record = data.getAt(row);
			var id     = record.get('id');

			grid.row_id = id;

		});

	},

	onRender: function(){

		this.store.load();

		amun.common.grid.superclass.onRender.apply(this, arguments);

	},

	build_search: function(){

		this.options.push('->');

		this.options.push({

			xtype: 'label',
			text: 'Search:'

		});

		this.options.push(' ');



		var field_store = new Ext.data.JsonStore({

			fields: [

				{name: 'header', type: 'string'},
				{name: 'dataIndex', type: 'string'}

			],
			data: this.columns

		});

		this.options.push(new Ext.form.ComboBox({

			name: 'field',
			hiddenName: 'field',
			store: field_store,
			valueField: 'dataIndex',
			displayField: 'header',
			editable: false,
			mode: 'local',
			emptyText: 'Select a field ...',
			selectOnFocus: true,
			triggerAction: 'all',
			allowBlank: true,
			width: 80,
			listWidth: 80

		}));

		this.options.push(' ');

		this.options.push({

			xtype: 'textfield',
			name: 'value',
			width: 150

		});

		this.options.push(' ');

		this.options.push({

			xtype: 'button',
			iconCls: 'search',
			text: 'Search',
			scope: this,
			handler: function(){

				//var tb = this.getTopToolbar();

				//tb.get(0);

				alert('Not implemented yet ...');

			}

		});

	},

	reset: function(){

		this.row_id = -1;

		var tbar = this.getTopToolbar();
		var sm   = this.getSelectionModel();

		sm.clearSelections();

		for(var i = 0; i < this.disabled_buttons.length; i++)
		{
			tbar.get(this.disabled_buttons[i]).disable();
		}

	},

	enable: function(){

		var tbar = this.getTopToolbar();
		var o;

		for(var i = 0; i < this.disabled_buttons.length; i++)
		{
			o = tbar.get(this.disabled_buttons[i]);

			if(typeof o != 'undefined')
			{
				tbar.get(this.disabled_buttons[i]).enable();
			}
		}

	}

});
*/

 /**
  * checkcolumn grid
  * @see http://dev.sencha.com/deploy/dev/examples/grid/edit-grid.html
  */
  /*
Ext.ns('Ext.ux.grid');

Ext.ux.grid.CheckColumn = Ext.extend(Ext.grid.Column, {

    processEvent : function(name, e, grid, rowIndex, colIndex){
        if (name == 'mousedown') {
            var record = grid.store.getAt(rowIndex);
            record.set(this.dataIndex, !record.data[this.dataIndex]);
            return false; // Cancel row selection.
        } else {
            return Ext.grid.ActionColumn.superclass.processEvent.apply(this, arguments);
        }
    },

    renderer : function(v, p, record){
        p.css += ' x-grid3-check-col-td';
        return String.format('<div class="x-grid3-check-col{0}">&#160;</div>', v ? '-on' : '');
    },

    // Deprecate use as a plugin. Remove in 4.0
    init: Ext.emptyFn
});

// register ptype. Deprecate. Remove in 4.0
Ext.preg('checkcolumn', Ext.ux.grid.CheckColumn);

// backwards compat. Remove in 4.0
Ext.grid.CheckColumn = Ext.ux.grid.CheckColumn;

// register Column xtype
Ext.grid.Column.types.checkcolumn = Ext.ux.grid.CheckColumn;
*/


