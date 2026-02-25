import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

var routes = [
  { path: '/', redirect: '/dashboard' },
  { 
    path: '/login', 
    name: 'Login', 
    component: function() { return import('../views/Login.vue') }, 
    meta: { guest: true } 
  },
  { 
    path: '/register', 
    name: 'Register', 
    component: function() { return import('../views/Register.vue') }, 
    meta: { guest: true } 
  },
  { 
    path: '/dashboard', 
    name: 'Dashboard', 
    component: function() { return import('../views/Dashboard.vue') }, 
    meta: { auth: true } 
  },
  { 
    path: '/devices', 
    name: 'Devices', 
    component: function() { return import('../views/Devices.vue') }, 
    meta: { auth: true } 
  },
  { 
    path: '/scenes', 
    name: 'Scenes', 
    component: function() { return import('../views/Scenes.vue') }, 
    meta: { auth: true } 
  },
  { 
    path: '/alerts', 
    name: 'Alerts', 
    component: function() { return import('../views/Alerts.vue') }, 
    meta: { auth: true } 
  },
  { 
    path: '/statistics', 
    name: 'Statistics', 
    component: function() { return import('../views/Statistics.vue') }, 
    meta: { auth: true } 
  },
  { 
    path: '/settings', 
    name: 'Settings', 
    component: function() { return import('../views/Settings.vue') }, 
    meta: { auth: true } 
  },
  { 
    path: '/feedback', 
    name: 'Feedback', 
    component: function() { return import('../views/Feedback.vue') }, 
    meta: { auth: true } 
  },
  { 
    path: '/admin', 
    name: 'Admin', 
    component: function() { return import('../views/Admin.vue') }, 
    meta: { auth: true, admin: true } 
  },
  { 
    path: '/share/:uuid', 
    name: 'Share', 
    component: function() { return import('../views/Share.vue') }
  }
]

var router = new VueRouter({
  mode: 'history',
  routes: routes
})

router.beforeEach(function(to, from, next) {
  var token = localStorage.getItem('token')
  var role = localStorage.getItem('role')
  
  if (to.meta.auth && !token) {
    return next('/login')
  }
  if (to.meta.guest && token) {
    return next('/dashboard')
  }
  if (to.meta.admin && role !== 'ADMIN') {
    return next('/dashboard')
  }
  next()
})

export default router
