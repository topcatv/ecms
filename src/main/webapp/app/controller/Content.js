Ext.define('ECM.controller.Content', {
	extend : 'Ext.app.Controller',
	stores : [ 'Content', 'ContentTree' ],
	models : [ 'Content' ],
	views : [ 'content.ContentGrid', 'content.CreateFolderWindow',
			'content.Tree', 'LeftContainer', 'content.CreateFileWindow' ],
	refs : [ {
		ref : 'contentGrid',
		selector : 'content_grid'
	}, {
		ref : 'folderWindow',
		selector : 'create_folder_window'
	}, {
		ref : 'tree',
		selector : 'contenttree'
	}, {
		ref : 'fileWindow',
		selector : 'create_file_window'
	} ],

	init : function() {
		Ext.create('ECM.view.content.CreateFolderWindow', {});
		Ext.create('ECM.view.content.CreateFileWindow', {});
		this.control({
			'create_file_window button[action=upload]' : {
				click : this.createFile
			},
			'content_grid menuitem[action=createFolder]' : {
				click : this.openCreateFolderWindow
			},
			'content_grid menuitem[action=createFile]' : {
				click : this.openCreateFileWindow
			},
			'create_folder_window button[action=create]' : {
				click : this.createFolder
			},
			'contenttree' : {
				render : function(t, eOpts) {
					tree = t;
				},
				beforeitemexpand : function(node, eOpts) {
					Ext.apply(tree.getStore().proxy.extraParams, {
						parent : node.data.id
					});
				},
				select : this.itemSelected
			}
		});
	},

	openCreateFolderWindow : function() {
		this.getFolderWindow().show();
	},
	openCreateFileWindow : function() {
		this.getFileWindow().show();
	},
	createFile : function(button) {
		var win = button.up('window'), form = win.down('form');
		var _this = this;
		var parentNode = this.getTree().getSelectionModel().getLastSelected();
		if (parentNode) {
			form.getForm().findField('parent').setRawValue(parentNode.get('id'));
		}
		if (form.isValid()) {
			form.submit({
				url : 'content/file',
				method : 'POST',
				type : 'ajax',
				waitMsg : '正在保存文件...',
				success : function(f, action) {
					win.hide();
					_this.getStore('Content').reload({
						params : {
							parent : parentNode.get('id')
						}
					});
					Ext.Msg.show({
						title : '提示信息',
						msg : '文件上传成功<br>上传文件名为：' + action.result.file,
						minWidth : 200,
						modal : true,
						buttons : Ext.Msg.OK
					})
					f.reset();
				},
				failure : function(f, action) {
					var result = Ext.decode(action.response.responseText);
					Ext.Msg.show({
						title : '提示信息',
						msg : result.data,
						minWidth : 200,
						modal : true,
						buttons : Ext.Msg.OK
					})
				}
			});
		}
	},
	createFolder : function(button) {
		var win = button.up('window'), form = win.down('form');
		var _this = this;
		var parentNode = this.getTree().getSelectionModel().getLastSelected();
		if (parentNode) {
			form.getForm().findField('parent').setRawValue(parentNode.get('id'));
		}

		form.submit({
			url : 'content/folder',
			method : 'POST',
			type : 'ajax',
			waitMsg : '正在创建文件夹...',
			success : function(f, action) {
				win.hide();
				_this.getStore('Content').reload({
					params : {
						parent : parentNode.get('id')
					}
				});
				Ext.Msg.show({
					title : '提示信息',
					msg : '文件夹创建成功：' + action.result.file,
					minWidth : 200,
					modal : true,
					buttons : Ext.Msg.OK
				})
				f.reset();
			},
			failure : function(f, action) {
				var result = Ext.decode(action.response.responseText);
				Ext.Msg.show({
					title : '提示信息',
					msg : result.data,
					minWidth : 200,
					modal : true,
					buttons : Ext.Msg.OK
				})
			}
		});
	},
	itemSelected : function(row, record, index, eOpts) {
		this.getStore('Content').load({
			params : {
				parent : record.get('id')
			}
		});
	}
});
