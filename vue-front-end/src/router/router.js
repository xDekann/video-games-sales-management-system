import {createRouter, createWebHistory} from 'vue-router' 
import Register from '@/components/Register.vue';
import Login from '@/components/Login.vue';
import Gamepanel from '@/components/employee/Gamepanel.vue';
import Addgame from '@/components/employee/Addgame.vue';
import Userpanel from '@/components/admin/Userpanel.vue';
import Edituser from '@/components/admin/EditUser.vue';
import AddUser from '@/components/admin/AddUser.vue';

import jsCookie from 'js-cookie';
const routes = [
  { path: '/register', component: Register },
  { path: '/login', component: Login },
  { path: '/gamepanel', component: Gamepanel},
  { path: '/gamepanel/addgame', component: Addgame},
  { path: '/userpanel', component: Userpanel},
  { path: '/userpanel/edituser/:id', name: "edit-user" , component: Edituser},
  { path: '/userpanel/adduser', component: AddUser}
]


const protectedRoutes = ['/gamepanel', '/gamepanel/addgame',
 '/userpanel', '/userpanel/adduser', '/userpanel/edituser/']
const roleAllowedRoutes = {
  'ROLE_EMPLOYEE': ['/gamepanel', '/gamepanel/addgame'],
  'ROLE_ADMIN': ['/userpanel', '/userpanel/adduser', '/userpanel/edituser/']
}


const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
  })

  router.beforeEach((to, from, next) => {
    let role = null;
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