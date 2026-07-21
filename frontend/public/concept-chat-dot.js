(function(window,document){
  'use strict';var YP=window.YP;if(!YP)return;
  // 订单/私信所在的轮盘节点索引：客户第 3 个节点，陪诊师第 2 个节点
  var orderNodeIndex=window.YPC?3:(window.YPComp?2:-1);
  if(orderNodeIndex<0)return;
  var SVGNS='http://www.w3.org/2000/svg';
  var dot=null,count=0,started=false;

  function nodeCircle(){
    var group=document.getElementById('nodesGroup');if(!group)return null;
    var g=group.children[orderNodeIndex];if(!g)return null;
    // 节点结构：halo, circle, icon, label —— 取主圆
    return g.children[1]||null;
  }
  function ensureDot(){
    if(dot&&dot.isConnected)return dot;
    // 挂到 SVG 根节点而非 #nodesGroup：内联 updateAll 会按索引遍历 nodesGroup.children，
    // 追加额外元素会让它取到不存在的子节点而报错、破坏轮盘动画。
    var svg=document.getElementById('arcSvg');if(!svg)return null;
    dot=document.createElementNS(SVGNS,'circle');
    dot.setAttribute('r',5);dot.setAttribute('class','arc-chat-dot');
    dot.style.display='none';svg.appendChild(dot);return dot;
  }
  function sync(){
    var c=nodeCircle(),d=ensureDot();
    if(c&&d&&count>0){
      var cx=Number(c.getAttribute('cx')),cy=Number(c.getAttribute('cy'));
      d.setAttribute('cx',cx+12);d.setAttribute('cy',cy-12);d.style.display='';
    }else if(d){d.style.display='none'}
    requestAnimationFrame(sync);
  }

  // 由 renderOrders 调用：传入未读会话总数
  YP.setChatDot=function(n){count=Number(n)||0;if(!started){started=true;requestAnimationFrame(sync)}};
})(window,document);
