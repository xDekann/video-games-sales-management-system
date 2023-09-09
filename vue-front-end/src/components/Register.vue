<template>
  <div class="registration-container mt-5">
    <h2 class="register-title">Register</h2>
    <form @submit.prevent="submitForm" class="registration-form">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="username" class="form-control" :class="{ 'is-invalid': errors.username }" />
        <div class="invalid-feedback" v-if="errors.username">{{ errors.username }}</div>
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="password" v-model="password" class="form-control" :class="{ 'is-invalid': errors.password }" />
        <div class="invalid-feedback" v-if="errors.password">{{ errors.password }}</div>
      </div>

      <div class="form-group">
        <label for="name">Name:</label>
        <input type="text" id="name" v-model="name" class="form-control" :class="{ 'is-invalid': errors.name }" />
        <div class="invalid-feedback" v-if="errors.name">{{ errors.name }}</div>
      </div>

      <div class="form-group">
        <label for="surname">Surname:</label>
        <input type="text" id="surname" v-model="surname" class="form-control" :class="{ 'is-invalid': errors.surname }" />
        <div class="invalid-feedback" v-if="errors.surname">{{ errors.surname }}</div>
      </div>

      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="email" class="form-control" :class="{ 'is-invalid': errors.email }" />
        <div class="invalid-feedback" v-if="errors.email">{{ errors.email }}</div>
      </div>

      <div class="form-group">
        <label for="city">City:</label>
        <input type="text" id="city" v-model="city" class="form-control" :class="{ 'is-invalid': errors.city }" />
        <div class="invalid-feedback" v-if="errors.city">{{ errors.city }}</div>
      </div>

      <div class="form-group">
        <label for="address">Address:</label>
        <textarea id="address" v-model="address" class="form-control" :class="{ 'is-invalid': errors.address }"></textarea>
        <div class="invalid-feedback" v-if="errors.address">{{ errors.address }}</div>
      </div>

      <button type="submit" class="btn btn-primary">Register</button>
    </form>
    <div class="center-button">
      <button @click="backToLogin" class="btn btn-link">Back to Login</button>
    </div>
    <p v-if="message" class="message">{{ message }}</p>
  </div>
  <Footer></Footer>
</template>

<script>
import axios from 'axios';
import Footer from '@/components/Footer.vue';

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
      errors: {}
    };
  },
  components: {
      Footer,
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
          authorityName: 'USER'
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
          this.errors = {};
          const validationErrors = error.response.data.errors;
          for (const validationError of validationErrors) {
            this.errors[validationError.field] = validationError.defaultMessage;
          }
        }
        else {
          this.message = "An error has occurred";
        } 
      }
    },
    backToLogin() {
      this.$router.push('/login');
    },
  }
};
</script>

<style scoped>
  .registration-container {
    max-width: 400px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f9f9f9;
    border: 1px solid #ddd;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    margin-bottom: 50px;
  }

  .register-title {
    text-align: center;
    font-size: 24px;
    margin-bottom: 20px;
  }

  .registration-form .form-group {
    margin-bottom: 15px;
  }

  .registration-form label {
    display: block;
    font-weight: bold;
  }

  .registration-form .form-control {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
  }

  .registration-form button {
    width: 100%;
    padding: 10px;
    background-color: #007bff;
    color: #fff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }

  .message {
    text-align: center;
    margin-top: 10px;
  }

  .error-item {
    color: #e82727;
    margin-top: 5px;
  }

  .center-button {
    display: flex;
    justify-content: center;
    margin-top: 10px;
  }

  .form-control.is-invalid {
    border-color: #e82727;
  }

  .invalid-feedback {
    color: #e82727;
  }
</style>
