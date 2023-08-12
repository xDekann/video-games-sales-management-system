import {createRouter, createWebHistory} from 'vue-router' 
import Register from '@/components/Register.vue';
import Login from '@/components/Login.vue';
import Gamepanel from '@/components/employee/Gamepanel.vue';
import Addgame from '@/components/employee/Addgame.vue';

const routes = [
  { path: '/register', component: Register },
  { path: '/login', component: Login },
  { path: '/gamepanel', component: Gamepanel},
  { path: '/gamepanel/addgame', component: Addgame}
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
  })

export default router;