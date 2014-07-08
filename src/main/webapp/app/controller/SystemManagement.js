Ext.define('ECM.controller.SystemManagement', {
	extend : 'Ext.app.Controller',
	stores : [ 'SystemManagement', 'Users', 'Roles', 'ContentTreeForPermission', 'ContentForPermission', 'UsersForPermission' ],
	views : [ 'SystemManagement', 'user.List', 'role.List', 'privilege.Authorize', 'privilege.Tree', 'privilege.ContentGrid', 'privilege.AuthorizeWindow' ],
	refs : [ {
		ref : 'systemManagement',
		selector : 'systemManagement'
	}, {
		ref : 'userList',
		selector : 'userlist'
	}, {
		ref : 'authorize',
		selector : 'authorize'
	}, {
		ref : 'authorizeWindow',
		selector : 'authorize_window'
	}, {
		ref : 'tree',
		selector : 'permissiontree'
	},{
		ref: 'contentGrid',
		selector: 'content_grid_for_permission'
	}, {
		ref : 'roleList',
		selector : 'rolelist'
	}],
	init : function() {
		Ext.create('ECM.view.user.List', {});
		Ext.create('ECM.view.privilege.Authorize', {});
		Ext.create('ECM.view.privilege.AuthorizeWindow', {});
		Ext.create('ECM.view.role.List', {});
		this.control({
			'systemManagement' : {
				itemclick : this.itemclick,
			},
			'content_grid_for_permission button[action=authorize]' : {
				click : this.showAuthorize
			},
			'authorize_window button[action=authorize]' : {
				click : this.authorize
			},
			'#cb_all' : {
				change : this.cb_all_change
			},
			'permissiontree' : {
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
	
	cb_all_change: function(self, nv, ov){
		Ext.getCmp('cb_write').setValue(nv);
		Ext.getCmp('cb_read').setValue(nv);
	},
	authorize: function(button){
		var grid = button.up('window').down('grid'), cm = grid.getSelectionModel(), user_seleted = cm.getSelection();
		var content_selected = this.getContentGrid().getSelectionModel().getSelection();
		var user_array = [];
		var content_array = [];
		user_seleted.forEach(function(element, index, array){
			user_array.push(element.get('id'));
		});
		content_selected.forEach(function(element, index, array){
			content_array.push(element.get("id"));
		});
		var can_write = Ext.getCmp('cb_write').getValue();
		var can_read = Ext.getCmp('cb_read').getValue();
		var permission = 0;
		if(can_read){
			permission += 1;
		}
		if(can_write){
			permission += 2;
		}
		var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在授权..."});
		myMask.show();
		Ext.Ajax.request({
		    url: 'permission/authorize',
		    params: {'cids': content_array, 'uids': user_array, 'permission': permission},
		    method: 'POST',
		    success: function(response, opts) {
		        var obj = Ext.decode(response.responseText);
		        if (myMask != undefined){ myMask.hide();}
		        Ext.Msg.alert("提示信息","授权成功");
		    },
		    failure: function(response, opts) {
		    	var result = Ext.decode(response.responseText);
		        console.log('server-side failure with status code ' + response.status);
		        if (myMask != undefined){ myMask.hide();}
		        Ext.Msg.alert("提示信息",result.data);
		    }
		});
	},
	showAuthorize : function(){
		var content_selected = this.getContentGrid().getSelectionModel().getSelection();
		if(content_selected.length <= 0){
			Ext.Msg.alert("提示信息",'请至少选择一项');
			return;
		}
		this.getAuthorizeWindow().show();
	},
	tree_itemSelected : function(row, record, index, eOpts) {
		this.getStore('ContentForPermission').load({
			params : {
				parent : record.get('id')
			}
		});
	},
	itemclick : function(view, record, item, index, e) {
		var id = record.get('id');
		var tabPanel = Ext.getCmp('mainTab');
		if (id == 'userManagement') {
			var userList = this.getUserList();
			if(!tabPanel.contains(userList)){
				userList.getStore().load();
				tabPanel.add(userList);
			}
			tabPanel.setActiveTab(userList);
		} else if (id == 'roleManagement') {
			var tabPanel = Ext.getCmp('mainTab');
			var userList = this.getRoleList();
			if(!tabPanel.contains(userList)){
				userList.getStore().load();
				tabPanel.add(userList);
			}
			tabPanel.setActiveTab(userList);
		}
		if (id == 'dataPrivilege') {
			var authorize = this.getAuthorize();
			if(!tabPanel.contains(authorize)){
				tabPanel.add(authorize);
			}
			tabPanel.setActiveTab(authorize);
		}
	}
});