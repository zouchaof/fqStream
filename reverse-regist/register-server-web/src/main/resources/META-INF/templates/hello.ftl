<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>demo</title>
    <link rel="stylesheet" href="/static/layui/css/layui.css">
</head>
<body>

<div class="layui-container">
    <div class="layui-progress" style="margin: 15px 0 30px;">
        <div class="layui-progress-bar" lay-percent="100%"></div>
    </div>


    <div class="layui-btn-container">
        <button class="layui-btn" test-active="test-form">一个按钮1</button>
        <button class="layui-btn layui-btn-normal" id="test2">当前日期</button>
    </div>

    <div class="layui-form-item">
        <label class="layui-form-label">单行选择框</label>
        <div class="layui-input-block">
            <select name="interest" lay-filter="demo-select-filter">
                <option value=""></option>
                <option value="0">写作</option>
                <option value="1" selected>阅读</option>
                <option value="2">游戏</option>
                <option value="3">音乐</option>
                <option value="4">旅行</option>
            </select>
        </div>
    </div>


    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">验证手机号</label>
            <div class="layui-form">
                <select lay-filter="demo-select-filter">
                    <option value="">请选择</option>
                    <option value="AAA">选项 A</option>
                    <option value="BBB">选项 B</option>
                    <option value="CCC">选项 C</option>
                </select>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">验证手机号</label>
            <div class="layui-input-inline layui-input-wrap">
                <input type="tel" name="phone" lay-verify="required|phone" autocomplete="off" lay-reqtext="请填写手机号" lay-affix="clear" class="layui-input demo-phone">
            </div>
            <div class="layui-form-mid" style="padding: 0!important;">
                <button type="button" class="layui-btn layui-btn-primary" lay-on="get-vercode">获取验证码</button>
            </div>
        </div>
    </div>

    <table class="layui-hide" id="ID-table-demo-search"></table>

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
    <a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="detail">查看</a>
    <a class="layui-btn layui-btn-xs" lay-event="more">更多 <i class="layui-icon layui-icon-down"></i></a>
</script>
<script>
    layui.use(function(){
        var layer = layui.layer;
        var table = layui.table;
        var form = layui.form;
        var laydate = layui.laydate;
        var util = layui.util;


        // select 事件
        form.on('select(demo-select-filter)', function(data){
            var elem = data.elem; // 获得 select 原始 DOM 对象
            var value = data.value; // 获得被选中的值
            var othis = data.othis; // 获得 select 元素被替换后的 jQuery 对象

            layer.msg(this.innerHTML + ' 的 value: '+ value); // this 为当前选中 <option> 元素对象
        });


        table.render({
            elem: '#ID-table-demo-search',
            url: '/rv-manage/select', // 此处为静态模拟数据，实际使用时需换成真实接口
            cols: [[
                {field:'ID', title: 'ID', width:80, sort: true},
                {field:'APP_NAME', title: '应用名称', width:180, align:'center'},
                {field:'MAPPING_PATH', title: '路由路径', width:380, align:'center'},
                {field:'CREATE_TIME', title: '操作时间', width:180, align:'center'},
                {title: '操作', width: 250, align:'center', toolbar: '#barDemo'}
            ]],
            width:1078,
            page: true
        });

        // 单元格工具事件
        table.on('tool(ID-test-table)', function(obj){
            var data = obj.data; // 获得当前行数据
            var layEvent = obj.event; // 获得 lay-event 对应的值

            if(layEvent === 'detail'){
                layer.msg('查看操作');
            } else if(layEvent === 'more'){
                // 下拉菜单
                dropdown.render({
                    elem: this, // 触发事件的 DOM 对象
                    show: true, // 外部事件触发即显示
                    data: [{
                        title: '编辑'
                        ,id: 'edit'
                    }, {
                        title: '删除'
                        ,id: 'del'
                    }],
                    click: function(menudata){
                        if(menudata.id === 'del'){
                            layer.confirm('真的删除行么', function(index){
                                obj.del(); // 删除对应行（tr）的DOM结构
                                layer.close(index);
                                // 向服务端发送删除指令
                            });
                        } else if(menudata.id === 'edit'){
                            layer.msg('编辑操作，当前行 ID:'+ data.id);
                        }
                    },
                    align: 'right', // 右对齐弹出
                    style: 'box-shadow: 1px 1px 10px rgb(0 0 0 / 12%);' // 设置额外样式
                })
            }
        });




        // 日期
        laydate.render({
            elem: '#test2',
            value: new Date(),
            isInitValue: true
        });

        // 触发事件
        util.on('test-active', {
            'test-form': function(){
                layer.open({
                    type: 1,
                    resize: false,
                    shadeClose: true,
                    area: '350px',
                    title: 'layer + form',
                    content: ['<ul class="layui-form layui-form-pane" style="margin: 15px;">',
                        '<li class="layui-form-item">',
                        '<label class="layui-form-label">输入框</label>',
                        '<div class="layui-input-block">',
                        '<input class="layui-input" lay-verify="required" name="field1">',
                        '</div>',
                        '</li>',
                        '<li class="layui-form-item">',
                        '<label class="layui-form-label">选择框</label>',
                        '<div class="layui-input-block">',
                        '<select name="field2">',
                        '<option value="A">A</option>',
                        '<option value="B">B</option>',
                        '<select>',
                        '</div>',
                        '</li>',
                        '<li class="layui-form-item" style="text-align:center;">',
                        '<button type="submit" lay-submit lay-filter="*" class="layui-btn">提交</button>',
                        '</li>',
                        '</ul>'].join(''),
                    success: function(layero, index){
                        layero.find('.layui-layer-content').css('overflow', 'visible');

                        form.render().on('submit(*)', function(data){
                            var field = data.field;

                            // 显示填写的表单
                            layer.msg(util.escape(JSON.stringify(field)), {
                                icon: 1
                            });
                            // layer.close(index); //关闭层
                        });
                    }
                });
            }
        });



    });
</script>
</body>
</html>
