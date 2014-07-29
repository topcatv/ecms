Ext.define('ECM.controller.Content', {
	extend : 'Ext.app.Controller',
	stores : [ 'Content', 'ContentTree', 'Version', 'UsersForPermission' ],
	models : [ 'Content', 'Version', 'User' ],
	views : [ 'content.ContentGrid', 'content.CreateFolderWindow', 'content.UpdateFolderWindow', 'content.UpdateFileWindow',
			'content.Tree', 'LeftContainer', 'content.CreateFileWindow', 'content.HistoryWindow', 'content.DetailWindow', 'content.SearchWindow', 'content.ShareWindow' ],
	refs : [ {
		ref : 'contentGrid',
		selector : 'content_grid'
	}, {
		ref : 'folderWindow',
		selector : 'create_folder_window'
	}, {
		ref : 'updateFolderWindow',
		selector : 'update_folder_window'
	}, {
		ref : 'updateFileWindow',
		selector : 'update_file_window'
	}, {
		ref : 'tree',
		selector : 'contenttree'
	}, {
		ref : 'fileWindow',
		selector : 'create_file_window'
	}, {
		ref : 'historyWindow',
		selector : 'history_window'
	}, {
		ref : 'detailWindow',
		selector : 'detail_window'
	}, {
		ref : 'searchWindow',
		selector : 'search_window'
	}, {
		ref : 'shareWindow',
		selector : 'share_window'
	}],

	init : function() {
		Ext.create('ECM.view.content.CreateFolderWindow', {});
		Ext.create('ECM.view.content.UpdateFolderWindow', {});
		Ext.create('ECM.view.content.CreateFileWindow', {});
		Ext.create('ECM.view.content.UpdateFileWindow', {});
		Ext.create('ECM.view.content.HistoryWindow', {});
		Ext.create('ECM.view.content.DetailWindow', {});
		Ext.create('ECM.view.content.SearchWindow', {});
		Ext.create('ECM.view.content.ShareWindow', {});
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
			'content_grid button[action=delete]' : {
				click : this.deleteContent
			},
			'content_grid button[action=edit]' : {
				click : this.editContent
			},
			'content_grid button[action=show_history]' : {
				click : this.show_history
			},
			'content_grid button[action=search]' : {
				click : this.showSearch
			},
			'content_grid button[action=shareTo]' : {
				click : this.shareTo
			},
			'content_grid textfield' : {
				specialkey : this.fulltext
			},
			"content_grid" : {
				itemdblclick : this.detail
			},
			"history_window button[action=restore]" : {
				click : this.restore
			},
			"history_window button[action=view]" : {
				click : this.viewVersion
			},
			'create_folder_window button[action=create]' : {
				click : this.createFolder
			},
			'update_folder_window button[action=folder_rename]' : {
				click : this.renameFolder
			},
			'update_file_window button[action=file_modify]' : {
				click : this.updateFile
			},
			'search_window button[action=search]' : {
				click : this.search
			},
			'share_window button[action=share]' : {
				click : this.share
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
				select : this.tree_itemSelected
			}
		});
	},
	
	share: function(button){
		var grid = button.up('window').down('grid'), cm = grid.getSelectionModel(), user_seleted = cm.getSelection();
		var content_selected = this.getContentGrid().getSelectionModel().getSelection();
		var user_array = [];
		var content_array = [];
		var current_user = Ext.getDom('user_name').innerHTML;
		var flag = false;
		content_selected.forEach(function(element, index, array){
			if(current_user != element.get("creator")){
				flag = true;
				return;
			}
			content_array.push(element.get("id"));
		});
		if(flag){
			Ext.Msg.alert("提示信息","只能共享你创建的文档");
			return;
		}
		user_seleted.forEach(function(element, index, array){
			user_array.push(element.get('id'));
		});
		var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在共享..."});
		myMask.show();
		Ext.Ajax.request({
		    url: 'permission/authorize',
		    params: {'cids': content_array, 'uids': user_array, 'permission': 3},
		    method: 'POST',
		    success: function(response, opts) {
		        var obj = Ext.decode(response.responseText);
		        if (myMask != undefined){ myMask.hide();}
		        Ext.Msg.alert("提示信息","共享成功");
		    },
		    failure: function(response, opts) {
		    	var result = Ext.decode(response.responseText);
		        console.log('server-side failure with status code ' + response.status);
		        if (myMask != undefined){ myMask.hide();}
		        Ext.Msg.alert("提示信息",result.data);
		    }
		});
	},
	shareTo: function(button){
		this.getShareWindow().show();
	},
	search: function(button){
		var win = button.up('window'), form = win.down('form');
		var _this = this;
		var content_store = this.getStore('Content');
		form.submit({
			url : 'content/search',
			method : 'GET',
			type : 'ajax',
			waitMsg : '正在查询...',
			success : function(f, action) {
				var result = Ext.decode(action.response.responseText);
				console.debug(result.children);
				win.hide();
				content_store.removeAll();
				content_store.add(result.children);
			},
			failure : function(f, action) {
				var result = Ext.decode(action.response.responseText);
				Ext.Msg.alert("提示信息",result.data);
			}
		});
	},
	showSearch:function(button){
		var searchWin = this.getSearchWindow();
		searchWin.show();
	},	
	detail: function(grid, record){
		if(record.get("folder")){
			this.getStore('Content').reload({
				params : {
					parent : record.get('id')
				}
			});
		} else {
			var detail = this.getDetailWindow();
			detail.update(record.getData());
			detail.show();
		}
	},
	viewVersion : function(button){
		var grid = button.up('grid'), cm = grid.getSelectionModel(), seleted = cm.getLastSelected();
		var versionName = seleted.get('name');
		var id = this._getGridSelectedIds()[0];
		var detail = this.getDetailWindow();
		Ext.Ajax.request({
			url: 'content/version',
			params: {'id': id, 'versionName': versionName},
			method: 'GET',
			success: function(response, opts) {
				var obj = Ext.decode(response.responseText);
				console.dir(obj);
				detail.update(obj.file);
				detail.show();
			},
			failure: function(response, opts) {
				var result = Ext.decode(response.responseText);
				console.log('server-side failure with status code ' + response.status);
				Ext.Msg.alert("提示信息",result.data);
			}
		});
	},
	restore : function(button){
		var grid = button.up('grid'), cm = grid.getSelectionModel(), seleted = cm.getLastSelected();
		var versionName = seleted.get('name');
		var _this = this;
		Ext.Msg.confirm("提示信息","确定要恢复到版本'"+versionName+"'吗？", function(button, text){
			if(button == "yes"){
				var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在恢复..."});
				myMask.show();
				var id = this._getGridSelectedIds()[0];
				var parentNode = this._getTreeSelected();
				Ext.Ajax.request({
				    url: 'content/restore',
				    params: {'id': id, 'versionName': versionName},
				    method: 'POST',
				    success: function(response, opts) {
				        var obj = Ext.decode(response.responseText);
				        console.dir(obj);
				        if (myMask != undefined){ myMask.hide();}
				        _this.getStore('Content').reload({
							params : {
								parent : parentNode.get('id')
							}
						});
				        _this.getStore('ContentTree').reload();
				        Ext.Msg.alert("提示信息","恢复成功");
				    },
				    failure: function(response, opts) {
				    	var result = Ext.decode(response.responseText);
				        console.log('server-side failure with status code ' + response.status);
				        if (myMask != undefined){ myMask.hide();}
				        Ext.Msg.alert("提示信息",result.data);
				    }
				});
			}
		}, _this);
	},
	fulltext : function(field, e){
		if (e.getKey() == e.ENTER) {
            var keywords = field.getValue();
            var url = this.getStore('Content').getProxy().url;
            this.getStore('Content').getProxy().url = 'content/full_text'
            this.getStore('Content').reload({
				params : {
					keywords : keywords
				}
			});
            this.getStore('Content').getProxy().url = url;
        }
	},
	updateFile : function(button) {
		var win = button.up('window'), form = win.down('form');
		var _this = this;
		var parentNode = this._getTreeSelected();
		form.submit({
			url : 'content/file',
			method : 'PUT',
			type : 'ajax',
			waitMsg : '正在修改文件...',
			success : function(f, action) {
				win.hide();
				_this.getStore('Content').reload({
					params : {
						parent : parentNode.get('id')
					}
				});
				Ext.Msg.alert("提示信息",'修改成功');
				f.reset();
			},
			failure : function(f, action) {
				var result = Ext.decode(action.response.responseText);
				Ext.Msg.alert("提示信息",result.data);
			}
		});
	},
	renameFolder : function(button){
		var win = button.up('window'), form = win.down('form');
		var _this = this;
		var parentNode = this._getTreeSelected();

		form.submit({
			url : 'content/folder',
			method : 'PUT',
			type : 'ajax',
			waitMsg : '正在修改文件夹名...',
			success : function(f, action) {
				win.hide();
				_this.getStore('Content').reload({
					params : {
						parent : parentNode.get('id')
					}
				});
				_this.getStore('ContentTree').reload();
				Ext.Msg.alert("提示信息",'修改成功');
				f.reset();
			},
			failure : function(f, action) {
				var result = Ext.decode(action.response.responseText);
				Ext.Msg.alert("提示信息",result.data);
			}
		});
	},
	editContent : function(button){
		var selected = this._getGridSelected(), record = selected[0];
		if(selected.length <= 0){
			Ext.Msg.alert("提示信息", '请选择一个文档');
			return;
		}
		if(selected.length > 1){
			Ext.Msg.alert("提示信息", '只能选择一个文档');
			return;
		}
		if(record.get("folder")){
			var win = this.getUpdateFolderWindow();
			var form = win.down('form');
			form.getForm().findField('id').setValue(record.get("id"));
			form.getForm().findField('name').setValue(record.get("name"));
			win.show();
		} else {
			var win = this.getUpdateFileWindow();
			var form = win.down('form');
			form.getForm().findField('id').setValue(record.get("id"));
			form.getForm().findField('fileName').setValue(record.get("name"));
			win.show();
		}
	},
	show_history : function(button){
		var ids = this._getGridSelectedIds();
		if(ids.length > 1){
			Ext.Msg.alert("提示信息", '只能选择一个文档');
			return;
		}
		if(ids.length > 0){
			if(this._getGridSelected()[0].get('folder')){
				Ext.Msg.alert("提示信息", '文件夹没有历史');
				return;
			}
			var version_store = this.getStore("Version");
			version_store.load({
				params : {
					id : ids[0]
				}
			});
			this.getHistoryWindow().show();
		}
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
		var parentNode = this._getTreeSelected();
		if (parentNode) {
			form.getForm().findField('parent')
					.setRawValue(parentNode.get('id'));
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
					Ext.Msg.alert("提示信息",'文件上传成功');
					f.reset();
				},
				failure : function(f, action) {
					var result = Ext.decode(action.response.responseText);
					Ext.Msg.alert("提示信息",result.data);
				}
			});
		}
	},
	createFolder : function(button) {
		var win = button.up('window'), form = win.down('form');
		var _this = this;
		var parentNode = this._getTreeSelected();
		if (parentNode) {
			form.getForm().findField('parent')
					.setRawValue(parentNode.get('id'));
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
				_this.getStore('ContentTree').reload();
				Ext.Msg.alert("提示信息",'文件夹创建成功');
				f.reset();
			},
			failure : function(f, action) {
				var result = Ext.decode(action.response.responseText);
				Ext.Msg.alert("提示信息",result.data);
			}
		});
	},
	tree_itemSelected : function(row, record, index, eOpts) {
		Ext.getCmp('mainTab').setActiveTab(this.getContentGrid());
		this.getStore('Content').load({
			params : {
				parent : record.get('id')
			}
		});
	},
	deleteContent : function(button) {
		var _this = this;
		Ext.Msg.confirm("提示信息","确定删除吗？", function(button, text){
			if(button == "yes"){
				var ids = this._getGridSelectedIds();
				var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在删除..."});
				myMask.show();
				var parentNode = this._getTreeSelected();
				Ext.Ajax.request({
				    url: 'content/delete',
				    params: {'ids': ids},
				    method: 'PUT',
				    success: function(response, opts) {
				        var obj = Ext.decode(response.responseText);
				        console.dir(obj);
				        if (myMask != undefined){ myMask.hide();}
				        _this.getStore('Content').reload({
							params : {
								parent : parentNode.get('id')
							}
						});
				        _this.getStore('ContentTree').reload();
				        Ext.Msg.alert("提示信息","删除成功");
				    },
				    failure: function(response, opts) {
				    	var result = Ext.decode(response.responseText);
				        console.log('server-side failure with status code ' + response.status);
				        if (myMask != undefined){ myMask.hide();}
				        Ext.Msg.alert("提示信息",result.data);
				    }
				});
			}
		}, _this);
	},
	_getGridSelected : function(){
		var cm = this.getContentGrid().getSelectionModel(), seleted = cm.getSelection();
		return seleted;
	},
	_getTreeSelected : function(){
		return this.getTree().getSelectionModel().getLastSelected();
	},
	_getGridSelectedIds : function(){
		var seleted = this._getGridSelected();
		var ids = [];
		Ext.Array.forEach(seleted, function(modle){
			ids.push(modle.get('id'));
		});
		return ids;
	}
});
