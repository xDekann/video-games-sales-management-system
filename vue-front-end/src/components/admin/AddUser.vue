<template>
  <div class="registration-container mt-5">
    <h2 class="register-title">{{translations.register.addUser}}</h2>
    <form @submit.prevent="submitForm" class="registration-form">
      <div class="form-group">
        <label for="name">{{translations.register.name}}</label>
        <input v-model="user.name" type="text" id="name" class="form-control" :class="{ 'is-invalid': errors.name }" />
        <div class="invalid-feedback" v-if="errors.name">{{ errors.name }}</div>
      </div>

      <div class="form-group">
        <label for="surname">{{translations.register.surname}}</label>
        <input v-model="user.surname" type="text" id="surname" class="form-control" :class="{ 'is-invalid': errors.surname }" />
        <div class="invalid-feedback" v-if="errors.surname">{{ errors.surname }}</div>
      </div>

      <div class="form-group">
        <label for="email">{{translations.register.email}}</label>
        <input v-model="user.email" type="email" id="email" class="form-control" :class="{ 'is-invalid': errors.email }" />
        <div class="invalid-feedback" v-if="errors.email">{{ errors.email }}</div>
      </div>

      <div class="form-group">
        <label for="username">{{translations.register.username}}</label>
        <input v-model="user.username" type="text" id="username" class="form-control" :class="{ 'is-invalid': errors.username }" />
        <div class="invalid-feedback" v-if="errors.username">{{ errors.username }}</div>
      </div>

      <div class="form-group">
        <label for="password">{{translations.register.password}}</label>
        <input v-model="user.password" type="password" id="password" class="form-control" :class="{ 'is-invalid': errors.password }" />
        <div class="invalid-feedback" v-if="errors.password">{{ errors.password }}</div>
      </div>

      <div class="form-group">
        <label for="city">{{translations.register.city}}</label>
        <input v-model="user.city" type="text" id="city" class="form-control" :class="{ 'is-invalid': errors.city }" />
        <div class="invalid-feedback" v-if="errors.city">{{ errors.city }}</div>
      </div>

      <div class="form-group">
        <label for="address">{{translations.register.address}}</label>
        <textarea id="address" v-model="user.address" class="form-control" :class="{ 'is-invalid': errors.address }"></textarea>
        <div class="invalid-feedback" v-if="errors.address">{{ errors.address }}</div>
      </div>

      <div class="form-group">
        <label>{{translations.register.role}}</label>
        <div>
          <label><input type="radio" v-model="user.authorityName" value="USER" /> {{translations.register.user}}</label>
          <label><input type="radio" v-model="user.authorityName" value="EMPLOYEE" /> {{translations.register.employee}}</label>
          <label><input type="radio" v-model="user.authorityName" value="ADMIN" /> {{translations.register.admin}}</label>
        </div>
      </div>

      <button type="submit" class="btn btn-primary">{{translations.register.confirmSubmit}}</button>
    </form>
    <button @click="goToUserPanel" class="btn btn-link">{{translations.register.singleBack}}</button>
    <p v-if="message" class="message">{{ message }}</p>
  </div>
  <Footer :translations="translations"></Footer>
</template>

<script>
import axios from "axios";
import Footer from '@/components/Footer.vue';

export default {
  props: ['translations'],

  data() {
    return {
      user: {
        name: "",
        surname: "",
        email: "",
        username: "",
        password: "",
        city: "",
        address: "",
        authorityName: "USER",
      },
      message: "",
      errors: {},
      errorMessages: {
        username: this.translations.register.usernameErr,
        password: this.translations.register.passwordErr,
        name: this.translations.register.nameErr,
        surname: this.translations.register.surnameErr,
        email: this.translations.register.emailErr,
        city: this.translations.register.cityErr,
        address: this.translations.register.addressErr
      }
    };
  },
  components: {
      Footer,
  },
  methods: {
    async submitForm() {
      try {
        const response = await axios.post("/v1/admin/user", this.user);
        if (response.status === 200) {
          this.message = this.translations.register.added;
          this.clearForm();
        }
      } catch (error) {
        if (error.response && error.response.status === 400) {
            this.errors = {};
            const validationErrors = error.response.data.errors;
            if (validationErrors === undefined) {
              this.message = error.response.data;
              return;
            }
            for (const validationError of validationErrors) {
              this.errors[validationError.field] = this.errorMessages[validationError.field];
            }
            this.message = "";
          }
        else if (error.response.status === 403) {
           this.$router.push("/login");
        }
        else {
            this.message = this.translations.error;
        }
      }
    },
    clearForm() {
      this.user.name = "";
      this.user.surname = "";
      this.user.email = "";
      this.user.username = "";
      this.user.password = "";
      this.user.city = "";
      this.user.address = "";
      this.user.authorityName = "USER";
      this.errors = {};
    },
    goToUserPanel() {
      this.$router.push("/userpanel");
    },
  },
};
</script>

<style scoped>
.registration-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.4);
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

.error-container {
  margin-top: 10px;
}

.error-item {
  color: #e82727;
  margin-top: 5px;
}

.btn-link {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  text-decoration: underline;
  margin-top: 10px;
}
</style>
