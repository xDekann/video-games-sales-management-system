<template>
  <div class="container mt-5">
    <h1>{{ translations.employee.game.title }}</h1>
    <form @submit.prevent="addGame" class="needs-validation" novalidate>
      <div class="mb-3">
        <label for="name" class="form-label">{{ translations.employee.game.name }}</label>
        <input type="text" id="name" v-model="gameData.name" class="form-control" required :class="{ 'is-invalid': errors.name }" />
        <div class="invalid-feedback" v-if="errors.name">{{ errors.name }}</div>
      </div>

      <div class="mb-3">
        <label for="price" class="form-label">{{ translations.employee.game.price }}</label>
        <input type="text" id="price" v-model="gameData.price" class="form-control" required :class="{ 'is-invalid': errors.price }" />
        <div class="invalid-feedback" v-if="errors.price">{{ errors.price }}</div>
      </div>

      <div class="mb-3">
        <label for="producer" class="form-label">{{ translations.employee.game.producer }}</label>
        <input type="text" id="producer" v-model="gameData.producer" class="form-control" required :class="{ 'is-invalid': errors.producer }" />
        <div class="invalid-feedback" v-if="errors.producer">{{ errors.producer }}</div>
      </div>

      <div class="mb-3">
        <label for="category" class="form-label">{{ translations.employee.game.category }}</label>
        <select id="category" v-model="gameData.category" class="form-select" required :class="{ 'is-invalid': errors.category }">
          <option value="">{{ translations.employee.game.selectCategory }}</option>
          <option v-for="category in gameCategories" :key="category.label" :value="category.value">{{ category.label }}</option>
        </select>
        <div class="invalid-feedback" v-if="errors.category"> {{ translations.employee.game.categoryErr }} </div>
      </div>

      <button type="submit" class="btn btn-primary">{{ translations.employee.game.addGame }}</button>
    </form>

    <div class="mt-3">
      <button @click="goToGamePanel" class="btn btn-secondary">{{ translations.employee.game.goToGamePanel }}</button>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
  <Footer :translations="translations"></Footer>
</template>
<script>
import { generateGameCategories } from "../gameCategories";
import axios from "axios";
import Footer from '@/components/Footer.vue';

export default {
  props: ['translations'],

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
      errorMessages: {
        name: this.translations.employee.game.nameErr,
        category: this.translations.employee.game.categoryErr,
        price: this.translations.employee.game.priceErr,
        producer: this.translations.employee.game.producerErr,
      }
    };
  },
  components: {
      Footer,
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
          if (validationErrors === undefined) {
              if (error.response.data.message != null) {
                this.message = this.translations.employee.game.useInstead;
                return;
              }
              this.message = error.response.data;
              return;
            }
          for (const validationError of validationErrors) {
            this.errors[validationError.field] = this.errorMessages[validationError.field];
          }
        }
        if (error.response.status === 403) {
            this.$router.push("/login");
        } 
        else if (error.response.status > 500) {
          this.message = this.translations.error;
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
.form-control.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  color: #dc3545;
}

.message {
  color: #dc3545;
}
</style>
