Ext.define('ECM.controller.TabPanel', {
	extend : 'Ext.app.Controller',
	views : [ 'TabPanel' ],
	refs : [ {
		ref : 'edituser',
		selector : 'edituser'
	}],

	init : function() {
		console.log('The TabPanel was init');
	}
});
