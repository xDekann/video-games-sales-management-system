<template>
    <div>
      <h2>Add User</h2>
      <form @submit.prevent="submitForm">
        <label for="name">Name:</label>
        <input v-model="user.name" type="text" id="name">
  
        <label for="surname">Surname:</label>
        <input v-model="user.surname" type="text" id="surname">
  
        <label for="email">Email:</label>
        <input v-model="user.email" type="email" id="email">
  
        <label for="username">Username:</label>
        <input v-model="user.username" type="text" id="username">
  
        <label for="password">Password:</label>
        <input v-model="user.password" type="password" id="password">
  
        <label for="city">City:</label>
        <input v-model="user.city" type="text" id="city">
  
        <label for="address">Address:</label>
        <input v-model="user.address" type="text" id="address">
  
        <label>Authority:</label>
        <div>
          <label> <input type="radio" v-model="user.authorityName" value="USER"> User </label>
          <label> <input type="radio" v-model="user.authorityName" value="EMPLOYEE"> Employee </label>
          <label> <input type="radio" v-model="user.authorityName" value="ADMIN"> Admin </label>
        </div>
  
        <div>
          <button type="submit">Submit</button>
          <button @click="goToUserPanel">Back</button>
        </div>
      </form>
      <div v-if="message">{{ message }}</div>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
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
      };
    },
    methods: {
      async submitForm() {
        try {
          const response = await axios.post("/v1/admin/user", this.user);
          if (response.status === 200) {
            this.message = "User added successfully!";
            this.clearForm();
          }
        } catch (error) {
            if (error.response.status === 400) {
              this.errors = error.response.data.errors;
            }
          console.error("Error adding user:", error);
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
        this.user.authorityName = "ROLE_USER";
      },
      goToUserPanel() {
        this.$router.push("/userpanel");
      },
    },
  };
  </script>
  
  <style scoped>
  /* Your styles for the component */
  </style>