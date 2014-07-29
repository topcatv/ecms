Ext.define('ECM.model.User', {
	extend : 'Ext.data.Model',
	fields : [ 'id', 'name', 'description', {name:'createDate',type : 'date', dateFormat : 'Y-m-d H:i:s'}, 'locked' ]
});