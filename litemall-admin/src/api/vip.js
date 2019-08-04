import request from '@/utils/request'

export function listAd(query) {
  return request({
    url: '/vip/list',
    method: 'get',
    params: query
  })
}

export function createAd(data) {
  return request({
    url: '/vip/create',
    method: 'post',
    data
  })
}

export function readAd(data) {
  return request({
    url: '/vip/read',
    method: 'get',
    data
  })
}

export function updateAd(data) {
  return request({
    url: '/vip/update',
    method: 'post',
    data
  })
}

export function deleteAd(data) {
  return request({
    url: '/vip/delete',
    method: 'post',
    data
  })
}
