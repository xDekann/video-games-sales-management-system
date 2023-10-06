<template>
  <div class="custom-modal-overlay">
    <div class="custom-modal">
      <h2>{{ translations.user.addToCart.add }}</h2>
      <p>{{ game.name }}</p>
      <div class="input-container">
        <label for="quantity" class="quantity-label">{{ translations.user.addToCart.quantity }}</label>
        <input type="number" id="quantity" v-model="quantity" min="1" />
      </div>
      <div class="button-container">
        <button @click="addToCart" class="btn btn-success">{{ translations.user.addToCart.confirmButtonText }}</button>
        <button @click="closeModal" class="btn btn-danger">{{ translations.user.addToCart.cancelButtonText }}</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    game: {
      type: Object,
      required: true,
    },
    translations: {
      type: Object,
      required: true,
    }
  },
  data() {
    return {
      quantity: 1,
    };
  },
  methods: {
    addToCart() {
      this.$emit("add-to-cart", this.quantity);
    },
    closeModal() {
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
.custom-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.custom-modal {
  background-color: white;
  color: black;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.5);
  text-align: center;
  z-index: 10000;
  max-width: 300px;
  width: 100%;
}

.input-container {
  margin-bottom: 20px;
}

.button-container {
  display: flex;
  justify-content: center;
}

.button-container button {
  margin: 0 10px;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  min-width: 100px;
}

.button-container button:first-child {
  background-color: #28a745;
  color: white;
}

.button-container button:last-child {
  background-color: #dc3545;
  color: white;
}

.quantity-label {
  margin-right: 5px;
}
</style>
