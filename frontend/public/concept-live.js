(function(window,document){
  'use strict';var YP=window.YP;if(!YP)return;
  async function quiet(path,fallback){try{return await YP.api(path)}catch(e){return fallback}}
  async function tick(){if(document.hidden)return;
    if(window.YPC){var C=window.YPC,S=C.state,r=await Promise.all([quiet('/notifications',S.notifications),quiet('/notifications/unread',S.notificationUnread),quiet('/chat/conversations',S.conversations),quiet('/order/my',S.orders),quiet('/service-request/list',S.requests)]);S.notifications=r[0];S.notificationUnread=Number(r[1]||0);S.conversations=r[2];S.orders=r[3];S.requests=r[4];C.renderDashboard();C.renderOrders();C.renderRequests();return}
    if(window.YPComp){var P=window.YPComp,PS=P.state,pr=await Promise.all([quiet('/notifications',PS.notifications),quiet('/notifications/unread',PS.notificationUnread),quiet('/chat/conversations',PS.conversations),quiet('/order/my',PS.orders),quiet('/order/available',PS.directed)]);PS.notifications=pr[0];PS.notificationUnread=Number(pr[1]||0);PS.conversations=pr[2];PS.orders=pr[3];PS.directed=pr[4];P.renderDashboard();P.renderOrders();P.renderAvailable();return}
    if(window.YPAdmin){var A=window.YPAdmin,AS=A.state,ar=await Promise.all([quiet('/notifications',AS.notifications),quiet('/notifications/unread',AS.notificationUnread),quiet('/admin/statistics',AS.stats)]);AS.notifications=ar[0];AS.notificationUnread=Number(ar[1]||0);AS.stats=ar[2];A.renderDashboard();A.renderStats()}
  }
  document.addEventListener('DOMContentLoaded',function(){setInterval(tick,20000)});
})(window,document);
