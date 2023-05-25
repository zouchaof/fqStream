<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>内网映射平台</title>
    <link rel="stylesheet" href="/static/layui/css/layui.css">
</head>
<body>

<div class="layui-container">

    <div class="layui-progress" style="margin: 15px 0 30px;">
        <div class="layui-progress-bar" lay-percent="100%"></div>
    </div>

    <form class="layui-form search_filter2" action="">
        <div class="layui-form-item">
            <div class="layui-inline" style="width: 310px">
                <label class="layui-form-label" style="width: 70px">匹配路径:</label>
                <div class="layui-input-inline" style="width: 200px;">
                    <input type="text" id="mappingPath" lay-verify="mappingPath"
                           class="layui-input" placeholder="匹配路径">
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label" style="width: 70px">应用名称:</label>
                <div class="layui-input-inline" style="width: 170px;">
                    <select id="appName" lay-filter="appName">
                        <option value="">全部</option>
                        <#if appNameSet??> 
                        <#list appNameSet as appName>  
                        <option value="${appName}"> ${appName} </option>
                        </#list> 
                        </#if> 
                    </select>
                </div>
            </div>

            <div class="layui-inline">
                <button class="layui-btn" lay-filter="qry" lay-submit="">&nbsp;&nbsp;查询&nbsp;&nbsp;</button>
                <button type="reset" id="reset" class="layui-btn">&nbsp;&nbsp;重置&nbsp;&nbsp;</button>
            </div>
        </div>
    </form>


    <div class="layui-form">
        <button class="layui-btn" lay-submit="" id="toAdd" lay-filter="toAdd">新增映射</button>
        <br/><br/>
        <table class="layui-hide" id="app-table" lay-filter="app-table"></table>
        <#--        <table class="layui-table" id="announcement-table" lay-filter="announcement-table"></table>-->
    </div>

    <blockquote class="layui-elem-quote" style="margin-top: 30px;">
        <div class="layui-text">
            <ul>
                <li>网络请求反转服务</li>
                <li>这是一个演示服务功能，请遵守国家相关法律法规</li>
            </ul>
        </div>
    </blockquote>
</div>

<!-- body 末尾处引入 layui -->
<script src="/static/layui/layui.js"></script>
<script type="text/html" id="barDemo">
    <a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="edit">编辑</a>
    <a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="del">删除</a>
</script>
<script type="text/html" id="addContent">
    <form class="layui-form" action="" lay-filter="addContentForm" style="margin-left: 40px;margin-top: 20px">
        <div class="layui-form-item">
            <label class="layui-form-label">*应用名称：</label>
            <div class="layui-input-inline" style="width: 250px">
                <select id="addAppName" lay-filter="addAppName">
                    <#if appNameSet??> 
                    <#list appNameSet as appName>  
                    <option value="${appName}"> ${appName} </option>
                    </#list> 
                    </#if> 
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">*路径路由：</label>
            <div class="layui-input-inline" style="width: 250px">
                <input type="text" id="addMappingPath" lay-verify="required" autocomplete="off" class="layui-input" value="">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">*服务地址：</label>
            <div class="layui-input-inline" style="width: 250px">
                <input type="text" id="addServerPath" lay-verify="required" autocomplete="off" class="layui-input" value="">
            </div>
        </div>
    </form>
</script>
<script>
    layui.use(function(){
        var layer = layui.layer;
        var table = layui.table;
        var form = layui.form;
        var $ = layui.jquery;

        table.render({
            elem: '#app-table',
            url: '/rv-manage/select', // 此处为静态模拟数据，实际使用时需换成真实接口
            cols: [[
                {field:'ID', title: 'ID', width:80, align:'center'},
                {field:'APP_NAME', title: '应用名称', width:100, align:'center'},
                {field:'MAPPING_PATH', title: '路由路径', width:280, align:'center'},
                {field:'SERVER_PATH', title: '服务地址', align:'center'},
                {field:'CREATE_TIME', title: '操作时间', width:180, align:'center'},
                {title: '操作', width: 150, align:'center', toolbar: '#barDemo'}
            ]],
            // width:1078,
            loading: true,
            even: true,
            where: {
                appName: $("#appName").val(),
                mappingPath:$("#mappingPath").val().trim()
            },
            page: true
        });

        // 监听查询事件
        form.on('submit(qry)', function (data) {
            loadTable();
            return false;
        });

        function loadTable() {
            table.reload('app-table', {
                where: {
                    appName: $("#appName").val(),
                    mappingPath:$("#mappingPath").val().trim()
                }
                , page: {
                    curr: 1
                }
            });
        }


        // 单元格工具事件
        table.on('tool(app-table)', function(obj){
            var data = obj.data; // 获得当前行数据
            var layEvent = obj.event; // 获得 lay-event 对应的值

            console.log(data.ID);
            if(layEvent === 'edit'){
                layer.msg('编辑操作');
            } else if(layEvent === 'del'){
                layer.confirm('真的删除该数据吗？', function(index){
                    // 向服务端发送删除指令
                    $.ajax({
                        url:'/rv-manage/delete',
                        type:'POST',
                        data:{id:data.ID},
                        success:function(res){
                            if(res.code === '0'){
                                loadTable();
                            }else{
                                layer.alert(res.msg);
                            }
                        }
                    });
                    layer.close(index);
                });
            }
        });


        $("#toAdd").on('click',function () {
            var index = layer.open({
                type: 1,
                area: ['580px', '330px'],
                title: '新增映射',
                btn: ['确定', '取消'],
                shadeClose: false,
                content: $("#addContent").html(),
                success:function(layero, index) {
                    layui.use(function() {
                        layui.form.render('select');
                    });
                },
                yes:function (index, layero) {
                    $.ajax({
                        url:'/rv-manage/add',
                        data:{appName:$("#addAppName").val(),
                            mappingPath:$("#addMappingPath").val().trim(),
                            serverPath:$("#addServerPath").val().trim()
                        },
                        success:function (res) {
                            if(res.code === '0'){
                                layer.close(index);
                            }
                        }
                    });

                },
                end: function(){loadTable();}
            });
        });


    });
</script>
</body>
</html>
