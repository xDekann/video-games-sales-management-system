<template>
  <div class="registration-container mt-5">
    <h2 class="register-title">{{translations.register.editUser}}</h2>
    <form @submit.prevent="submitForm" class="registration-form">
      <div class="form-group">
        <label for="username">{{translations.register.username}}</label>
        <input type="text" id="username" v-model="username" class="form-control" :class="{ 'is-invalid': errors.username }" />
        <div class="invalid-feedback" v-if="errors.username">{{ errors.username }}</div>
      </div>

      <div class="form-group">
        <label for="password">{{translations.register.password}}</label>
        <input type="password" id="password" v-model="password" class="form-control" :class="{ 'is-invalid': errors.password }" />
        <div class="invalid-feedback" v-if="errors.password">{{ errors.password }}</div>
      </div>

      <div class="form-group">
        <label for="name">{{translations.register.name}}</label>
        <input type="text" id="name" v-model="name" class="form-control" :class="{ 'is-invalid': errors.name }" />
        <div class="invalid-feedback" v-if="errors.name">{{ errors.name }}</div>
      </div>

      <div class="form-group">
        <label for="surname">{{translations.register.surname}}:</label>
        <input type="text" id="surname" v-model="surname" class="form-control" :class="{ 'is-invalid': errors.surname }" />
        <div class="invalid-feedback" v-if="errors.surname">{{ errors.surname }}</div>
      </div>

      <div class="form-group">
        <label for="email">{{translations.register.email}}</label>
        <input type="email" id="email" v-model="email" class="form-control" :class="{ 'is-invalid': errors.email }" />
        <div class="invalid-feedback" v-if="errors.email">{{ errors.email }}</div>
      </div>

      <div class="form-group">
        <label for="city">{{translations.register.city}}</label>
        <input type="text" id="city" v-model="city" class="form-control" :class="{ 'is-invalid': errors.city }" />
        <div class="invalid-feedback" v-if="errors.city">{{ errors.city }}</div>
      </div>

      <div class="form-group">
        <label for="address">{{translations.register.address}}</label>
        <textarea id="address" v-model="address" class="form-control" :class="{ 'is-invalid': errors.address }"></textarea>
        <div class="invalid-feedback" v-if="errors.address">{{ errors.address }}</div>
      </div>

      <div class="form-group">
        <label>Role:</label>
        <div>
          <label>
            <input type="radio" v-model="selectedRole" value="USER" /> {{translations.register.user}}
          </label>
          <label>
            <input type="radio" v-model="selectedRole" value="ADMIN" /> {{translations.register.admin}}
          </label>
          <label>
            <input type="radio" v-model="selectedRole" value="EMPLOYEE" /> {{translations.register.employee}}
          </label>
        </div>
      </div>

      <button type="submit" class="btn btn-primary">{{translations.register.confirmSubmit}}</button>
    </form>
    <button @click="goBack" class="btn btn-link">{{translations.register.singleBack}}</button>
    <p v-if="message" class="message">{{ message }}</p>
  </div>
  <Footer :translations="translations"></Footer>
</template>

<script>
import axios from "axios";
import Footer from '@/components/Footer.vue';

export default {
  props: ['id', 'translations'],
  data() {
    return {
      userId: "",
      username: "",
      password: "",
      name: "",
      surname: "",
      email: "",
      city: "",
      address: "",
      selectedRole: "",
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
  created() {
    this.userId = this.$route.params.id;
    this.fetchUserData();
  },
  methods: {
    fetchUserData() {
      axios
        .get(`/v1/admin/user`, {
          params: {
            id: this.userId,
          },
        })
        .then((response) => {
          const userData = response.data;
          this.username = userData.username;
          this.name = userData.name;
          this.surname = userData.surname;
          this.email = userData.email;
          this.city = userData.city;
          this.address = userData.address;
          this.selectedRole = userData.authorityName;
        })
        .catch((error) => {
          console.log(error)
          this.errors.fetchError = this.translations.error;
          return;
        });
    },
    submitForm() {
      const updatedUser = {
        id: this.userId,
        username: this.username,
        password: this.password,
        name: this.name,
        surname: this.surname,
        email: this.email,
        city: this.city,
        address: this.address,
        authorityName: this.selectedRole
      };

      axios
        .put(`/v1/admin/user`, updatedUser)
        .then(() => {
          this.message = this.translations.register.successEdit;
          this.errors = {};
        })
        .catch((error) => {
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
          if (error.response.status === 403) {
           this.$router.push("/login");
          }
          else {
            this.message = this.translations.error;
          }
        });
    },
    goBack() {
      this.$router.push('/userpanel');
    }
  }
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
