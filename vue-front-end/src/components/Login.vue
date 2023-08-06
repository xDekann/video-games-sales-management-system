<template>
    <div>
        <h2>Login</h2>
        <form @submit.prevent="submitForm">
            <label for="email">Email:</label>
            <input type="email" id="email" v-model="email" />

            <label for="password">Password:</label>
            <input type="password" id="password" v-model="password" />

            <button type="submit">Login</button>
        </form>
        <p v-if="message">{{ message }}</p>
    </div>
</template>
  
<script>
import axios from 'axios';

export default {
    data() {
        return {
            email: '',
            password: '',
            message: ''
        };
    },
    methods: {
        async submitForm() {
            try {
                const response = await axios.post('/v1/login', {
                    email: this.email,
                    password: this.password
                });

                if (response.status === 200) {
                    // Login successful, handle the logic here
                    this.message = 'Login successful!';
                } else {
                    this.message = 'Login failed!';
                }
            } catch (error) {
                this.message = 'An error occurred during login.';
            }
        }
    }
};
</script>

  
  
  
  
  