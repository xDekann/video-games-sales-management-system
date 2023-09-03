import { createApp } from 'vue'
import App from './App.vue'
import router from './router/router';
import VueClickAway from "vue3-click-away";
import 'bootstrap/dist/css/bootstrap.css' // Import Bootstrap CSS


createApp(App).use(router).use(VueClickAway).mount('#app')
