<template>
  <div class="container mt-5">
    <h1>Add New Game</h1>
    <form @submit.prevent="addGame" class="needs-validation" novalidate>
      <div class="mb-3">
        <label for="name" class="form-label">Name:</label>
        <input type="text" id="name" v-model="gameData.name" class="form-control" required :class="{ 'is-invalid': errors.name }" />
        <div class="invalid-feedback" v-if="errors.name">{{ errors.name[0] }}</div>
      </div>

      <div class="mb-3">
        <label for="price" class="form-label">Price:</label>
        <input type="text" id="price" v-model="gameData.price" class="form-control" required :class="{ 'is-invalid': errors.price }" />
        <div class="invalid-feedback" v-if="errors.price">{{ errors.price[0] }}</div>
      </div>

      <div class="mb-3">
        <label for="producer" class="form-label">Producer:</label>
        <input type="text" id="producer" v-model="gameData.producer" class="form-control" required :class="{ 'is-invalid': errors.producer }" />
        <div class="invalid-feedback" v-if="errors.producer">{{ errors.producer[0] }}</div>
      </div>

      <div class="mb-3">
        <label for="category" class="form-label">Category:</label>
        <select id="category" v-model="gameData.category" class="form-select" required :class="{ 'is-invalid': errors.category }">
          <option value="">Select Category</option>
          <option v-for="category in gameCategories" :key="category.label" :value="category.value">{{ category.label }}</option>
        </select>
        <div class="invalid-feedback" v-if="errors.category"> Please select a category! </div>
      </div>

      <button type="submit" class="btn btn-primary">Add Game</button>
    </form>

    <div class="mt-3">
      <button @click="goToGamePanel" class="btn btn-secondary">Go to Game Panel</button>
    </div>

    <p v-if="message" class="mt-3">{{ message }}</p>
  </div>
</template>

<script>
import { generateGameCategories } from "../gameCategories";
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
      errors: {},
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
          this.errors = {};

          const validationErrors = error.response.data.errors;
          for (const validationError of validationErrors) {
            this.errors[validationError.field] = [validationError.defaultMessage];
          }
        } else {
          this.message = "An error has occurred";
        }
      }
    },
    goToGamePanel() {
      this.$router.push("/gamepanel");
    },
  },
};
</script>

<style scoped>
/* Style to highlight invalid input fields */
.form-control.is-invalid {
  border-color: #dc3545;
}

/* Style for error messages */
.invalid-feedback {
  color: #dc3545;
}
</style>
