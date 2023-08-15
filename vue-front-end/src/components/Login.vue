<template>
    <div>
        <h2>Login</h2>
        <form @submit.prevent="submitForm">
            <label for="username">Username:</label>
            <input type="text" id="username" v-model="username" />

            <label for="password">Password:</label>
            <input type="password" id="password" v-model="password" />

            <button type="submit">Login</button>
        </form>
        <p v-if="message">{{ message }}</p>
    </div>
    <!--
    <div v-if="role">
        <form @submit.prevent="logout">
            <button type="submit">Logout</button>
        </form>
    </div>
-->
</template>
  
<script>
import axios from 'axios';
import Cookies from 'js-cookie';
//import { onMounted, ref } from 'vue';

export default {
    /*
    setup() {
        const role = ref(null);
        onMounted(() => {
            const roleCookie = Cookies.get('ROLE');
            role.value = roleCookie ? atob(roleCookie) : null;
        });
        return {role};
    },
    */
    data() {
        return {
            username: '',
            password: '',
            message: '',
        };
    },
    methods: {
        async submitForm() {
            try {
                const response = await axios.post('/v1/login', {
                    username: this.username,
                    password: this.password
                });

                //this.message(response.data);

                if (response.status === 200) {
                    // Login successful, handle the logic here
                    this.message = 'Login successful!';
                    //this.message = response.headers['Set-Cookie'][0].split(';')[0].split('=')[1];
                    //Cookies.set('ROLE', response.data.role, { expires: 1 }); // Set cookie to expire in 1 days
                    const roleCookie = atob(Cookies.get('ROLE'))
                    switch(roleCookie) {
                        case 'ROLE_EMPLOYEE': this.$router.push('/gamepanel'); break;
                        case 'ROLE_USER': this.$router.push('/home'); break;
                        case 'ROLE_ADMIN': this.$router.push('/userpanel'); break;
                        default: this.message = "Login error occurred."
                    }
                    //this.$router.push('/register');
                } else {
                    this.message = 'Login failed!';
                }
            } catch (error) {
                this.message = 'An error occurred during login.';
            }
        },
        async logout() {
            await axios.get('/v1/logout');
            Cookies.remove('ROLE');
            this.role = null;
        }
    }
};
</script>

  
  
  
  
  