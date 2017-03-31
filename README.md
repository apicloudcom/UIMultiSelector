# **概述**

支持多选、单选的择器模块（内含iOS和android）

APICloud 的 UIMultiSelector 模块是一个从屏幕底部弹出的选择器模块。由于本模块 UI 布局界面为固定模式，不能满足日益增长的广大开发者对侧滑列表模块样式的需求。因此，广大原生模块开发者，可以参考此模块的开发方式、接口定义等开发规范，或者基于此模块开发出更多符合产品设计的新 UI 布局的模块，希望此模块能起到抛砖引玉的作用。

# **模块接口文档**

<p style="color: #ccc; margin-bottom: 30px;">来自于：APICloud 官方</p>

<ul id="tab" class="clearfix">
	<li class="active"><a href="#method-content">Method</a></li>
</ul>

<div class="outline">

[open](#open)
[show](#show)
[hide](#hide)
[close](#close)

</div>

## **模块概述**

UIMultiSelector 封装了一个支持多选的选择器，开发者可自定义该选择器的样式及其数据源。当您的 APP 需要为用户同时提供多种可选项的支持时可以选择该控件快速配置使用节省开发时间。**该模块是 multiSelector 模块的优化版本**

![图片说明](http://docs.apicloud.com/img/docImage/multiSelector.jpg)


## [实例widget下载地址](https://github.com/XM-Right/UIMultiSelector-Example/archive/master.zip)


<div id="open"></div>

# **open**

打开选择器

open({params}, callback(ret))

## params

rect：

- 类型：JSON 对象
- 描述：（可选项）模块的位置及尺寸
- 内部字段：

```js
{
    h: 244                             //（可选项）数字类型；模块的高度；默认：244
}
```

text：

- 类型：JSON 对象类型
- 描述：（可选项）模块相应文字设置
- 内部字段：

```js
{
	title: '标题',                     //（可选项）字符串类型；模块左上按钮和右上按钮中间显示的标题文字，若不传则不显示
    leftBtn: '取消',                   //（可选项）字符串类型；模块左上按钮的显示文字；默认：取消
    rightBtn: '完成',                  //（可选项）字符串类型；模块右上按钮的显示文字；默认：完成
    selectAll: '全选'                  //（可选项）字符串类型；模块的全选项文字，若max值大于0，则本参数被忽略；默认：全选
}
```

max：

- 类型：数字类型
- 描述：（可选项）最多允许同时选中的项数；当值为 0 时可选中所有项，若本字段值大于0则全选项不显示；当值为其它时，选择超过 max 的选项，选项选择无效且回调 ret-> eventType 返回 “overflow” 
- 默认：0

singleSelection:

- 类型：布尔类型
- 描述：（可选项）是否为单选框；参数值为 true 时，忽略参数 max，模块呈 “单选项” 模式状态，即选择第二项将自动取消前一项的已选中状态
- 默认：false

styles：

- 类型：JSON 类型
- 描述：（可选项）模块的样式设置
- 内部字段：

```js
{
    bg: '#fff',                        //（可选项）字符串类型；主体的背景，支持 rgb，rgba，#，widget://，fs://；默认：#fff
    mask: 'rgba(0,0,0,0.3)',           //（可选项）字符串类型；遮罩层的背景，支持 rgb、rgba、#、img；默认：rgba(0,0,0,0)
    title: {                           //（可选项）JSON 类型；标题栏样式；默认：见内部字段
        bg: '#ddd',                    //（可选项）字符串类型；标题栏的背景，支持 rgb、rgba、#、img；默认：#ddd
        color:'#444',                  //（可选项）字符串类型；标题字体颜色，支持 rgb、gba、#；默认：#444
        size: 16,                      //（可选项）数字类型；标题字体大小；默认：16
        h: 44                          //（可选项）数字类型；标题栏的高度；默认：44
    },
    leftButton: {                      //（可选项）JSON 类型；左上角按钮样式；默认：见内部字段
        bg: '#f00',                    //（可选项）字符串类型；按钮的背景，支持 rgb、rgba、#、img；默认：#f00
        w: 80,                         //（可选项）数字类型；按钮的宽度；默认：80
        h: 35,                         //（可选项）数字类型；按钮的高度；默认：35
        marginT: 5,                    //（可选项）数字类型；按钮的上边距；默认：5
        marginL: 8,                    //（可选项）数字类型；按钮的左边距；默认：8
        color: '#fff',                 //（可选项）字符串类型；按钮的文字颜色，支持 rgb、rgba、#；默认：#fff
        size: 14                       //（可选项）数字类型；按钮的文字大小；默认：14
    },
    rightButton: {                     //（可选项）JSON 类型；右上角按钮样式；默认：见内部字段
        bg: '#0f0',                    //（可选项）字符串类型；按钮的背景，支持 rgb、rgba、#、img；默认：#0f0
        w: 80,                         //（可选项）数字类型；按钮的宽度；默认：80
        h: 35,                         //（可选项）数字类型；按钮的高度；默认：35
        marginT: 5,                    //（可选项）数字类型；按钮的上边距；默认：5
        marginR: 8,                    //（可选项）数字类型；按钮的右边距；默认：8
        color: '#fff',                 //（可选项）字符串类型；按钮的文字颜色，支持 rgb、rgba、#；默认：#fff
        size: 14                       //（可选项）数字类型；按钮的文字大小；默认：14
    },
    item: {                            //（可选项）JSON 类型；每个选项的样式；默认：见内部字段
        h: 44,                         //（可选项）数字类型；按钮的高度；默认：35
        bg: '#fff',                    //（可选项）字符串类型；选项的背景，支持 rgb、rgba、#、img；默认：#fff
        bgActive: '#fff',              //（可选项）字符串类型；已选中选项的背景，支持 rgb、rgba、#、img；默认：bg
        bgHighlight: '#fff',           //（可选项）字符串类型；选项的高亮背景，支持 rgb、rgba、#、img；默认：bg
        color: '#444',                 //（可选项）字符串类型；选项的文字颜色，支持 rgb，rgba，#；默认：#444
        active: '#444',                //（可选项）字符串类型；已选中选项的文字颜色，支持 rgb、rgba、#；默认：color
        highlight: '#444',             //（可选项）字符串类型；选项的高亮文字颜色，支持 rgb、rgba、#；默认：color
        size: 14,                      //（可选项）数字类型；选项的文字大小；默认：14
        lineColor: '#ccc',             //（可选项）字符串类型；分隔线的颜色，支持 rgb、rgba、#；默认：rgba(0,0,0,0)
        textAlign: 'left'              //（可选项）字符串类型；选项文字的对齐方式，'left|center|right'，当值为 left 或 right 时文字会根据情况空留 icon 已占的位置；默认：left
    },
    icon: {                            //（可选项）JSON 类型；每个选项的状态图标样式，若不传则不显示选中的状态图标
        w: 20,                         //（可选项）数字类型；图标的高度；默认：20
        h: 20,                         //（可选项）数字类型；图标的高度；默认：w
        marginT: 11,                   //（可选项）数字类型；图标的上边距；默认：(item.h - h) / 2
        marginH: 8,                    //（可选项）数字类型；图标的横向边距，与 align 的对齐方向相关；默认：8
        bg: '#fff',                    //（可选项）字符串类型；图标未选中时的背景，支持 rgb、rgba、#、img；默认：rgba(0,0,0,0)
        bgActive: '#fff',              //（可选项）字符串类型；已选中图标的背景，支持 rgb、rgba、#、img；默认：bg
        bgHighlight: '#fff',           //（可选项）字符串类型；选项的高亮背景，支持 rgb、rgba、#、img；默认：bg
        align: 'left'                  //（可选项）字符串类型；图标相对与选项的对齐方式：'left|right'；默认：left
    }
}

```

animation：

- 类型：布尔
- 描述：（可选项）打开关闭时是否显示滑入滑出动画
- 默认：true


maskClose：

- 类型：布尔
- 描述：（可选项）用户点击遮罩层（选择器以外的区域）时，是否关闭选择器
- 默认：true

items：

- 类型：JSON 数组类型
- 描述：选择器的数据
- 内部字段：

```js
[{
    text: '选项0',               //字符串类型；选项的文字内容
    status: 'normal',            //字符串类型；选项状态，取值范围如下；默认：normal
                                 //normal：未选中
                                 //selected：已选中
                                 //disable：不可选中
                                 //forever：不可取消
}]
```

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    eventType:                         //字符串类型；事件交互类型，取值范围如下：
                                       // show：显示完成
                                       // clickRight： 点击右上按钮  
                                       // clickLeft：点击左上按钮
                                       // overflow：用户选择项大于 open 时设置的 max 值
                                       // clickItem：用户点击了列表选项事件
                                       // clickMask：用户点击了选择器区域以外的遮罩层事件
    items:                             //数组类型；返回当前用户所选择的数据项数据，内容格式同传入的 items （支持自定义字段），当 eventType 为 overflow 时本参数无效
}
```

## 示例代码

```js
 var UIMultiSelector = api.require('UIMultiSelector');
 UIMultiSelector.open({
 	rect: {
 		h: 244
 	},
 	text: {
 		title: 'UIMultiSelector',
 		leftBtn: 'Cancel',
 		rightBtn: 'Finish',
 		selectAll: 'ALL'
 	},
 	max: 0,
 	singleSelection: true,
 	styles: {
 		mask: 'rgba(0,0,0,0)',
 		title: {
 			bg: 'rgb(0,64,128)',
 			color: 'rgb(184,209,5)',
 			size: 16,
 			h: 44
 		},
 		leftButton: {
 			w: 80,
 			h: 35,
 			marginT: 5,
 			marginL: 8,
 			color: 'rgb(0,0,0)',
 			size: 14
 		},
 		rightButton: {
 			w: 80,
 			h: 35,
 			marginT: 5,
 			marginR: 8,
 			color: 'rgb(0,0,0)',
 			size: 14
 		},
 		item: {
 			h: 35,
 			bg: 'rgb(121,82,173)',
 			bgActive: 'rgb(43,213,166)',
 			bgHighlight: 'rgb(238,17,150)',
 			color: 'rgb(22,112,233)',
 			active: 'rgb(201,118,126)',
 			highlight: 'rgb(255,255,0)',
 			size: 14,
 			lineColor: 'rgb(78,57,255)',
 			textAlign: 'center'
 		},
 		icon: {
 			w: 20,
 			h: 20,
 			marginT: 11,
 			marginH: 8,
 			bg: '#fff',
 			align: 'left'
 		}
 	},
 	animation: true,
 	items: [{
 		text: 'Monday',
 		status: 'normal'
 	}, {
 		text: 'Tuesday',
 		status: 'normal'
 	}, {
 		text: 'Wednesday',
 		status: 'normal'
 	}, {
 		text: 'Thursday',
 		status: 'normal'
 	}, {
 		text: 'Friday',
 		status: 'normal'
 	}, {
 		text: 'Saturday',
 		status: 'normal'
 	}, {
 		text: 'Sunday',
 		status: 'normal'
 	}, {
 		text: 'from Monday to Sunday',
 		status: 'normal'
 	}]
 }, function(ret, err) {
 	if (ret) {
 		alert(JSON.stringify(ret));
 	} else {
 		alert(JSON.stringify(err));
 	}
 });
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="close"></div>

# **close**

关闭选择器（从内存清除）

close()

## 示例代码

```js
var multiSelector = api.require('UIMultiSelector');
UIMultiSelector.close();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="show"></div>

# **show**

显示已隐藏的选择器

show()

## 示例代码

```js
var UIMultiSelector = api.require('UIMultiSelector');
UIMultiSelector.show();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本


<div id="hide"></div>

# **hide**

隐藏选择器（并未从内存清除）

hide()

## 示例代码

```js
var UIMultiSelector = api.require('UIMultiSelector');
UIMultiSelector.hide();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本
