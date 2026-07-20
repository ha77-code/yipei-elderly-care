(function(window,document){
  'use strict';var YP=window.YP;if(!YP)return;
  function context(){if(window.YPC)return{host:window.YPC,state:window.YPC.state,page:3,tabAttr:'data-order-tab'};if(window.YPComp)return{host:window.YPComp,state:window.YPComp.state,page:2,tabAttr:'data-companion-order-tab'};return null}
  function noticeById(id){var ctx=context();return ctx&&ctx.state.notifications.find(function(n){return String(n.id)===String(id)})}
  function markChatNotices(orderId){var ctx=context();if(!ctx)return;var list=ctx.state.notifications.filter(function(n){return n.type==='CHAT_MESSAGE'&&String(n.relatedId)===String(orderId)&&Number(n.isRead)===0});if(!list.length)return;list.forEach(function(n){n.isRead=1});ctx.state.notificationUnread=Math.max(0,Number(ctx.state.notificationUnread||0)-list.length);Promise.allSettled(list.map(function(n){return YP.api('/notifications/'+n.id+'/read',{method:'PUT'})})).then(function(){ctx.host.renderDashboard()})}
  function jump(orderId){var ctx=context();if(!ctx||!orderId)return;ctx.state.orderTab='chats';ctx.host.setActive(ctx.host.pages.orders,ctx.tabAttr,'chats');ctx.host.renderOrders();window.goPage(ctx.page);setTimeout(function(){YP.openChat(orderId)},320)}

  var originalOpenChat=YP.openChat;
  YP.openChat=function(orderId){markChatNotices(orderId);return originalOpenChat(orderId)};

  var originalAttachNotifications=YP.attachNotifications;
  YP.attachNotifications=function(container,onChange){originalAttachNotifications(container,onChange);if(!container)return;container.querySelectorAll('[data-notification-id]').forEach(function(el){var n=noticeById(el.dataset.notificationId);if(!n||n.type!=='CHAT_MESSAGE'||!n.relatedId)return;el.title='点击进入订单 #'+n.relatedId+' 的聊天';el.addEventListener('click',function(){jump(n.relatedId)},true)})};

  var originalAttachConversations=YP.attachConversations;
  YP.attachConversations=function(container){originalAttachConversations(container);if(!container)return;container.querySelectorAll('[data-chat-order]').forEach(function(el){el.addEventListener('click',function(){markChatNotices(el.dataset.chatOrder)},true)})};
})(window,document);
