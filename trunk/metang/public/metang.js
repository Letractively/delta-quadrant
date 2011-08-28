
Ext.onReady(function(){

	Ext.Loader.setConfig({enabled:true});

	Ext.create('Metang.main.Util');

	Metang.main.Util.checkAuth();

});


