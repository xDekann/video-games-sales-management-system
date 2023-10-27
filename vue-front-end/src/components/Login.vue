<template>
    <div class="login-container">
      <h2 class="sign-in-title">{{translations.login.title}}</h2>
      <div class="form-container mt-3">
        <form @submit.prevent="submitForm" class="login-form">
          <label for="username">{{translations.login.username}}</label>
          <input type="text" id="username" v-model="username" />
          <label for="password">{{translations.login.password}}</label>
          <input type="password" id="password" v-model="password" />
          <button type="submit">{{translations.login.login}}</button>
        </form>
      </div>
      <p v-if="message" class="message">{{ message }}</p>
      <router-link to="/register" class="register-link">{{translations.login.register}}</router-link>
    </div>
    <Footer :translations="translations"></Footer>
  </template>
  
  <script>
  import axios from 'axios';
  import Cookies from 'js-cookie';
  import Footer from '@/components/Footer.vue';
  
  export default {
    props: ['translations'],
    data() {
      return {
        username: '',
        password: '',
        message: '',
      };
    },
    created() {
      const registered = this.$route.query.registered;
      const loggedout = this.$route.query.loggedout;
      if (registered === 'true') {
        this.message = this.translations.login.registration;
      }
      if (loggedout === 'true') {
        this.message = this.translations.login.logout;
      }
    },
    components: {
      Footer,
    },
    methods: {
      async submitForm() {
        try {
          const response = await axios.post('/v1/login', {
            username: this.username,
            password: this.password,
          });
  
          if (response.status === 200) {
            this.message = this.translations.login.success;
            const roleCookie = atob(Cookies.get('ROLE'));
            switch (roleCookie) {
              case 'ROLE_EMPLOYEE':
                this.$router.push('/gamepanel');
                break;
              case 'ROLE_USER':
                this.$router.push('/games');
                break;
              case 'ROLE_ADMIN':
                this.$router.push('/userpanel');
                break;
              default:
                this.message = this.translations.login.error;
            }
          } else if (response.status === 503){
            this.message = this.translations.login.serviceUnavailable;
          } else {
            this.message = this.translations.login.wrongUP;
          }
        } catch (error) {
          this.message = this.translations.login.wrongUP;
        }
      },
    },
  };
  </script>
  
  <style scoped>
  .login-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
  }
  
  .login-form {
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.4);
  }
  
  .label {
    font-weight: bold;
    margin-top: 10px;
  }
  
  input {
    width: 100%;
    padding: 10px;
    margin-top: 5px;
    border: 1px solid #ccc;
    border-radius: 3px;
  }
  
  button {
    width: 100%;
    padding: 10px;
    margin-top: 10px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 3px;
    cursor: pointer;
  }
  
  .message {
    color: red;
    margin-top: 10px;
  }
  
  .register-link {
    margin-top: 10px;
    text-align: center;
    text-decoration: underline;
    color: #007bff;
    cursor: pointer;
  }
  </style>
  