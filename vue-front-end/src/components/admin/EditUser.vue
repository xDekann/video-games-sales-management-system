<template>
    <div class="edit-user">
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

        <label>Role:</label>
        <div>
            <label>
            <input type="radio" v-model="selectedRole" value="USER" /> User
            </label>
            <label>
            <input type="radio" v-model="selectedRole" value="ADMIN" /> Admin
            </label>
            <label>
            <input type="radio" v-model="selectedRole" value="EMPLOYEE" /> Employee
            </label>
        </div>
  
        <button type="submit">Update</button>
      </form>
      <button @click="goBack">Back</button>
      <p v-if="message">{{ message }}</p>
      <div v-if="errors.length > 0">
        <ul>
          <li v-for="error in errors" :key="error.id">{{ error.defaultMessage }}</li>
        </ul>
      </div>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    props: ['id'],
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
        message: "",
        selectedRole: "",
        errors: []
      };
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
            const userData = response.data; // Assuming the API response is an object containing user data
            this.username = userData.username;
            this.name = userData.name;
            this.surname = userData.surname;
            this.email = userData.email;
            this.city = userData.city;
            this.address = userData.address;
            this.selectedRole = userData.authorityName;
          })
          .catch((error) => {
            console.error("Error fetching user data:", error);
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
                authorityName: this.selectedRole // Make sure to include the role
            };

            axios
                .put(`/v1/admin/user`, updatedUser)
                .then(() => {
                    this.message = "Successfully updated user!";
                })
                .catch((error) => {
                    this.message = "An error has occured";
                    console.error("Error updating user:", error);
                });
            },
        goBack() {
            // Use the router to navigate back to /userpanel
            this.$router.push('/userpanel');
        }
    }
  };
  </script>