(function(window,document){
  'use strict';
  var YP=window.YP;if(!YP||!YP.guardRole('CUSTOMER'))return;
  var C=window.YPC={state:{companions:[],requests:[],orders:[],notifications:[],notificationUnread:0,conversations:[],requestTab:'create',orderTab:'orders',selectedCompanion:null}};
  var S=C.state;
  C.pages={dash:document.querySelector('#page-dash .panel-inner'),companions:document.querySelector('#page-companions .panel-inner'),create:document.querySelector('#page-create .panel-inner'),orders:document.querySelector('#page-orders .panel-inner')};
  C.safe=function(path,fallback){return YP.api(path).catch(function(e){YP.toast(e.message,'error');return fallback})};
  C.setActive=function(root,attribute,value){root.querySelectorAll('['+attribute+']').forEach(function(el){el.classList.toggle('active',el.getAttribute(attribute)===value)})};

  C.build=function(){
    C.pages.dash.innerHTML='<div class="yp-head"><div><h2>你好，'+YP.text(YP.user.nickname||YP.user.username)+'</h2><p>找到最合适的陪诊师，为家人预约安心服务</p></div><span id="customerDashTime"></span></div><div class="stat-row" id="customerStats">'+YP.loading('正在读取服务数据...')+'</div><div class="yp-scroll"><div class="card-grid"><div class="c-card" data-go="1"><h3>☷ 浏览陪诊师</h3><p>查看已认证陪诊师，也可以直接指定服务人员</p></div><div class="c-card" data-go="2"><h3>✎ 发布服务需求</h3><p>发布普通需求，或携带指定陪诊师提交审核</p></div><div class="c-card" data-go="3"><h3>☰ 订单与私信</h3><p>处理订单、查看私信标签与全部历史聊天记录</p></div></div><div id="customerNotifications" style="margin-top:14px"></div></div>';
    C.pages.companions.innerHTML='<div class="yp-head"><div><h2>陪诊师列表</h2><p>保持原轮盘页，在此选择或指定陪诊师</p></div><span id="customerCompanionCount">0 人</span></div><div class="yp-inline-filters"><input id="customerCompanionKeyword" class="yp-input" placeholder="搜索姓名、区域、服务或性格"><button id="customerCompanionReset" class="yp-btn small" type="button">重置</button></div><div class="yp-scroll"><div class="card-grid" id="customerCompanionList">'+YP.loading()+'</div></div>';
    C.pages.create.innerHTML='<div class="yp-head"><div><h2>发布服务需求</h2><p>普通匹配与指定陪诊师都在原页面内完成</p></div><span id="customerRequestCount">0 条需求</span></div><div class="yp-tabs"><button class="yp-tab active" data-request-tab="create" type="button">发布需求</button><button class="yp-tab" data-request-tab="mine" type="button">我的需求</button></div><div class="yp-scroll" id="customerRequestBody"></div>';
    C.pages.orders.innerHTML='<div class="yp-head"><div><h2>我的订单</h2><p>双方同意后开放私信，结束后保留只读记录</p></div><span id="customerOrderCount">0 笔</span></div><div class="yp-tabs"><button class="yp-tab active" data-order-tab="orders" type="button">订单进度</button><button class="yp-tab" data-order-tab="chats" type="button">聊天记录 <span id="customerChatBadge"></span></button></div><div class="yp-scroll" id="customerOrderBody"></div>';
    document.getElementById('customerDashTime').textContent=new Date().toLocaleDateString('zh-CN',{year:'numeric',month:'long',day:'numeric'});
    C.pages.dash.querySelectorAll('[data-go]').forEach(function(el){el.addEventListener('click',function(){window.goPage(Number(el.dataset.go))})});
    document.getElementById('customerCompanionKeyword').addEventListener('input',C.renderCompanions);
    document.getElementById('customerCompanionReset').addEventListener('click',function(){document.getElementById('customerCompanionKeyword').value='';C.renderCompanions()});
    C.pages.create.querySelectorAll('[data-request-tab]').forEach(function(el){el.addEventListener('click',function(){S.requestTab=el.dataset.requestTab;C.setActive(C.pages.create,'data-request-tab',S.requestTab);C.renderRequests()})});
    C.pages.orders.querySelectorAll('[data-order-tab]').forEach(function(el){el.addEventListener('click',function(){S.orderTab=el.dataset.orderTab;C.setActive(C.pages.orders,'data-order-tab',S.orderTab);C.renderOrders()})});
    YP.bindInteractive(document);
  };

  C.load=async function(){
    var r=await Promise.all([C.safe('/companion/list',[]),C.safe('/service-request/list',[]),C.safe('/order/my',[]),C.safe('/notifications',[]),C.safe('/notifications/unread',0),C.safe('/chat/conversations',[])]);
    S.companions=r[0]||[];S.requests=r[1]||[];S.orders=r[2]||[];S.notifications=r[3]||[];S.notificationUnread=Number(r[4]||0);S.conversations=r[5]||[];C.renderAll();
  };
  C.refresh=async function(){
    var r=await Promise.all([C.safe('/service-request/list',S.requests),C.safe('/order/my',S.orders),C.safe('/notifications',S.notifications),C.safe('/notifications/unread',S.notificationUnread),C.safe('/chat/conversations',S.conversations)]);
    S.requests=r[0];S.orders=r[1];S.notifications=r[2];S.notificationUnread=Number(r[3]||0);S.conversations=r[4];C.renderAll();
  };
  C.renderAll=function(){C.renderDashboard();C.renderCompanions();C.renderRequests();C.renderOrders();document.getElementById('customerRequestCount').textContent=S.requests.length+' 条需求';document.getElementById('customerOrderCount').textContent=S.orders.length+' 笔'};

  C.renderDashboard=function(){
    var active=S.orders.filter(function(o){return['PENDING_ACCEPT','ACCEPTED','IN_SERVICE','PENDING_CONFIRM'].indexOf(o.status)>=0}).length;
    var avg=S.companions.length?S.companions.reduce(function(n,c){return n+Number(c.rating||0)},0)/S.companions.length:0;
    document.getElementById('customerStats').innerHTML='<div class="mini-stat"><div class="val">'+S.orders.length+'</div><div class="lbl">我的订单</div></div><div class="mini-stat"><div class="val">'+S.companions.length+'</div><div class="lbl">认证陪诊师</div></div><div class="mini-stat"><div class="val">'+active+'</div><div class="lbl">进行中</div></div><div class="mini-stat"><div class="val" style="color:rgba(170,130,60,.9)">'+(avg?avg.toFixed(1):'—')+'</div><div class="lbl">平均评分</div></div>';
    var box=document.getElementById('customerNotifications');box.innerHTML=YP.dashboardNotificationBlock('消息通知',S.notificationUnread,S.notifications);
    YP.attachNotifications(box,function(){S.notificationUnread=Math.max(0,S.notificationUnread-1);C.renderDashboard()});
    var all=box.querySelector('[data-read-all]');if(all)all.addEventListener('click',async function(){try{await YP.api('/notifications/read-all',{method:'PUT'});S.notificationUnread=0;S.notifications.forEach(function(n){n.isRead=1});C.renderDashboard();YP.toast('全部通知已标记为已读')}catch(e){YP.toast(e.message,'error')}});YP.bindInteractive(C.pages.dash);
  };

  C.renderCompanions=function(){
    var input=document.getElementById('customerCompanionKeyword');if(!input)return;var keyword=input.value.trim().toLowerCase();
    var list=S.companions.filter(function(c){return!keyword||[c.nickname,c.realName,c.serviceArea,c.serviceTypes,c.traits,c.introduction].join(' ').toLowerCase().indexOf(keyword)>=0});
    document.getElementById('customerCompanionCount').textContent=list.length+' 人';var box=document.getElementById('customerCompanionList');
    box.innerHTML=list.length?list.map(function(c){return'<div class="c-card"><div class="yp-card-top"><h3>'+YP.text(c.nickname||c.realName,'陪诊师')+'</h3>'+YP.statusTag('ACCEPTED','已认证')+'</div><p>'+YP.text(c.serviceTypes,'陪诊服务')+' · '+YP.text(c.serviceArea,'服务区域待补充')+' · '+Number(c.experienceYears||0)+' 年经验 · ★ '+Number(c.rating||0).toFixed(1)+'</p><p class="yp-card-note">'+YP.text(c.introduction,'暂未填写个人介绍')+'</p><div class="yp-card-meta"><span>已完成 '+Number(c.completedCount||0)+' 单</span><span>'+YP.text(c.traits,'耐心、细致')+'</span></div><div class="yp-actions"><button class="yp-btn primary small" type="button" data-select-companion="'+YP.attr(c.id)+'">指定该陪诊师</button><button class="yp-btn speak small" type="button" data-speak-companion="'+YP.attr(c.id)+'">🔊 朗读信息</button></div></div>'}).join(''):YP.empty('暂无符合条件的陪诊师');
    box.querySelectorAll('[data-select-companion]').forEach(function(el){el.addEventListener('click',function(){var c=S.companions.find(function(x){return String(x.id)===String(el.dataset.selectCompanion)});S.selectedCompanion=c;S.requestTab='create';C.setActive(C.pages.create,'data-request-tab','create');C.renderRequests();window.goPage(2);YP.toast('已选择 '+(c.nickname||c.realName)+'，请填写需求后提交审核')})});
    box.querySelectorAll('[data-speak-companion]').forEach(function(el){el.addEventListener('click',function(){if(YP.speak)YP.speak({path:'/tts/companion/'+el.dataset.speakCompanion,button:el})})});YP.bindInteractive(box);
  };

  document.addEventListener('DOMContentLoaded',function(){C.build();C.load()});
})(window,document);
