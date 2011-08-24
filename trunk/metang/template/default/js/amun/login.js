/*
 *  $Id: login.js 117 2010-11-24 20:31:42Z k42b3.x $
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

Ext.ns('metang.login');

/**
 * login
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 117 $
 */

metang.login.form = Ext.extend(Ext.FormPanel, {

	initComponent: function(){

		var config = {

			frame: false,
			border: false,
			bodyStyle: 'padding:5px 5px',
			trackResetOnLoad: true,
			defaults: {

				width: 200

			},
			items: {

				xtype: 'button',
				text: 'Login',
				scale: 'large',
				scope: this,
				handler: function(){

					this.getForm().submit({

						url: metang_url + 'login',
						waitMsg: 'Redirecting ...'

					});

				}

			}

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		metang.login.form.superclass.initComponent.apply(this, arguments);

	}

});


metang.login.window = Ext.extend(Ext.Window, {

	o_login: null,

	initComponent: function(){

		this.o_login = new metang.login.form();

		var config = {

			title: 'Metang',
			layout: 'fit',
			/*
			width: 225,
			height: 80,
			*/
			closable: false,
			resizable: false,
			draggable: false,
			modal: true,
			items: [this.o_login]

		};


		this.o_login.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				window.location.href = action.result.url;

			}

		});

		this.o_login.on('actionfailed', function(form, action){

			Ext.Msg.alert('Error', action.result.message);

		});


		Ext.apply(this, Ext.apply(this.initialConfig, config));

		metang.login.window.superclass.initComponent.apply(this, arguments);

	}

});


Ext.onReady(function(){

	Ext.QuickTips.init();

	Ext.form.Field.prototype.msgTarget = 'qtip';

	var w = new metang.login.window();

	w.show();

});

