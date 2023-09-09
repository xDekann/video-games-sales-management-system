import { createApp } from 'vue'
import App from './App.vue'
import router from './router/router';
import VueClickAway from "vue3-click-away";
import 'bootstrap/dist/css/bootstrap.css'
import VueSweetalert2 from 'vue-sweetalert2';


createApp(App).use(router).use(VueClickAway).use(VueSweetalert2).mount('#app')
