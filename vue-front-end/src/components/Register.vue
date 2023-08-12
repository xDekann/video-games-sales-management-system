<template>
  <div>
    <form @submit.prevent="submitForm">
      <label for="username">Username:</label>
      <input type="text" id="username" v-model="username" />

      <label for="password">Password:</label>
      <input type="password" id="password" v-model="password" />

      <label for="name">Name:</label>
      <input type="text" id="name" v-model="name" />

      <label for="surname">Surname:</label>
      <input type="text" id="surname" v-model="surname" />

      <label for="email">Email:</label>
      <input type="email" id="email" v-model="email" />

      <label for="city">City:</label>
      <input type="text" id="city" v-model="city" />

      <label for="address">Address:</label>
      <textarea id="address" v-model="address"></textarea>

      <button type="submit">Register</button>
    </form>
    <p v-if="message">{{ message }}</p>
    <div v-if="errors.length > 0">
      <ul>
        <li v-for="error in errors" :key="error.id">{{ error.defaultMessage }}</li> <!--id just for the vuejs case, it does not exist-->
      </ul>
    </div>
  </div>
</template>
  
<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      password: '',
      name: '',
      surname: '',
      email: '',
      city: '',
      address: '',
      message: '',
      errors: []
    };
  },
  methods: {
    async submitForm() {
      try {
        const response = await axios.post('/v1/register', {
          username: this.username,
          password: this.password,
          name: this.name,
          surname: this.surname,
          email: this.email,
          city: this.city,
          address: this.address,
          authorityName: 'EMPLOYEE'
        });

        if (response.status === 200) {
          this.message = 'Registration successful!';
          this.$router.push('/login');
        } 
        else {
          this.message = 'Registration failed!';
        }
      } catch (error) {
        if (error.response.status === 403) {
          this.message = 'Unauthorized!';
        }
        if (error.response.status === 400) {
          this.errors = error.response.data.errors;
        }
        else {
          this.message = "An error has occured";
        } 
      }
    }
  }
};
</script>