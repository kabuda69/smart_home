import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    devices: [],
    alerts: [],
    unreadAlertCount: 0
  },
  mutations: {
    SET_USER: function(state, user) {
      state.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    SET_DEVICES: function(state, devices) {
      state.devices = devices
    },
    SET_ALERTS: function(state, alerts) {
      state.alerts = alerts
    },
    SET_UNREAD_COUNT: function(state, count) {
      state.unreadAlertCount = count
    },
    UPDATE_DEVICE: function(state, device) {
      var idx = -1
      for (var i = 0; i < state.devices.length; i++) {
        if (state.devices[i].id === device.id) {
          idx = i
          break
        }
      }
      if (idx !== -1) {
        Vue.set(state.devices, idx, device)
      }
    },
    LOGOUT: function(state) {
      state.user = null
      state.devices = []
      state.alerts = []
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('role')
    }
  },
  actions: {
    login: function(context, credentials) {
      return axios.post('/api/auth/login', credentials).then(function(response) {
        var data = response.data
        if (data.success) {
          localStorage.setItem('token', data.data.token)
          localStorage.setItem('role', data.data.role)
          context.commit('SET_USER', {
            id: data.data.userId,
            username: data.data.username,
            role: data.data.role
          })
        }
        return data
      })
    },
    register: function(context, userData) {
      return axios.post('/api/auth/register', userData).then(function(response) {
        var data = response.data
        if (data.success) {
          localStorage.setItem('token', data.data.token)
          localStorage.setItem('role', data.data.role)
          context.commit('SET_USER', {
            id: data.data.userId,
            username: data.data.username,
            role: data.data.role
          })
        }
        return data
      })
    },
    fetchDevices: function(context) {
      return axios.get('/api/devices').then(function(response) {
        var data = response.data
        if (data.success) {
          context.commit('SET_DEVICES', data.data)
        }
      })
    },
    fetchAlerts: function(context) {
      return axios.get('/api/alerts/unread').then(function(response) {
        var data = response.data
        if (data.success) {
          context.commit('SET_ALERTS', data.data)
          context.commit('SET_UNREAD_COUNT', data.data.length)
        }
      }).catch(function(e) {
        console.error('获取警报失败', e)
      })
    },
    logout: function(context) {
      context.commit('LOGOUT')
    }
  }
})
