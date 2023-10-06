import { createApp } from 'vue'
import Vuex from 'vuex';
import App from './App.vue'
import router from './router/router';
import VueClickAway from "vue3-click-away";
import 'bootstrap/dist/css/bootstrap.css'
import VueSweetalert2 from 'vue-sweetalert2';
import './styles/dynamic-global.css';
import translationsPL from '@/languages/pl.json';
import translationsEN from '@/languages/en.json';
import Cookies from 'js-cookie';

const store = new Vuex.Store({
    state: {
      translations: translationsEN,
    },
    mutations: {
      setTranslations(state, language) {
        state.translations = language === 'pl' ? translationsPL : translationsEN;
      },
    },
    actions: {
      changeLanguage({ commit }, language) {
        commit('setTranslations', language);
      },
    },
  });

const langCookie = Cookies.get('LANG');
if (langCookie) {
    store.dispatch('changeLanguage', langCookie);
} else {
    store.dispatch('changeLanguage', 'en');
}

createApp(App).use(router).use(VueClickAway).use(VueSweetalert2).use(store).mount("#app");

export default store;