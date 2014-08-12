Ext.define('ECM.view.content.DetailWindow', {
	extend : 'Ext.Window',
	alias : 'widget.detail_window',

	title : '文档详细',
	width: 600,
	autoHeight : true,
	closable : true,
	closeAction : 'hide',
	resizable : true,
	draggable : true,
	autoHeight : true,
	layout : 'fit',
	border : true,
	modal : true,

	initComponent : function() {
		Ext.apply(this, {
			tpl : ['<p>文档名: {name}</p>',
		            '<p>文档大小: {size:this.formatSize} M</p>',
		            '<p>创建时间: {created:date("Y-m-d H:i")}</p>',
		            '<p>创建人: {creator}</p>',
		            '<p>修改时间: {lastModified:date("Y-m-d H:i")}</p>',
		            '<p>修改人: {lastModifiedBy}</p>',
		            '<tpl if="this.isImage(mimeType)">',
		            	'<p>文档: <a href="content/stream?id={id}" target="blank"><img src="content/stream?id={id}" alt="{name}" height="300" width="300"/></a></p>',
		            '</tpl>',
		            '<tpl if="this.isText(mimeType)">',
		            	'<p>文档: <iframe src="content/stream?id={id}" name="iframe_text"></iframe></p>',
		            '</tpl>',
		            '<tpl if="this.isPdf(mimeType)">',
		            	'<p>文档: <a href="pdf/web/viewer.html?{id}" target="blank">查看详细</a></p>',
		            '</tpl>',
		            '<tpl if="this.isMs(mimeType)">',
		            	'<p>文档: <a href="pdf/web/viewer.html?{id}&isCopy=true" target="blank">查看PDF副本</a></p>',
		            '</tpl>',
		            '<p><a href="content/stream?id={id}&isCopy=false" target="blank">下载原文件</a></p>',
		            
		            {
						formatSize : function(size){
							return Ext.util.Format.round(size/1024/1024, 2);
						},
						isImage : function(mimeType){
							return (mimeType.search("image") != -1);
						},
			            isText : function(mimeType){
			            	return (mimeType.search("text") != -1);
			            },
						isPdf : function(mimeType){
							return (mimeType.search("pdf") != -1);
						},
						isMs : function(mimeType){
							return (mimeType.search("ms") != -1);
						}
					}
		   ]
		});
		this.callParent(arguments);
	}
}); 