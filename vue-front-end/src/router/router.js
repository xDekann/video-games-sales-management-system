import {createRouter, createWebHistory} from 'vue-router' 
import Register from '@/components/Register.vue';
import Login from '@/components/Login.vue';
import Gamepanel from '@/components/employee/Gamepanel.vue';
import Addgame from '@/components/employee/Addgame.vue';
import Userpanel from '@/components/admin/Userpanel.vue';
import Edituser from '@/components/admin/EditUser.vue';
import AddUser from '@/components/admin/AddUser.vue';
import Games from '@/components/user/Games.vue';
import Cart from '@/components/user/Cart.vue';
import Checkout from '@/components/user/Checkout.vue';
import TransactionHistory from '@/components/user/TransactionHistory.vue';

import jsCookie from 'js-cookie';
const routes = [
  { path: '/register', component: Register },
  { path: '/login', component: Login },
  { path: '/', redirect: '/login'},
  { path: '/home', redirect: '/login'},
  { path: '/gamepanel', component: Gamepanel},
  { path: '/gamepanel/addgame', component: Addgame},
  { path: '/userpanel', component: Userpanel},
  { path: '/userpanel/edituser/:id', name: "edit-user" , component: Edituser},
  { path: '/userpanel/adduser', component: AddUser},
  { path: '/games', component: Games},
  { path: '/cart',  component: Cart},
  { path: '/checkout', component: Checkout },
  { path: '/profile', component: TransactionHistory}
]


const protectedRoutes = ['/gamepanel', '/gamepanel/addgame',
 '/userpanel', '/userpanel/adduser', '/userpanel/edituser/', '/games', 
 '/cart', '/checkout', '/profile']
const roleAllowedRoutes = {
  'ROLE_EMPLOYEE': ['/gamepanel', '/gamepanel/addgame'],
  'ROLE_ADMIN': ['/userpanel', '/userpanel/adduser', '/userpanel/edituser/'],
  'ROLE_USER': ['/games', '/cart', '/checkout', '/profile']
}


const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
  })

router.beforeEach((to, from, next) => {
  let role = null;
  document.title = 'Game store';
  if (jsCookie.get('ROLE')) {
    role = atob(jsCookie.get('ROLE'));
  }
  if (!protectedRoutes.some(route => to.path === route || to.path.startsWith(route))) {
    next();
    return;
  }
  if (role && protectedRoutes.some(route => to.path === route || to.path.startsWith(route))) {
    if (roleAllowedRoutes[role].some(allowedRoute => allowedRoute.startsWith(to.path) || to.path.startsWith(allowedRoute))) {
      next();
      return;
    }
  }
  next('/login');
});
  

export default router;