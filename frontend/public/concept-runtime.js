(function(window,document){
  'use strict';

  var USER_KEY='yipei_user';
  var interactiveBound=new WeakSet();
  var currentModal=null;

  function readUser(){
    var raw=sessionStorage.getItem(USER_KEY)||localStorage.getItem(USER_KEY);
    if(!raw)return null;
    try{var user=JSON.parse(raw);return user&&user.id?user:null}catch(e){return null}
  }

  var user=readUser();
  var apiBase=window.YIPEI_API_BASE||(location.port==='8080'?'/api':'http://localhost:8080/api');

  function escapeHtml(value){
    return String(value==null?'':value).replace(/[&<>'"]/g,function(ch){return{'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[ch]});
  }

  function attr(value){return escapeHtml(value).replace(/`/g,'&#96;')}
  function text(value,fallback){return value==null||value===''?(fallback||'—'):escapeHtml(value)}
  function money(value){var n=Number(value);return Number.isFinite(n)?'￥'+n.toFixed(2):'—'}
  function date(value,short){
    if(!value)return '—';
    var raw=String(value).replace('T',' ');
    if(short)return raw.slice(0,16);
    return raw.slice(0,19);
  }
  function maskPhone(value){var s=String(value||'');return /^1\d{10}$/.test(s)?s.slice(0,3)+'****'+s.slice(7):text(s)}

  var statusLabels={
    PENDING:'待审核/待匹配',MATCHED:'已匹配',CLOSED:'已结束',CANCELLED:'已取消',
    PENDING_ACCEPT:'待陪诊师确认',ACCEPTED:'已接单',REJECTED:'已拒绝',IN_SERVICE:'服务中',PENDING_CONFIRM:'待客户确认',COMPLETED:'已完成',
    WITHDRAWN:'已撤回',HANDLING:'处理中',RESOLVED:'已解决'
  };
  var auditLabels={0:'待审核',1:'审核通过',2:'审核未通过'};
  function statusLabel(value){return statusLabels[value]||value||'未知'}
  function auditLabel(value){return auditLabels[value]||'未提交'}
  function tone(value){
    if(['ACCEPTED','COMPLETED','RESOLVED',1].indexOf(value)>=0)return'green';
    if(['PENDING','PENDING_ACCEPT','IN_SERVICE','PENDING_CONFIRM','HANDLING',0].indexOf(value)>=0)return'amber';
    if(['REJECTED','CANCELLED','CLOSED',2].indexOf(value)>=0)return'red';
    return'';
  }
  function statusTag(value,label){return'<span class="yp-status '+tone(value)+'">'+escapeHtml(label||statusLabel(value))+'</span>'}
  function auditTag(value){return'<span class="yp-status '+tone(value)+'">'+escapeHtml(auditLabel(value))+'</span>'}

  async function api(path,options){
    options=options||{};
    var headers=Object.assign({},options.headers||{});
    if(user&&user.id)headers['X-User-Id']=String(user.id);
    var body=options.body;
    if(body!=null&&!(body instanceof FormData)&&typeof body!=='string'){
      headers['Content-Type']='application/json';body=JSON.stringify(body);
    }
    var response;
    try{response=await fetch(apiBase+path,{method:options.method||'GET',headers:headers,body:body})}
    catch(e){throw new Error('无法连接后端服务，请确认 8080 端口已启动')}
    var payload=null;
    try{payload=await response.json()}catch(e){throw new Error('服务返回格式异常')}
    if(!response.ok||!payload||payload.code!==200)throw new Error(payload&&payload.message?payload.message:'请求失败');
    return payload.data;
  }

  function guardRole(role){
    if(!user){toast('请先登录后使用该功能','error');setTimeout(function(){location.href='login.html'},900);return false}
    if(user.role!==role){
      var pages={CUSTOMER:'customer-concept-light.html',COMPANION:'companion-concept-light.html',ADMIN:'admin-concept-light.html'};
      toast('当前账号角色与页面不匹配，正在返回对应中心','error');
      setTimeout(function(){location.href=pages[user.role]||'landing.html'},900);return false;
    }
    return true;
  }

  function bindInteractive(root){
    var dot=document.getElementById('dot')||document.getElementById('cursorDot');
    var ring=document.getElementById('ring')||document.getElementById('cursorRing');
    (root||document).querySelectorAll('a,button,input,select,textarea,.c-card,.quick-card,.yp-tab,.yp-notice').forEach(function(el){
      if(interactiveBound.has(el))return;interactiveBound.add(el);
      el.addEventListener('mouseenter',function(){
        if(dot)dot.classList.add('hover-text');if(ring)ring.classList.add('hover-text');
      });
      el.addEventListener('mouseleave',function(){
        if(dot)dot.classList.remove('hover-text');if(ring)ring.classList.remove('hover-text');
      });
    });
  }

  function toast(message,type){
    var stack=document.querySelector('.yp-toast-stack');
    if(!stack){stack=document.createElement('div');stack.className='yp-toast-stack';document.body.appendChild(stack)}
    var item=document.createElement('div');item.className='yp-toast'+(type==='error'?' error':'');item.textContent=message;stack.appendChild(item);
    setTimeout(function(){item.style.opacity='0';item.style.transform='translateY(-5px)'},2600);
    setTimeout(function(){item.remove()},3000);
  }

  function closeModal(){if(currentModal)currentModal.close()}
  function modal(options){
    options=options||{};closeModal();
    var mask=document.createElement('div');mask.className='yp-modal-mask';
    mask.innerHTML='<div class="yp-modal '+(options.wide?'wide':'')+'"><div class="yp-modal-head"><h3>'+escapeHtml(options.title||'提示')+'</h3><button class="yp-close" type="button" aria-label="关闭">×</button></div><div class="yp-modal-body"></div><div class="yp-modal-foot"></div></div>';
    var body=mask.querySelector('.yp-modal-body'),foot=mask.querySelector('.yp-modal-foot');
    if(typeof options.content==='string')body.innerHTML=options.content||'';else if(options.content)body.appendChild(options.content);
    var closed=false,cleanup=options.onClose;
    var controller={mask:mask,body:body,foot:foot,close:function(){if(closed)return;closed=true;if(typeof cleanup==='function')cleanup();mask.remove();if(currentModal===controller)currentModal=null}};
    (options.buttons||[{label:'关闭'}]).forEach(function(button){
      var el=document.createElement('button');el.type='button';el.className='yp-btn '+(button.className||'');el.textContent=button.label||'确定';
      el.addEventListener('click',async function(){
        try{var result=button.action?await button.action(controller):true;if(result!==false&&button.keepOpen!==true)controller.close()}
        catch(e){toast(e.message||'操作失败','error')}
      });foot.appendChild(el);
    });
    mask.querySelector('.yp-close').addEventListener('click',controller.close);
    mask.addEventListener('click',function(e){if(e.target===mask&&options.locked!==true)controller.close()});
    document.body.appendChild(mask);currentModal=controller;bindInteractive(mask);
    if(typeof options.onOpen==='function')options.onOpen(controller);
    return controller;
  }

  function confirmBox(message,title){
    return new Promise(function(resolve){
      var settled=false;
      modal({title:title||'请确认',content:'<p style="font-size:13px;line-height:1.8;color:rgba(var(--ink-soft),.9)">'+escapeHtml(message)+'</p>',onClose:function(){if(!settled)resolve(false)},buttons:[
        {label:'取消',action:function(){settled=true;resolve(false)}},
        {label:'确认',className:'primary',action:function(){settled=true;resolve(true)}}
      ]});
    });
  }

  function promptBox(options){
    options=options||{};
    return new Promise(function(resolve){
      var settled=false,id='yp-prompt-'+Date.now();
      modal({title:options.title||'请输入',content:'<div class="yp-field"><label>'+escapeHtml(options.label||'内容')+'</label>'+(options.textarea?'<textarea':'<input')+' id="'+id+'" class="'+(options.textarea?'yp-textarea':'yp-input')+'" '+(options.textarea?'>':'value="'+attr(options.value||'')+'">')+(options.textarea?escapeHtml(options.value||'')+'</textarea>':'')+'</div>',onClose:function(){if(!settled)resolve(null)},buttons:[
        {label:'取消',action:function(){settled=true;resolve(null)}},
        {label:'确定',className:'primary',action:function(){var v=document.getElementById(id).value.trim();if(options.required!==false&&!v){toast('请填写内容','error');return false}settled=true;resolve(v)}}
      ]});
    });
  }

  function loading(message){return'<div class="yp-loading">'+escapeHtml(message||'正在加载...')+'</div>'}
  function empty(message){return'<div class="yp-empty">'+escapeHtml(message||'暂无数据')+'</div>'}
  function formObject(form){
    var out={};new FormData(form).forEach(function(value,key){out[key]=value});
    form.querySelectorAll('input[type="checkbox"][name]').forEach(function(el){out[el.name]=el.checked});
    return out;
  }

  function notificationHtml(list,limit){
    list=(list||[]).slice(0,limit||list.length);
    if(!list.length)return empty('暂无消息通知');
    return'<div class="yp-notice-list">'+list.map(function(n){return'<div class="yp-notice '+(Number(n.isRead)===0?'unread':'')+'" data-notification-id="'+attr(n.id)+'"><span class="yp-notice-dot"></span><div class="yp-notice-main"><div class="yp-notice-title">'+text(n.title,'系统通知')+'</div><div class="yp-notice-content">'+text(n.content,'')+'</div><div class="yp-notice-time">'+date(n.createdAt)+'</div></div></div>'}).join('')+'</div>';
  }

  function attachNotifications(container,onChange){
    if(!container)return;
    container.querySelectorAll('[data-notification-id]').forEach(function(el){el.addEventListener('click',async function(){
      if(!el.classList.contains('unread'))return;
      try{await api('/notifications/'+el.dataset.notificationId+'/read',{method:'PUT'});el.classList.remove('unread');if(onChange)onChange()}catch(e){toast(e.message,'error')}
    })});bindInteractive(container);
  }

  function conversationHtml(items){
    if(!items||!items.length)return empty('双方同意订单后，会话与历史聊天记录会显示在这里');
    return'<div class="card-grid">'+items.map(function(c){return'<div class="c-card yp-conversation" data-chat-order="'+attr(c.orderId)+'"><div class="yp-card-top"><h3>'+text(c.counterpartName,'订单联系人')+'</h3>'+statusTag(c.orderStatus)+'</div><p>'+text(c.serviceType)+' · '+text(c.hospitalName)+' · '+date(c.serviceDate,true)+'</p><p class="yp-card-note">'+text(c.lastMessage,'还没有聊天消息，点击进入会话')+'</p><div class="yp-card-meta"><span>'+date(c.lastTime)+'</span><span>'+(c.chatOpen?'可继续沟通':'历史记录 · 只读')+'</span>'+(Number(c.unreadCount)>0?'<span class="yp-unread-line">'+c.unreadCount+' 条未读</span>':'')+'</div></div>'}).join('')+'</div>';
  }

  function attachConversations(container){
    if(!container)return;
    container.querySelectorAll('[data-chat-order]').forEach(function(el){el.addEventListener('click',function(){openChat(el.dataset.chatOrder)})});bindInteractive(container);
  }

  function messageHtml(messages){
    if(!messages||!messages.length)return'<div class="yp-empty">暂无聊天记录，可以先发送一条问候</div>';
    return messages.map(function(m){var mine=user&&String(m.fromUserId)===String(user.id);return'<div class="yp-msg '+(mine?'mine':'')+'"><div class="yp-msg-name">'+text(m.fromName,mine?'我':'对方')+'</div><div class="yp-msg-bubble">'+escapeHtml(m.content||'').replace(/\n/g,'<br>')+'</div><div class="yp-msg-time">'+date(m.createdAt)+'</div></div>'}).join('');
  }

  async function openChat(orderId){
    var timer=null,open=true,lastSignature='';
    var ctl=modal({title:'订单 #'+orderId+' · 私信聊天',wide:true,content:'<div class="yp-chat-layout"><div class="yp-chat-messages">'+loading('正在读取聊天记录...')+'</div><div class="yp-chat-compose"><input class="yp-input" maxlength="1000" placeholder="输入消息，与订单另一方沟通"><button class="yp-btn primary" type="button">发送</button></div></div>',buttons:[{label:'关闭'}],onClose:function(){if(timer)clearInterval(timer)}});
    var messagesEl=ctl.body.querySelector('.yp-chat-messages'),compose=ctl.body.querySelector('.yp-chat-compose'),input=compose.querySelector('input'),send=compose.querySelector('button');
    async function refresh(scroll){
      try{
        var results=await Promise.all([api('/chat/'+orderId+'/messages'),api('/chat/'+orderId+'/open')]);
        var messages=results[0]||[];open=!!results[1];var signature=messages.map(function(m){return m.id}).join(',');
        if(signature!==lastSignature){messagesEl.innerHTML=messageHtml(messages);lastSignature=signature;if(scroll!==false)messagesEl.scrollTop=messagesEl.scrollHeight}
        if(!open){if(compose&&compose.isConnected)compose.outerHTML='<div class="yp-readonly">服务已结束，无法继续聊天，仅可查看聊天记录</div>';if(timer){clearInterval(timer);timer=null}}
      }catch(e){messagesEl.innerHTML=empty(e.message);if(timer){clearInterval(timer);timer=null}}
    }
    async function sendMessage(){var content=input.value.trim();if(!content)return;if(!open)return;
      send.disabled=true;try{await api('/chat/'+orderId+'/send',{method:'POST',body:{content:content}});input.value='';await refresh(true)}catch(e){toast(e.message,'error')}finally{send.disabled=false;input.focus()}
    }
    send.addEventListener('click',sendMessage);input.addEventListener('keydown',function(e){if(e.key==='Enter'&&!e.shiftKey){e.preventDefault();sendMessage()}});
    await refresh(true);if(open)timer=setInterval(function(){refresh(false)},5000);bindInteractive(ctl.mask);
  }

  function dashboardNotificationBlock(title,count,list){
    return'<div class="c-card" style="cursor:default"><div class="yp-card-top"><h3>'+escapeHtml(title||'消息通知')+'</h3><span class="yp-status '+(count?'amber':'green')+'">'+Number(count||0)+' 条未读</span></div><div style="margin-top:12px">'+notificationHtml(list,5)+'</div><div class="yp-actions"><button class="yp-btn small" type="button" data-read-all '+(count?'':'disabled')+'>全部已读</button></div></div>';
  }

  window.YP={
    user:user,apiBase:apiBase,api:api,guardRole:guardRole,escape:escapeHtml,attr:attr,text:text,money:money,date:date,maskPhone:maskPhone,
    statusLabel:statusLabel,statusTag:statusTag,auditLabel:auditLabel,auditTag:auditTag,tone:tone,
    bindInteractive:bindInteractive,toast:toast,modal:modal,closeModal:closeModal,confirm:confirmBox,prompt:promptBox,
    loading:loading,empty:empty,formObject:formObject,notificationHtml:notificationHtml,attachNotifications:attachNotifications,
    conversationHtml:conversationHtml,attachConversations:attachConversations,openChat:openChat,dashboardNotificationBlock:dashboardNotificationBlock
  };

  document.addEventListener('DOMContentLoaded',function(){bindInteractive(document)});
})(window,document);
