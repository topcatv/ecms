Ext.define('ECM.view.privilege.ContentGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.content_grid_for_permission',

	title : '文件',
	store : "ContentForPermission",
	selType : 'checkboxmodel',
	tbar : [{
		text : '授权',
		iconCls : 'fa fa-key',
		action : 'authorize'
	}],
	bbar : {
		xtype : 'pagingtoolbar',
		store : 'ContentForPermission',
		displayInfo : true
	},

	initComponent : function() {
		this.columns = [{
			dataIndex: 'isFolder',
			renderer: function(value, metaData, record, row, col, store, gridView){
				var isFolder = record.get('folder');
                var mimeType = record.get('mimeType');
                if(isFolder) {
                	return '<i class="fa fa-folder"></i>';
                } else {
                	if(mimeType.search("image") != -1){
                		return '<i class="fa fa-file-picture-o"></i>'
                	}
                	if(mimeType.search("pdf") != -1){
                		return '<i class="fa fa-file-pdf-o"></i>'
                	}
                	if(mimeType.search("powerpoint") != -1){
                		return '<i class="fa fa-file-powerpoint-o"></i>'
                	}
                	if(mimeType.search("msword") != -1){
                		return '<i class="fa fa-file-word-o"></i>'
                	}
                	if(mimeType.search("excel") != -1){
                		return '<i class="fa fa-file-excel-o"></i>'
                	}
                	if(mimeType.search("zip") != -1){
                		return '<i class="fa fa-file-archive-o"></i>'
                	}
                	if(mimeType.search("audio") != -1){
                		return '<i class="fa fa-file-audio-o"></i>'
                	}
                	if(mimeType.search("video") != -1){
                		return '<i class="fa fa-file-video-o"></i>'
                	}
                	if(mimeType.search("text") != -1){
                		return '<i class="fa fa-file-text-o"></i>'
                	}
                    return '<i class="fa fa-file"></i>';
                }
            },
            width : 25
		},{
			header : '名称',
			dataIndex : 'name'
		}, {
			header : '大小',
			dataIndex : 'size'
		}, {
			header : '创建人',
			dataIndex : 'creator'
		}, {
			header : '文件类型',
			dataIndex : 'mimeType'
		}, {
			header : '创建时间',
			flex : 1,
			dataIndex : 'created',
			xtype : 'datecolumn', // Ext.grid.column.Date日期列
			format : 'Y-m-d H:i'// 日期格式化字符串
		}, {
			header : '修改人',
			dataIndex : 'lastModifiedBy'
		}, {
			header : '修改时间',
			dataIndex : 'lastModified',
			flex : 1,
			xtype : 'datecolumn', // Ext.grid.column.Date日期列
			format : 'Y-m-d H:i'// 日期格式化字符串
		}];

		this.callParent(arguments);
	}
});
