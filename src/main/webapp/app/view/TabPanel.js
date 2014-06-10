Ext.define('ECM.view.TabPanel', {
	extend : 'Ext.tab.Panel',
	title : '',
	alias : 'widget.ecmTabPanel',
	defaults : {
		//bodyStyle : 'padding:15px'
	},
	items : [{
		title: 'test',
		xtype: 'userlist'
	}/*{
		title : 'General Info',
		xtype : 'form',
		items : [ {
			xtype : 'textfield',
			fieldLabel : 'First Name',
			value : 'Vitaliy',
			allowBlank : false
		}, {
			xtype : 'textfield',
			fieldLabel : 'Last Name',
			value : 'Khmurach',
			allowBlank : false
		} ],
		buttons : [ {
			text : 'Save',
			action : 'save',
			disabled : true
		} ]
	}, {
		title : 'Additional Info',
		xtype : 'form',
		items : [ {
			xtype : 'textfield',
			fieldLabel : 'Country',
			value : 'Ukraine',
			allowBlank : false
		}, {
			xtype : 'textfield',
			fieldLabel : 'City',
			value : 'Kiev',
			allowBlank : false
		} ],
		buttons : [ {
			text : 'Save',
			action : 'save',
			disabled : true
		} ]
	} */],
	initComponent : function() {
		this.callParent(arguments);
	},
});