<template>
  <div class="registration-container mt-5">
    <h2 class="register-title">Register</h2>
    <form @submit.prevent="submitForm" class="registration-form">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="username" class="form-control" />
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="password" v-model="password" class="form-control" />
      </div>

      <div class="form-group">
        <label for="name">Name:</label>
        <input type="text" id="name" v-model="name" class="form-control" />
      </div>

      <div class="form-group">
        <label for="surname">Surname:</label>
        <input type="text" id="surname" v-model="surname" class="form-control" />
      </div>

      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="email" class="form-control" />
      </div>

      <div class="form-group">
        <label for="city">City:</label>
        <input type="text" id="city" v-model="city" class="form-control" />
      </div>

      <div class="form-group">
        <label for="address">Address:</label>
        <textarea id="address" v-model="address" class="form-control"></textarea>
      </div>

      <button type="submit" class="btn btn-primary">Register</button>
    </form>
    <div class="center-button">
      <button @click="backToLogin" class="btn btn-link">Back to Login</button>
    </div>
    <p v-if="message" class="message">{{ message }}</p>
    <div v-if="errors.length > 0" class="error-container">
      <ul class="error-list">
        <li v-for="error in errors" :key="error.id" class="error-item">{{ error.defaultMessage }}</li>
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
          this.errors = error.response.data.errors;
        }
        else {
          this.message = "An error has occured";
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

  .error-container {
    margin-top: 10px;
    padding: 10px;
    background-color: #ff6b6b;
    border: 1px solid #e82727;
    border-radius: 5px;
  }

  .error-list {
    list-style: none;
    padding: 0;
  }

.error-item {
    color: #e82727;
    margin-bottom: 5px;
}
.center-button {
    display: flex;
    justify-content: center;
    margin-top: 10px;
}
</style>