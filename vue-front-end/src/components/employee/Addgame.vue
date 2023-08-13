<template>
  <div>
    <h1>Add New Game</h1>
    <form @submit.prevent="addGame">
      <label for="name">Name:</label>
      <input type="text" id="name" v-model="gameData.name" />

      <label for="price">Price:</label>
      <input type="text" id="price" v-model="gameData.price" />

      <label for="producer">Producer:</label>
      <input type="text" id="producer" v-model="gameData.producer" />

      <label for="category">Category:</label>
      <select id="category" v-model="gameData.category" >
        <option value="">Select Category</option>
        <option
          v-for="category in gameCategories"
          :key="category.label"
          :value="category.value"
        >
          {{ category.label }}
        </option>
      </select>

      <button type="submit">Add Game</button>
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
import { generateGameCategories } from "./gameCategories";
import axios from "axios";

export default {
  data() {
    return {
      gameData: {
        name: "",
        price: null,
        producer: "",
        category: "",
      },
      gameCategories: generateGameCategories(),
      message: "",
      errors: [],
    };
  },
  methods: {
    async addGame() {
      try {
        const response = await axios.post("/v1/employee/game", {
          name: this.gameData.name,
          price: this.gameData.price,
          category: this.gameData.category,
          producer: this.gameData.producer,
        });

        this.message = response.statusText;

        if (response.status === 200) {
          this.$router.push("/gamepanel");
        }
      } catch (error) {
        if (error.response.status === 400) { 
          this.errors = error.response.data.errors;
        }
        this.message = "An error has occured";
      }
    },
  },
};
</script>