/*
 * BASE_URL
 * 测试环境与生产环境切换 BASE_URL
 */
var BASE_URL = "http://211.138.16.144/deskserver/";//测试环境
//var BASE_URL = "http://211.138.16.146/deskserver/";//生产环境
//var BASE_URL = "http://192.168.41.12/deskserver/";

/**
 * 手机号正则
 */
var phoneRegex = /^1[3|4|5|8][0-9]\d{4,8}$/;

var largeImgW = 900, largeImgH = 500;
var smallImgW = 100, smallImgH = 100;

//关于如何更新,自定义的常量
var CONSTANT_UPDATE_ID = {
	ALERT_UPDATE: '*?*',//提示更新
	MUST_UPDATE : '*!!*' //强制更新
}
/*
 * 角色（0：普通用户，1：管理员）
 */
var ROLE = {
	MEMBER:0,
	MANAGER:1
};

/**
 * 默认头像
 */
var TMP_PHOTO = {
	CUSTOMER: 'photo_01.png',//客户
	SERVICER: 'photo_01.png'//客服
}
/*头像*/
var photo = {
	//计算层级
	getPrefix: function(level){
		var prefix = '';
		for(var i=0; i<level; i++){
			prefix += '../'
		}
		prefix += 'res/';
		return prefix;
	},
	//path: 图片路径
	//level:层级
	get: function(path, level){
		if(!path){
			path = photo.getPrefix(level) + TMP_PHOTO.SERVICER;
		}else{
			//判断path是本地还是URL
			var isLocal = path.indexOf('local:')>=0;
			if(isLocal){
				name = path.split('local:').pop();
				path = photo.getPrefix(level) + name;
			}else{
				path = path;
			}
		}
		return path;
	}
}
/**
 * 融云消息类型
 */
var CONSTANT_RONG_MSG_TYPE = {
	TEXT	:	'RC:TxtMsg',
	IMG		:	'RC:ImgMsg',
	IMGTEXT	:	'RC:ImgTextMsg',
	VOICE	:	'RC:VcMsg',
	LOCATION:	'RC:LBSMsg'
};
/**
 * 融云会话类型
 */
var CONSTANT_RONG_CHAT_TYPE = {
	PRIVATE	: 'PRIVATE',// 单聊
	GROUP	: 'GROUP',// 群组
	DISCUSSION: 'DISCUSSION',// 讨论组
	CHATROOM: 'CHATROOM',// 聊天室
	SYSTEM	: 'SYSTEM',// 系统
	CUSTOMER_SERVICE: 'CUSTOMER_SERVICE'// 客服
}
var CONSTANT_MSG_OPERATION_TYPE = {
	NORMAL			:0,//：正常消息
	END				:1,//：结束工单，
	SEND_FAIL		:2,//：发送失败，
	SEND_CANCEL		:3,//：撤销消息，
	OUT_CALL		:4,//：客服外呼，
	OUT_CALL_CANCEL	:5,//：客服取消外呼，
	OUT_CALL_BEGIN	:6,//：外呼电话接起，
	OUT_CALL_END	:7,//：外呼电话结束，
	IN_CALL			:8,//：客户电话呼入；
	IN_CALL_BEGIN	:9,//：客户电话呼入接起；
	IN_CALL_END		:10//：客户电话呼入结束。
}
/**
 * 工单类型类型
 */
var ORDER_STATUS = {
    PENDING:'pending',
    PROCESSED:'processed'
};
/**
 * 用户登录状态
 * 1：在线
 * 2：暂离
 * 3：离线
 */
var CONSTANT_USER_STATUS = {
	ONLINE: 1,
	LEAVE: 2,
	OFFLINE: 3
}
/**
 * 渠道类型
 */
var CONSTANT_CHANNEL_TYPE = {
	SMS		: 'C1',//短信
	WECHAT	: 'C2',//微信
	CUSTOMER: 'C3',//客服之间
	CALL	: 'C4',//电话
	WEIBO	: 'C5',//微博
	APP		: 'C6',//APP插件
	H5		: 'C7',//H5插件 网页
	EMAIL	: 'C8' //邮件
}


var listView = {
	itemStyle4Order: {
        borderColor: "#F2F2F2", //item间分割线颜色，支持rgb，rgba，#，默认#696969，可为空
        bgColor: "#FFFFFF",    //item背景色，支持rgb，rgba，#，默认#AFEEEE，可为空
        selectedColor: "#c2c2c2", // item背景选中色,支持rgb，rgba，#，默认#f5f5f5可为空
        height: "61",     //一条item的高度，数字类型，默认55，可为空
        avatarH: "40",    //头像（上下居中）的高(不可超过height)，数字类型，默认45，可为空
        avatarW: "40",    //头像（距左边框距离和上下相等）的宽，数字类型，默认45，可为空
        placeholderImg: "widget://res/chat_C1.png",//头像为网络资源时的占位图,仅支持本地路径协议,有默认图标，可为空
        titleSize: "16",    //标题字体大小，数字类型，默认13，可为空
        titleColor: "#3C3C3C",     //标题字体颜色，支持rgb,rgba,#，默认：#696969,可为空
        subTitleSize: "14",  //子标题字体大小，数字类型，默认13，可为空
        subTitleColor: "#999999",  //子标题字体颜色，支持rgb,rgba,#，默认：#000000,可为空
        badge: {     //头像徽章设置背景色，可为空
            bg: '#F1431D',     //背景设置，支持rgb，rgba，#，默认#ff0000，可为空
            size: '14',    //徽章上的字体大小，数字类型，默认10，可为空
            color: '#FFFFFF'   //徽章上的字体颜色，支持支持rgb，rgba，#，默认#000000，可为空
			,xPercentage: 95//badge视图中心锚点坐标x在父视图（头像视图）的宽的百分比，数字类型，取值范围0-100
			,yPercentage: 10// badge视图中心锚点坐标y在父视图（头像视图）的高的百分比，数字类型，取值范围0-100
        }
        ,itemSlipDistance: "45"//向左滑动露出右边按钮时，item的滑动距离咱item宽的百分比，默认50，取值范围30-100，可为空
    },
	bean: function(data){
		var _default = {
			img				: 'widget://res/personal_photo.png',//photo
			title			: '默认用户',//标题-客户名
			subTitle		: '默认内容',//内容
			badge			: undefined, //头像徽章，字符串类型，可为空，为空则不显示
			icons			: '', //用户名后面的图标路径组成的数组,可为空,为空则不显示,支持本地图片
			rightBtn		: '', //数组对象,往左滑动item露出的按钮信息组成的数组，无默认值，可为空，为空时表示item不可向左滑动
			
			targetId		: 'tmp001',//客户ID
			channelType		: CONSTANT_CHANNEL_TYPE.SMS,//渠道
			conversationType: 'PRIVATE',//会话类型
			isTop			: false//是否置顶
		};
		var obj = $.extend(_default, data);
		return obj;
	},
	//open: function(data){},
	getById: function(id){},
	newLine: function(data){},
	refresh: function(){},
	filtrate: function(){},
	//设置为"待处理/已处理"
	setType: function(type){},
	reload: function(objListView, listViewDatas){
		objListView.reloadData({
            datas: listViewDatas
        })
	},
	end: function(objListView, index){
		try{
			objListView.deleteItem({
		        index: index
		    });
		}catch(e){
			api.alert({
				title: 'listview.end-error',
				msg: e
			})
		}
	},
	
	getSubTitle: function(objectName, text, draftText){
		var subTitle = '';
		if (objectName == CONSTANT_RONG_MSG_TYPE.TEXT ) {
            if (draftText) {
                subTitle = '[草稿]' + draftText;
            }else{
            	if(text.indexOf('time-line-msg')>=0){
            		subTitle = '工单结束';
            	}else if(text){
            		subTitle = text;
            	}else{
            		subTitle = '[不支持自定义表情]';
            	}
            }
        } else if (objectName == CONSTANT_RONG_MSG_TYPE.IMG ) {
            subTitle = '[图片]';
        } else if (objectName == CONSTANT_RONG_MSG_TYPE.IMGTEXT ) {
        	subTitle = '[链接]';
        } else if (objectName == CONSTANT_RONG_MSG_TYPE.LOCATION ) {
        	subTitle = '[地理位置]';
        } else if (objectName == CONSTANT_RONG_MSG_TYPE.VOICE ){
        	subTitle = '[语音]';
        }
        return subTitle;
	}
}

/**
 * 转换code --> message
 */
function err_code2msg(errCode){
	var errMsg = '未知错误';
	switch(errCode){
		case -2: errMsg = '发送处理失败'; break;
		case -1: errMsg = '该客户当前已有客服接待，您暂时不能发消息。'; break;
		case 405: errMsg = '在黑名单中'; break;
		case 2002: errMsg = '数据包不完整，请求数据包有缺失'; break;
		case 2003: errMsg = '服务器不可用'; break;
		case 2004: errMsg = '错误的令牌（Token），Token 解析失败，请重新向身份认证服务器获取 Token'; break;
		case 2005: errMsg = '可能是错误的 App Key，或者 App Key 被服务器积极拒绝'; break;
		case 2006: errMsg = '服务端数据库错误'; break;
		case 3001: errMsg = '服务器超时'; break;
		case 5004: errMsg = '服务器超时'; break;
		case -10000: errMsg = '未调用 init 方法进行初始化'; break;
		case -10001: errMsg = '未调用 connect 方法进行连接'; break;
		case -10002: errMsg = '输入参数错误'; break;
		default: errMsg = '未知错误D.';
	}
	return errMsg;
}
/**
 * Created by andy on 15-3-19.
 */
function fixIos7Bar(el) {
    var isAndroid = (/android/gi).test(navigator.appVersion);
    var strSV = api.systemVersion;
    var numSV = parseInt(strSV, 10);
    var strDM = api.systemType;
    if (!isAndroid && numSV >= 7) {
        el.css("padding-top", '20px');
    }
};
function trans(arg) {
    var result = '';
    try {
        result = eval('(' + arg + ')');
    } catch (e) {
        result = arg.slice(1, -1);
    } finally {

    }
    return result;
}
//聊天表情
//(不要修改路径，如果需要修改路径 请与【team/frm_team_chat.html】以及【chat/frm_order_chat.html】保持一致)

var qqface = {
	'/::)': '<img src="../../res/chatBox/emotion/Expression_1.png" />',
	'/::~': '<img src="../../res/chatBox/emotion/Expression_2.png" />',
	'/::B': '<img src="../../res/chatBox/emotion/Expression_3.png" />',
	'/::|': '<img src="../../res/chatBox/emotion/Expression_4.png" />',
	'/:8-)': '<img src="../../res/chatBox/emotion/Expression_5.png" />',
	'/::<': '<img src="../../res/chatBox/emotion/Expression_6.png" />',
	'/::$': '<img src="../../res/chatBox/emotion/Expression_7.png" />',
	'/::X': '<img src="../../res/chatBox/emotion/Expression_8.png" />',
	'/::Z': '<img src="../../res/chatBox/emotion/Expression_9.png" />',
	'/::\'(': '<img src="../../res/chatBox/emotion/Expression_10.png" />',
	'/::-|': '<img src="../../res/chatBox/emotion/Expression_11.png" />',
	'/::@': '<img src="../../res/chatBox/emotion/Expression_12.png" />',
	'/::P': '<img src="../../res/chatBox/emotion/Expression_13.png" />',
	'/::D': '<img src="../../res/chatBox/emotion/Expression_14.png" />',
	'/::O': '<img src="../../res/chatBox/emotion/Expression_15.png" />',
	'/::(': '<img src="../../res/chatBox/emotion/Expression_16.png" />',
	'/::+': '<img src="../../res/chatBox/emotion/Expression_17.png" />',
	'/:--b': '<img src="../../res/chatBox/emotion/Expression_18.png" />',
	'/::Q': '<img src="../../res/chatBox/emotion/Expression_19.png" />',
	'/::T': '<img src="../../res/chatBox/emotion/Expression_20.png" />',
	'/:,@P': '<img src="../../res/chatBox/emotion/Expression_21.png" />',
	'/:,@-D': '<img src="../../res/chatBox/emotion/Expression_22.png" />',
	'/::d': '<img src="../../res/chatBox/emotion/Expression_23.png" />',
	'/:,@o': '<img src="../../res/chatBox/emotion/Expression_24.png" />',
	'/::g': '<img src="../../res/chatBox/emotion/Expression_25.png" />',
	'/:|-)': '<img src="../../res/chatBox/emotion/Expression_26.png" />',
	'/::!': '<img src="../../res/chatBox/emotion/Expression_27.png" />',
	'/::L': '<img src="../../res/chatBox/emotion/Expression_28.png" />',
	'/::>': '<img src="../../res/chatBox/emotion/Expression_29.png" />',
	'/::,@': '<img src="../../res/chatBox/emotion/Expression_30.png" />',
	'/:,@f': '<img src="../../res/chatBox/emotion/Expression_31.png" />',
	'/::-S': '<img src="../../res/chatBox/emotion/Expression_32.png" />',
	'/:?': '<img src="../../res/chatBox/emotion/Expression_33.png" />',
	'/:,@x': '<img src="../../res/chatBox/emotion/Expression_34.png" />',
	'/:,@@': '<img src="../../res/chatBox/emotion/Expression_35.png" />',
	'/::8': '<img src="../../res/chatBox/emotion/Expression_36.png" />',
	'/:,@!': '<img src="../../res/chatBox/emotion/Expression_37.png" />',
	'/:!!!': '<img src="../../res/chatBox/emotion/Expression_38.png" />',
	'/:xx': '<img src="../../res/chatBox/emotion/Expression_39.png" />',
	'/:bye': '<img src="../../res/chatBox/emotion/Expression_40.png" />',
	'/:wipe': '<img src="../../res/chatBox/emotion/Expression_41.png" />',
	'/:dig': '<img src="../../res/chatBox/emotion/Expression_42.png" />',
	'/:handclap': '<img src="../../res/chatBox/emotion/Expression_43.png" />',
	'/:&-(': '<img src="../../res/chatBox/emotion/Expression_44.png" />',
	'/:B-)': '<img src="../../res/chatBox/emotion/Expression_45.png" />',
	'/:<@': '<img src="../../res/chatBox/emotion/Expression_46.png" />',
	'/:@>': '<img src="../../res/chatBox/emotion/Expression_47.png" />',
	'/::-O': '<img src="../../res/chatBox/emotion/Expression_48.png" />',
	'/:>-|': '<img src="../../res/chatBox/emotion/Expression_49.png" />',
	'/:P-(': '<img src="../../res/chatBox/emotion/Expression_50.png" />',
	'/::\'|': '<img src="../../res/chatBox/emotion/Expression_51.png" />',
	'/:X-)': '<img src="../../res/chatBox/emotion/Expression_52.png" />',
	'/::*': '<img src="../../res/chatBox/emotion/Expression_53.png" />',
	'/:@x': '<img src="../../res/chatBox/emotion/Expression_54.png" />',
	'/:8*': '<img src="../../res/chatBox/emotion/Expression_55.png" />',
	'/:pd': '<img src="../../res/chatBox/emotion/Expression_56.png" />',
	'/:<W>': '<img src="../../res/chatBox/emotion/Expression_57.png" />',
	'/:beer': '<img src="../../res/chatBox/emotion/Expression_58.png" />',
	'/:basketb': '<img src="../../res/chatBox/emotion/Expression_59.png" />',
	'/:oo': '<img src="../../res/chatBox/emotion/Expression_60.png" />',
	'/:coffee': '<img src="../../res/chatBox/emotion/Expression_61.png" />',
	'/:eat': '<img src="../../res/chatBox/emotion/Expression_62.png" />',
	'/:pig': '<img src="../../res/chatBox/emotion/Expression_63.png" />',
	'/:rose': '<img src="../../res/chatBox/emotion/Expression_64.png" />',
	'/:fade': '<img src="../../res/chatBox/emotion/Expression_65.png" />',
	'/:showlove': '<img src="../../res/chatBox/emotion/Expression_66.png" />',
	'/:heart': '<img src="../../res/chatBox/emotion/Expression_67.png" />',
	'/:break': '<img src="../../res/chatBox/emotion/Expression_68.png" />',
	'/:cake': '<img src="../../res/chatBox/emotion/Expression_69.png" />',
	'/:li': '<img src="../../res/chatBox/emotion/Expression_70.png" />',
	'/:bome': '<img src="../../res/chatBox/emotion/Expression_71.png" />',
	'/:kn': '<img src="../../res/chatBox/emotion/Expression_72.png" />',
	'/:footb': '<img src="../../res/chatBox/emotion/Expression_73.png" />',
	'/:ladybug': '<img src="../../res/chatBox/emotion/Expression_74.png" />',
	'/:shit': '<img src="../../res/chatBox/emotion/Expression_75.png" />',
	'/:moon': '<img src="../../res/chatBox/emotion/Expression_76.png" />',
	'/:sun': '<img src="../../res/chatBox/emotion/Expression_77.png" />',
	'/:gift': '<img src="../../res/chatBox/emotion/Expression_78.png" />',
	'/:hug': '<img src="../../res/chatBox/emotion/Expression_79.png" />',
	'/:strong': '<img src="../../res/chatBox/emotion/Expression_80.png" />',
	'/:weak': '<img src="../../res/chatBox/emotion/Expression_81.png" />',
	'/:share': '<img src="../../res/chatBox/emotion/Expression_82.png" />',
	'/:v': '<img src="../../res/chatBox/emotion/Expression_83.png" />',
	'/:@)': '<img src="../../res/chatBox/emotion/Expression_84.png" />',
	'/:jj': '<img src="../../res/chatBox/emotion/Expression_85.png" />',
	'/:@@': '<img src="../../res/chatBox/emotion/Expression_86.png" />',
	'/:bad': '<img src="../../res/chatBox/emotion/Expression_87.png" />',
	'/:lvu': '<img src="../../res/chatBox/emotion/Expression_88.png" />',
	'/:no': '<img src="../../res/chatBox/emotion/Expression_89.png" />',
	'/:ok': '<img src="../../res/chatBox/emotion/Expression_90.png" />',
	'/:love': '<img src="../../res/chatBox/emotion/Expression_91.png" />',
	'/:<L>': '<img src="../../res/chatBox/emotion/Expression_92.png" />',
	'/:jump': '<img src="../../res/chatBox/emotion/Expression_93.png" />',
	'/:shake': '<img src="../../res/chatBox/emotion/Expression_94.png" />',
	'/:<O>': '<img src="../../res/chatBox/emotion/Expression_95.png" />',
	'/:circle': '<img src="../../res/chatBox/emotion/Expression_96.png" />',
	'/:kotow': '<img src="../../res/chatBox/emotion/Expression_97.png" />',
	'/:turn': '<img src="../../res/chatBox/emotion/Expression_98.png" />',
	'/:skip': '<img src="../../res/chatBox/emotion/Expression_99.png" />',
	'/:oY': '<img src="../../res/chatBox/emotion/Expression_100.png" />',
	'/:#-0': '<img src="../../res/chatBox/emotion/Expression_101.png" />',
	'/:hiphot': '<img src="../../res/chatBox/emotion/Expression_102.png" />',
	'/:kiss': '<img src="../../res/chatBox/emotion/Expression_103.png" />',
	'/:<&': '<img src="../../res/chatBox/emotion/Expression_104.png" />',
	'/:&>': '<img src="../../res/chatBox/emotion/Expression_105.png" />'
}
var face = {
    '[微笑]': '<img src="../../res/chatBox/emotion/Expression_1.png" />',
    '[撇嘴]': '<img src="../../res/chatBox/emotion/Expression_2.png" />',
    '[色]': '<img src="../../res/chatBox/emotion/Expression_3.png" />',
    '[发呆]': '<img src="../../res/chatBox/emotion/Expression_4.png" />',
    '[得意]': '<img src="../../res/chatBox/emotion/Expression_5.png" />',
    '[流泪]': '<img src="../../res/chatBox/emotion/Expression_6.png" />',
    '[害羞]': '<img src="../../res/chatBox/emotion/Expression_7.png" />',
    '[闭嘴]': '<img src="../../res/chatBox/emotion/Expression_8.png" />',
    '[睡]': '<img src="../../res/chatBox/emotion/Expression_9.png" />',
    '[大哭]': '<img src="../../res/chatBox/emotion/Expression_10.png"/>',
    '[尴尬]': '<img src="../../res/chatBox/emotion/Expression_11.png"/>',
    '[发怒]': '<img src="../../res/chatBox/emotion/Expression_12.png"/>',
    '[调皮]': '<img src="../../res/chatBox/emotion/Expression_13.png" />',
    '[呲牙]': '<img src="../../res/chatBox/emotion/Expression_14.png" />',
    '[惊讶]': '<img src="../../res/chatBox/emotion/Expression_15.png" />',
    '[难过]': '<img src="../../res/chatBox/emotion/Expression_16.png" />',
    '[酷]': '<img src="../../res/chatBox/emotion/Expression_17.png" />',
    '[冷汗]': '<img src="../../res/chatBox/emotion/Expression_18.png" />',
	'[抓狂]': '<img src="../../res/chatBox/emotion/Expression_19.png" />',
	'[吐]': '<img src="../../res/chatBox/emotion/Expression_20.png" />',
    '[偷笑]': '<img src="../../res/chatBox/emotion/Expression_21.png" />',
	'[愉快]': '<img src="../../res/chatBox/emotion/Expression_22.png" />',
    '[白眼]': '<img src="../../res/chatBox/emotion/Expression_23.png" />',
    '[傲慢]': '<img src="../../res/chatBox/emotion/Expression_24.png" />',
    '[饥饿]': '<img src="../../res/chatBox/emotion/Expression_25.png" />',
    '[困]': '<img src="../../res/chatBox/emotion/Expression_26.png" />',
	'[惊恐]': '<img src="../../res/chatBox/emotion/Expression_27.png" />',
    '[流汗]': '<img src="../../res/chatBox/emotion/Expression_28.png" />',
	'[憨笑]': '<img src="../../res/chatBox/emotion/Expression_29.png" />',
	/*从这*/
	'[悠闲]': '<img src="../../res/chatBox/emotion/Expression_30.png" />',
    '[奋斗]': '<img src="../../res/chatBox/emotion/Expression_31.png" />',
    '[咒骂]': '<img src="../../res/chatBox/emotion/Expression_32.png" />',
    '[疑问]': '<img src="../../res/chatBox/emotion/Expression_33.png" />',
    '[嘘]': '<img src="../../res/chatBox/emotion/Expression_34.png" />',
    '[晕]': '<img src="../../res/chatBox/emotion/Expression_35.png" />',
    '[疯了]': '<img src="../../res/chatBox/emotion/Expression_36.png" />',
    '[衰]': '<img src="../../res/chatBox/emotion/Expression_37.png" />',
    '[骷髅]': '<img src="../../res/chatBox/emotion/Expression_38.png" />',
    '[敲打]': '<img src="../../res/chatBox/emotion/Expression_39.png"/>',
    '[再见]': '<img src="../../res/chatBox/emotion/Expression_40.png"/>',
    '[擦汗]': '<img src="../../res/chatBox/emotion/Expression_41.png"/>',
    '[抠鼻]': '<img src="../../res/chatBox/emotion/Expression_42.png" />',
    '[鼓掌]': '<img src="../../res/chatBox/emotion/Expression_43.png" />',
    '[糗大了]': '<img src="../../res/chatBox/emotion/Expression_44.png" />',
    '[坏笑]': '<img src="../../res/chatBox/emotion/Expression_45.png" />',
    '[左哼哼]': '<img src="../../res/chatBox/emotion/Expression_46.png" />',
    '[右哼哼]': '<img src="../../res/chatBox/emotion/Expression_47.png" />',
	'[哈欠]': '<img src="../../res/chatBox/emotion/Expression_48.png" />',
	'[鄙视]': '<img src="../../res/chatBox/emotion/Expression_49.png" />',
    '[委屈]': '<img src="../../res/chatBox/emotion/Expression_50.png" />',
	'[快哭了]': '<img src="../../res/chatBox/emotion/Expression_51.png" />',
    '[阴险]': '<img src="../../res/chatBox/emotion/Expression_52.png" />',
    '[亲亲]': '<img src="../../res/chatBox/emotion/Expression_53.png" />',
    '[吓]': '<img src="../../res/chatBox/emotion/Expression_54.png" />',
    '[可怜]': '<img src="../../res/chatBox/emotion/Expression_55.png" />',
	'[菜刀]': '<img src="../../res/chatBox/emotion/Expression_56.png" />',
    '[西瓜]': '<img src="../../res/chatBox/emotion/Expression_57.png" />',
	'[啤酒]': '<img src="../../res/chatBox/emotion/Expression_58.png" />',
	'[篮球]': '<img src="../../res/chatBox/emotion/Expression_59.png" />',
    '[乒乓]': '<img src="../../res/chatBox/emotion/Expression_60.png" />',
    '[咖啡]': '<img src="../../res/chatBox/emotion/Expression_61.png" />',
    '[饭]': '<img src="../../res/chatBox/emotion/Expression_62.png" />',
    '[猪头]': '<img src="../../res/chatBox/emotion/Expression_63.png" />',
    '[玫瑰]': '<img src="../../res/chatBox/emotion/Expression_64.png" />',
    '[凋谢]': '<img src="../../res/chatBox/emotion/Expression_65.png" />',
    '[嘴唇]': '<img src="../../res/chatBox/emotion/Expression_66.png" />',
    '[爱心]': '<img src="../../res/chatBox/emotion/Expression_67.png" />',
    '[心碎]': '<img src="../../res/chatBox/emotion/Expression_68.png"/>',
    '[蛋糕]': '<img src="../../res/chatBox/emotion/Expression_69.png"/>',
    '[闪电]': '<img src="../../res/chatBox/emotion/Expression_70.png"/>',
    '[炸弹]': '<img src="../../res/chatBox/emotion/Expression_71.png" />',
    '[刀]': '<img src="../../res/chatBox/emotion/Expression_72.png" />',
    '[足球]': '<img src="../../res/chatBox/emotion/Expression_73.png" />',
    '[瓢虫]': '<img src="../../res/chatBox/emotion/Expression_74.png" />',
    '[便便]': '<img src="../../res/chatBox/emotion/Expression_75.png" />',
    '[月亮]': '<img src="../../res/chatBox/emotion/Expression_76.png" />',
	'[太阳]': '<img src="../../res/chatBox/emotion/Expression_77.png" />',
	'[礼物]': '<img src="../../res/chatBox/emotion/Expression_78.png" />',
    '[拥抱]': '<img src="../../res/chatBox/emotion/Expression_79.png" />',
	'[强]': '<img src="../../res/chatBox/emotion/Expression_80.png" />',
    '[弱]': '<img src="../../res/chatBox/emotion/Expression_81.png" />',
    '[握手]': '<img src="../../res/chatBox/emotion/Expression_82.png" />',
    '[胜利]': '<img src="../../res/chatBox/emotion/Expression_83.png" />',
    '[抱拳]': '<img src="../../res/chatBox/emotion/Expression_84.png" />',
	'[勾引]': '<img src="../../res/chatBox/emotion/Expression_85.png" />',
    '[拳头]': '<img src="../../res/chatBox/emotion/Expression_86.png" />',
	'[差劲]': '<img src="../../res/chatBox/emotion/Expression_87.png" />',
	'[爱你]': '<img src="../../res/chatBox/emotion/Expression_88.png" />',
    '[NO]': '<img src="../../res/chatBox/emotion/Expression_89.png" />',
    '[OK]': '<img src="../../res/chatBox/emotion/Expression_90.png" />',
    '[爱情]': '<img src="../../res/chatBox/emotion/Expression_91.png" />',
    '[飞吻]': '<img src="../../res/chatBox/emotion/Expression_92.png" />',
    '[跳跳]': '<img src="../../res/chatBox/emotion/Expression_93.png" />',
    '[发抖]': '<img src="../../res/chatBox/emotion/Expression_94.png" />',
    '[怄火]': '<img src="../../res/chatBox/emotion/Expression_95.png" />',
    '[转圈]': '<img src="../../res/chatBox/emotion/Expression_96.png" />',
    '[磕头]': '<img src="../../res/chatBox/emotion/Expression_97.png"/>',
    '[回头]': '<img src="../../res/chatBox/emotion/Expression_98.png"/>',
    '[跳绳]': '<img src="../../res/chatBox/emotion/Expression_99.png"/>',
    '[投降]': '<img src="../../res/chatBox/emotion/Expression_100.png" />',
    '[激动]': '<img src="../../res/chatBox/emotion/Expression_101.png" />',
    '[街舞]': '<img src="../../res/chatBox/emotion/Expression_102.png" />',
    '[献吻]': '<img src="../../res/chatBox/emotion/Expression_103.png" />',
    '[左太极]': '<img src="../../res/chatBox/emotion/Expression_104.png" />',
    '[右太极]': '<img src="../../res/chatBox/emotion/Expression_105.png" />'
};
//正则替换表情
var reg = /\[.+?\]/g;
var qqfaceRegex = /\/::\)|\/::~|\/::B|\/::\||\/:8-\)|\/::<|\/::\$|\/::X|\/::Z|\/::'\(|\/::-\||\/::@|\/::P|\/::D|\/::O|\/::\(|\/::\+|\/:--b|\/::Q|\/::T|\/:,@P|\/:,@-D|\/::d|\/:,@o|\/::g|\/:\|-\)|\/::!|\/::L|\/::>|\/::,@|\/:,@f|\/::-S|\/:\?|\/:,@x|\/:,@@|\/::8|\/:,@!|\/:!!!|\/:xx|\/:bye|\/:wipe|\/:dig|\/:handclap|\/:&-\(|\/:B-\)|\/:<@|\/:@>|\/::-O|\/:>-\||\/:P-\(|\/::'\||\/:X-\)|\/::\*|\/:@x|\/:8\*|\/:pd|\/:<W>|\/:beer|\/:basketb|\/:oo|\/:coffee|\/:eat|\/:pig|\/:rose|\/:fade|\/:showlove|\/:heart|\/:break|\/:cake|\/:li|\/:bome|\/:kn|\/:footb|\/:ladybug|\/:shit|\/:moon|\/:sun|\/:gift|\/:hug|\/:strong|\/:weak|\/:share|\/:v|\/:@\)|\/:jj|\/:@@|\/:bad|\/:lvu|\/:no|\/:ok|\/:love|\/:<L>|\/:jump|\/:shake|\/:<O>|\/:circle|\/:kotow|\/:turn|\/:skip|\/:oY|\/:#-0|\/:hiphot|\/:kiss|\/:<&|\/:&>/g;
//聊天框加号按钮点出来显示的图标
var addButtonAry = [
    {
        normal: "widget://res/chatBox/pic.png",
        title: "相册"
    }, {
        normal: "widget://res/chatBox/camera.png",
        title: "拍摄"
    }, {
        normal: "widget://res/chatBox/location.png",
        title: "位置"
    }, {
        normal: "widget://res/chatBox/useful_expressions.png",
        title: "营销库"
    }, {
        normal: "widget://res/chatBox/repository.png",
        title: "知识库"
    }/*, {
        normal: "widget://res/chatBox/btn_internetcall.png",
        title: "网络电话"
    }*/, {
        normal: "widget://res/voice_msg/btn_08.png",
        title: "普通电话"
    }
];
//聊天面板文字
var hints = [
    {
        size: '16',
        color: '#000000',
        contents: [ '加载中...' ],
        normal: 'widget://res/chatBox/30-10.png',
        selected: 'widget://res/chatBox/content_selected.png'
    }, {
        size: '16',
        color: '#000000',
        contents: [ '加载中...' ],
        normal: 'widget://res/chatBox/30-10.png',
        selected: 'widget://res/chatBox/content_selected.png'
    }, {
        size: '16',
        color: '#000000',
        contents: [ '加载中...' ],
        normal: 'widget://res/chatBox/30-10.png',
        selected: 'widget://res/chatBox/content_selected.png'
    }, {
        size: '16',
        color: '#000000',
        contents: [ '加载中...' ],
        normal: 'widget://res/chatBox/30-10.png',
        selected: 'widget://res/chatBox/content_selected.png'
    }, {
        size: '16',
        color: '#000000',
        contents: [ '加载中...' ],
        normal: 'widget://res/chatBox/30-10.png',
        selected: 'widget://res/chatBox/content_selected.png'
    }
];
//聊天框左侧按钮
var leftButtons = [
    {
        normal: 'widget://res/chatBox/channel_1.png',
        selected: 'widget://res/chatBox/channel_1_press.png'
    }, {
        normal: 'widget://res/chatBox/wechat.png',
        selected: 'widget://res/chatBox/wechat_press.png'
    }, {
        normal: 'widget://res/chatBox/app.png',
        selected: 'widget://res/chatBox/app__press.png'
    }, {
        normal: 'widget://res/chatBox/sms.png',
        selected: 'widget://res/chatBox/sms_press.png'
    }, {
        normal: 'widget://res/chatBox/sina.png',
        selected: 'widget://res/chatBox/sina_press.png'
    }
];

//打开大图
function openImage(path) {
    var obj = api.require('imageBrowser');
    obj.openImages({
        imageUrls: [path]
    });
}
//滑动到底部
function pageDown(time) {
    setTimeout(function () {
        api.pageDown({bottom: true, animate: true}, function (ret) {});
    }, time || 0)

}

//获取待处理工单数据源每条记录的索引
function getPendingIndex(){
    return JSON.parse(localStorage.getItem('pendingIndex')||'{}');
}
//设置待处理工单数据源每条记录的索引
function setPendingIndex(pendingIndex){
    localStorage.setItem('pendingIndex',JSON.stringify(pendingIndex));
}

//获取已处理工单数据源每条记录的索引
function getProcessedIndex(){
    return JSON.parse(localStorage.getItem('processedIndex')||'{}');
}
//设置已处理工单数据源每条记录的索引
function setProcessedIndex(chatIndex){
    localStorage.setItem('processedIndex',JSON.stringify(chatIndex));
}
function getDateYMDHM(){
    var todayDate = new Date();
    var year = todayDate.getFullYear();
    var date = full(todayDate.getDate());
    var month = full(todayDate.getMonth() + 1);
    var hour = full(todayDate.getHours());
    var mininutes = full(todayDate.getMinutes());
    return year + "-" + month + "-" + date + " " + hour + ":" + mininutes;
}
function getTime(time){
    var date = new Date(time);
    return full(date.getHours()) + ":" + full(date.getMinutes());
}
function getDate(time){
	var date = new Date(time);
	return (date.getMonth()+1) + '-' + date.getDate();
}
function full(num){
    if(num<10){
        num="0"+num;
    }
    return num;
}

//获取团队用户的头像
function getUserIcon(targetId){
    var user_tar = localStorage[targetId];
    var user = JSON.parse(user_tar);
    return user.userIcon;
}

// 打开电话窗口
function openWinCall(type,id){
    api.execScript({
        name: 'root',
        script: 'indexOpenWinCall(\''+type+'\',\''+id+'\');'
    });
    
}

function createData(body){
	var result = {"body":body}
	return JSON.parse(JSON.stringify(result));
}

function loginData(){
	var body = {
	    "errorMsg": "登陆成功",
	    "data": {
	    	"loginToken":"",
	    	"rongyunToken":"VjKxa0+QA2yr/hajQe4A9OSKVWCFsb6E5P+qYOYsmeJu5Y9OFfCZXjsc60+4zUOAXB1wqi8qzUtsEdSxgKPsyg==",
	    	"user":{
	    		//	id	String	客服id
				//	role	Number	角色（0：普通用户，1：管理员）
				//	name	String	用户名
				//	password	string	密码
				//	cellNum	string	手机号码
				//	deviceId	string	客服手机设备id
				//	companyId	string	团队id
				//	status	number	客服状态（0：暂离，1：在线，2：离线）
				//	headLargeUrl	string	大头像url
				//	desc	string	个人信息描述
				//	headSmallUrl	string	小头像url
				//	agentId	number	本团队内的客服编号，从1开始计数
				//	createTime	string	创建时间
	    		"id": "00-7a2e61e4-c8e2-4506-8418-e9bdf7499000",
	    		"role":1,
		        "name": "郭丽巍",
		        "password": "q",
		        "cellNum": "18322488422",
		        "deviceId": null,
		        "createTime": 1428631699968,
		        "status": 0,
		        "headLargeUrl": "http://c1.mifile.cn/f/i/2014/cn/icon/site-logo.png",
		        "desc": "大家好，我是云客服团队的 客服guolw",
		        "headSmallUrl": "http://c1.mifile.cn/f/i/2014/cn/icon/site-logo.png",
		        "companyId": "00-b5f97f95-a7d9-45d2-855c-c18575be10a1",
	    		"agentId":001
	    	}
	    },
	    "errorCode":0
	}
	
	return createData(body);
}

function commonResult(){
	var result = {
	    "errorMsg": "更新公司信息成功",
	    "data": null,
	    "errorCode":0
	}
	return createData(result);
}

function messages(){
	var body = {
	    "errorMsg": "",
	    "data": {
	    	"messages":[
		    	 {
                "id": "8cc49a2962994780b71ad82a843dcbd9",
                "content": "{\"content\":\"才cvvhhh\",\"extra\":\"{\\\"channelType\\\":\\\"C2\\\",\\\"fromName\\\":\\\"Guolw\\\",\\\"isPrivate\\\":true,\\\"operation\\\":0,\\\"isPin\\\":true,\\\"type\\\":0}\"}",
                "timestamp": 1435548000959,
                "fromId": "236a0e145053489f904b78fb9ba0db19",
                "fromName": "郭丽巍",
                "toId": "d9f7ad05e4c34cd7bde7706663550a81",
                "toName": "Guolw",
                "msgType": "RC:TxtMsg",
                "pushContent": null,
                "pushData": null,
                "channelType": "C3",
                "channelFromId": "gh_cf06958465a8",
                "channelFromName": "UMX体验设计",
                "channelToId": "ohUVes70dBgmMwzojFHjfhKSjKq4",
                "channelToName": "guolw",
                "isPrivate": 1,
                "isPin": 1,
                "flag": 0,
                "type": 0
            	}
	    	]
	    },
	    "errorCode":0
	}
	
	return createData(body);
}
function getDisplayTimeHtml(lastSentTime, thisSentTime){
	var
	curSentTime = getTime(thisSentTime),
	curSentDate = getDate(thisSentTime),
	todayDate = getDate(+new Date());
    var timeHtml = '',dateHtml = '';
    if(curSentDate != todayDate){
    	dateHtml = curSentDate + ' ';
    }
    if(lastSentTime){
    	if (lastSentTime.substring(0,lastSentTime.length-1) != curSentTime.substring(0,curSentTime.length-1)) {
	        timeHtml = '<li class="timeWrap"><div class="time">'+ dateHtml + curSentTime+'</div></li>';
	    }
    }
	return timeHtml;
}
function getCurrUser(){
	var userObj = JSON.parse(localStorage.getItem("currUser"));
	return userObj;
}

function getUserId(){
	return localStorage.userId;
}

/**
 * 是否为团队管理员
 */
function isTeamManager(){
	if(ROLE.MANAGER == getCurrUser().role){
		return true;
	}
	return false;
}

var tools = {
	subString: function(str, len){
		return str.length > len ? str.substring(0, len)+"..." : str;
	},
	hashcode: function(str){
		var hash = 0, i, chr, len;
		if (str.length === 0) return hash;
		for (i = 0, len = str.length; i < len; i++) {
		chr   = str.charCodeAt(i);
		hash  = ((hash << 5) - hash) + chr;
		hash |= 0; // Convert to 32bit integer
		}
		return hash;
	},
	//补零
	padZero: function(v){
		return v >= 10 ? v : '0'+v;
	},
	//秒-->分秒,不管小时
	second2Date: function(second){
		second = parseInt(second);
		var hms = '00:00';
		if(second>0){
			//计算时分秒
			var
			sec = Math.floor( second % 60 ),
			min = Math.floor( (second / 60) % 60 );
			//hou = Math.floor( (second / 3600) % 60 );
			//容错
			//hou > 24 ? 24 : hou;
			//
			//hms = (tools.padZero(hou) == '00' ? '' : tools.padZero(hou))+ ':' + tools.padZero(min) + ':' + tools.padZero(sec);
			hms = tools.padZero(min) + ':' + tools.padZero(sec);
		}
		return hms;
	},
	createCode: function(phoneNum){
		var code = '';
		try{
			var hc = tools.hashcode(phoneNum).toString();
			code = hc.substring(1,7);
			//不够六位补零
			var len = code.length;
			if(len<6){
				for(var i=0; i<(6-len); i++ ){
					code += '0'
				}
			}
		}catch(e){
			fuck('产生随机密码出问题了')
		}
		return code;
	}
}
/** 跳入正在建设中页面*/
function build(fromPage){
	api.openWin({
		name: 'win_constructing',
		url : '../public/win_constructing.html'
		,pageParam: {
			fromPage: fromPage || ''
		}
	})
}
function shit(u){alert(JSON.stringify(u))}
function fuck(u){
	api.hideProgress();
	api.alert({
		title: '异常信息',
		msg: u || '请重启应用(F-1)'
	});
}
function fuckAlert(u){
	api.hideProgress();
	api.alert({
		title: '提示',
		msg: u || '-1'
	})
}
function fuckErr(err){
	api.hideProgress();
	api.alert({
		title: '错误信息',
		msg: err.msg || JSON.stringify(err)
		//msg: '错误码:'+err.code+'; 错误信息:'+err.msg+'; 网络状态码:'+err.statusCode
	})
}
function IsURL(str_url){
        var strRegex = "^((https|http|ftp|rtsp|mms)?://)"
        + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
        + "(([0-9]{1,3}\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
        + "|" // 允许IP和DOMAIN（域名）
        + "([0-9a-z_!~*'()-]+\.)*" // 域名- www.
        + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\." // 二级域名
        + "[a-z]{2,6})" // first level domain- .com or .museum
        + "(:[0-9]{1,4})?" // 端口- :80
        + "((/?)|" // a slash isn't required if there is no file name
        + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        var re=new RegExp(strRegex);
        //re.test()
        if (re.test(str_url)){
            return true;
        }else{
            return false;
        }
}
function ajaxRequest(url, method, bodyParam, callBack) {
	api.showProgress({
        style: 'default',
        animationType: 'fade',
        //text: '请稍等',
        modal: true
    });
	var url = BASE_URL + url;
	var
	isGet = method.toLowerCase()=='get',
	isDelete = method.toLowerCase()=='delete',
	
	//isLogout = url.indexOf('token/logout') >= 0,//需要 body 需要token
	getRandomCode = url.indexOf('randomsms?') >= 0,//不需要 body 不需要token
	isLogin = url.indexOf('token/login') >= 0,//不需要 body 不需要token
	
	isRegist = url.indexOf('register') >= 0,//不需要token  需要body
	isActive = url.indexOf('user/updateactivated/') >= 0,//不需要token,需要body
	isResetpwd = url.indexOf('user/password/by/user/cellnum/') >= 0,//不需要token 需要body
	isVerifyArea = url.indexOf('domainvalidity') >= 0;//验证域名  //不需要token 需要body
	var
	iDontNeedTokenAndBody = getRandomCode || isLogin,
	iDontNeedToken = isRegist || isActive || isResetpwd || isVerifyArea ,
	iDontNeedBody = isGet || isDelete;
	if(iDontNeedTokenAndBody){
		api.ajax({
	        url: url,
	        method: method,
	        cache: false,
	        timeout: 20,
	        returnAll: true,
	        headers: {
	            "Content-type": "application/json"
	        }
	    }, function (ret,err) {
	    	api.hideProgress();
	        callBack(ret,err);
	    });
	}
	else if(iDontNeedToken){
		api.ajax({
	        url: url,
	        method: method,
	        cache: false,
	        timeout: 20,
	        returnAll: true,
	        headers: {
	            "Content-type": "application/json"
	        },
	        data: {
	            body: bodyParam || {}
	        }
	    }, function (ret,err) {
	    	api.hideProgress();
	        callBack(ret,err);
	    });
	}
	else if(iDontNeedBody){
		api.ajax({
	        url: url,
	        method: method,
	        cache: false,
	        timeout: 20,
	        returnAll: true,
	        headers: {
	            "Content-type": "application/json",
	            "token": getCurrUser().token
	        }
	    }, function (ret,err) {
	    	api.hideProgress();
	        callBack(ret,err);
	    });
	}else{
		api.ajax({
	        url: url,
	        method: method,
	        cache: false,
	        timeout: 20,
	        returnAll: true,
	        headers: {
	            "Content-type": "application/json",
	            "token": getCurrUser().token
	        },
	        data: {
	            body: bodyParam || {}
	        }
	    }, function (ret,err) {
	    	api.hideProgress();
	        callBack(ret,err);
	    });
	}
}

/**
 * 上传文件ajax请求
 * @param {Object} url
 * @param {Object} method
 * @param {Object} fileParam
 * @param {Object} bodyParam
 * @param {Object} callBack
 */
function fileAjaxRequest(url, method, fileParam, callBack){
	api.showProgress({
        style: 'default',
        animationType: 'fade',
        //text: '请稍等',
        modal: true
    });
	var urlParam = BASE_URL + url;
	api.ajax({
	    url: urlParam,
	    method: method,
	    cache: false,
        timeout: 50,
        dataType: 'json',
        returnAll: true,
        headers: {
            "Content-type": undefined,
            "token": getCurrUser().token
        },
	    data:{
//	    	values: bodyParam,
	        files: fileParam
	    }
	},function (ret,err) {
		api.hideProgress();
		callBack(ret,err);
	});
}

//常用语DB接口
var _words = {
	list: function(type,callback){
		var words = [];
		var
		url ="commonwords/in/company/"+getCurrUser().companyId,
		method = 'post',
		params = {userId:getUserId(), united: type};
		ajaxRequest(url, method, params, function(ret,err){
			if(err){
				fuckErr(err);
				return false;
			}
			var result = ret.body;
			if(result.errorCode!=0&&result.errorCode!=200){
				fuckAlert(result.errorMsg);
				return false;
			}
			var data = ret.body.data;
			if(data.total){
				$.each(data.commonwordslist, function(i,v){
					words.push(v.name)
				})
			}
			//回调
			callback(words)
		});
	}
}
