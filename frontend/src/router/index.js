import Evaluate from '../views/companion/Evaluate.vue'
import Service from '../views/companion/Service.vue'
import Order from '../views/companion/Order.vue'
import CompanionList from '../views/companion/CompanionList.vue'
import Profile from '../views/companion/Profile.vue'
import Apply from '../views/companion/Apply.vue'
import Audit from '../views/admin/Audit.vue'
import Vue from "vue";
import VueRouter from "vue-router";
import HomeView from "../views/HomeView.vue";

Vue.use(VueRouter);

const routes = [{
  path:'/admin/audit',
  name:'Audit',
  component:Audit
},

  {
    path:'/companion/apply',
    name:'Apply',
    component:Apply
},

  {
  path:'/companion/profile',
  name:'Profile',
  component:Profile
},

  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path:'/companion/list',
    name:'CompanionList',
    component:CompanionList
  },
  {
    path:'/companion/order',
    name:'Order',
    component:Order
  },
  {
    path:'/companion/service',
    name:'Service',
    component:Service
  },
  {
    path:'/companion/evaluate',
    name:'Evaluate',
    component:Evaluate
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
