import {createRouter, createWebHistory} from 'vue-router' 
import Register from '@/components/Register.vue';
import Login from '@/components/Login.vue';
import Gamepanel from '@/components/employee/Gamepanel.vue';
import Addgame from '@/components/employee/Addgame.vue';

import jsCookie from 'js-cookie';
const routes = [
  { path: '/register', component: Register },
  { path: '/login', component: Login },
  { path: '/gamepanel', component: Gamepanel},
  { path: '/gamepanel/addgame', component: Addgame}
]


const protectedRoutes = ['/gamepanel', '/gamepanel/addgame']
const roleAllowedRoutes = {
  'ROLE_EMPLOYEE': ['/gamepanel', '/gamepanel/addgame']
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
  console.log(role);
  console.log(to.path);
  if (!protectedRoutes.includes(to.path)) {
      next();
      return;
  }


  if (role && protectedRoutes.includes(to.path)) {
    if (roleAllowedRoutes[role].includes(to.path)) {
      next();
      return;
    }
  }
  next('/login');
  return;
})
  

export default router;