/*
 *  $Id$
 *
 * metang
 * An web application to access the API of amun.
 *
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of metang. metang is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * metang is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with metang. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Metang.main.Login
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   javascript
 * @version    $Revision$
 */
Ext.define('Metang.main.Login', {
	extend: 'Ext.container.Viewport',

	objForm: null,

	initComponent: function(){

		this.objForm = Ext.create('Ext.form.Panel', {

			layout: {

				type: 'vbox',
				align: 'center',
				pack: 'start'

			},
			title: 'metang (' + metang_version + ')',
			region: 'center',
			frame: false,
			border: false,
			bodyStyle: 'padding:32px 6px',
			trackResetOnLoad: true,
			defaults: {

				width: 200

			},
			items: [{

				xtype: 'button',
				text: 'Login',
				scale: 'large',
				scope: this,
				handler: function(){

					this.objForm.getForm().submit({

						url: metang_url + 'login',
						waitMsg: 'Redirecting ...'

					});

				}

			}]

		});

		var config = {

			layout: 'border',
			items: [this.objForm]

		};


		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Login.superclass.initComponent.apply(this, arguments);


		this.objForm.on('actioncomplete', function(form, action){

			if(action.type == 'submit')
			{
				window.location.href = action.result.url;
			}

		});

		this.objForm.on('actionfailed', function(form, action){

			Ext.Msg.alert('Error', action.result.message);

		});
	}

});
