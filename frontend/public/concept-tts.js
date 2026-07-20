(function(window,document){
  'use strict';var YP=window.YP;if(!YP)return;
  var current=null; // {audio,url,button,label}

  function resetButton(){
    if(!current||!current.button)return;
    current.button.classList.remove('yp-speaking');
    current.button.innerHTML=current.label;
  }
  function stop(){
    if(!current)return;
    try{current.audio.pause()}catch(e){}
    if(current.url)URL.revokeObjectURL(current.url);
    resetButton();current=null;
  }

  /* opts: { path, method, body, button }。path 指向返回 audio/mpeg 的接口，出错时返回 JSON。 */
  async function speak(opts){
    opts=opts||{};var button=opts.button;
    // 再次点击同一个按钮 = 停止
    if(current&&current.button===button){stop();return}
    stop();
    var headers={};if(YP.user&&YP.user.id)headers['X-User-Id']=String(YP.user.id);
    var init={method:opts.method||'GET',headers:headers};
    if(opts.body!=null){headers['Content-Type']='application/json';init.body=JSON.stringify(opts.body)}
    var label=button?button.innerHTML:'';
    if(button){button.classList.add('yp-speaking');button.innerHTML='◼ 停止朗读'}
    var res;
    try{res=await fetch(YP.apiBase+opts.path,init)}
    catch(e){if(button){button.classList.remove('yp-speaking');button.innerHTML=label}YP.toast('无法连接语音服务，请确认后端已启动','error');return}
    var type=res.headers.get('content-type')||'';
    if(!res.ok||type.indexOf('audio')<0){
      var msg='语音生成失败';
      try{var j=await res.json();if(j&&j.message)msg=j.message}catch(e){}
      if(button){button.classList.remove('yp-speaking');button.innerHTML=label}
      YP.toast(msg,'error');return;
    }
    var blob=await res.blob(),url=URL.createObjectURL(blob),audio=new Audio(url);
    current={audio:audio,url:url,button:button,label:label};
    audio.addEventListener('ended',stop);
    audio.addEventListener('error',function(){YP.toast('音频播放失败','error');stop()});
    try{await audio.play()}catch(e){YP.toast('浏览器阻止了自动播放，请再次点击','error');stop()}
  }

  YP.speak=speak;YP.stopSpeak=stop;
  document.addEventListener('DOMContentLoaded',function(){YP.bindInteractive(document)});
})(window,document);
