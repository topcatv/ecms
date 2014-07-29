Ext.define('ECM.model.Content', {
	extend : 'Ext.data.Model',
	fields : [ {
		name : 'id',
		type : 'string'
	}, {
		name : 'name',
		type : 'string'
	}, {
		name : 'folder',
		type : 'boolean'
	}, {
		name : 'size',
		type : 'int'
	}, {
		name : 'mimeType',
		type : 'string'
	}, {
		name : 'created',
		type : 'date',
		dateFormat : 'Y-m-d H:i:s'
	}, {
		name : 'creator',
		type : 'string'
	}, {
		name : 'lastModifiedBy',
		type : 'string'
	}, {
		name : 'lastModified',
		type : 'date',
		dateFormat : 'Y-m-d H:i:s'
	} ]
});
