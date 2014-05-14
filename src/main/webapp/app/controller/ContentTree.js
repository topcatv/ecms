Ext.define('ECM.controller.ContentTree',{
    extend : 'Ext.app.Controller',
    init : function(){
        var tree = null;
        this.control({
            'contenttree':{
                render:function(t, eOpts){
                    tree = t;
                },
                beforeitemexpand:function(node, eOpts ){
                    Ext.apply(tree.getStore().proxy.extraParams,{
                        id:node.data.id
                    });
                }
            }
        });
    },
    views:[
        'content.Tree'
    ],
    stores:[
        'ContentTree'
    ],
    models:[]
});