import request from './request'

export function getVoices() {
  return request({
    url: '/api/tts/voices',
    method: 'get'
  })
}

export function speakText(text, voiceType) {
  return request({
    url: '/api/tts/speak',
    method: 'post',
    data: { text, voiceType },
    responseType: 'blob'
  })
}
