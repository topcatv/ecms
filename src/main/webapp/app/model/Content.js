Ext.define('ECM.model.Content', {
	extend : 'Ext.data.Model',
	fields : [ {
		name : 'id',
		type : 'string'
	}, {
		name : 'name',
		type : 'string'
	}, {
		name : 'isFolder',
		type : 'boolean'
	}, {
		name : 'size',
		type : 'int'
	}, {
		name : 'mimeType',
		type : 'string'
	}, {
		name : 'created',
		type : 'date'
	}, {
		name : 'creator',
		type : 'string'
	}, {
		name : 'lastModifiedBy',
		type : 'string'
	}, {
		name : 'lastModified',
		type : 'date'
	} ]
});
