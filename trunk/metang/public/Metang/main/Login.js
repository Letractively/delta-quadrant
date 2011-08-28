
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
