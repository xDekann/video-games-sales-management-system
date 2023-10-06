<template>
  <div class="prompt-overlay">
    <div class="prompt">
      <label for="quantity">{{translations.employee.stocking.quantity}}</label>
      <input type="number" id="quantity" v-model="quantity" class="form-control" />
      <div class="button-container mt-3">
        <button @click="confirm" class="btn btn-primary">{{translations.employee.stocking.confirmButtonText}}</button>
        <button @click="cancel" class="btn btn-secondary">{{translations.employee.stocking.cancelButtonText}}</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  props: ["gameId", "action", "translations"],
  data() {
    return {
      quantity: 0,
    };
  },
  methods: {
    confirm() {
      const endpoint = this.action
        ? "/v1/employee/game/replenish"
        : "/v1/employee/game/deplenish";
      axios
        .put(endpoint, null, {
          params: {
            id: this.gameId,
            amount: this.quantity,
          },
        })
        .catch((error) => {
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          return error;
        });
      this.$emit("close");
    },
    cancel() {
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
.prompt-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
}

.prompt {
  background-color: white;
  color: black;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  text-align: center;
  z-index: 1002;
}

.button-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}
</style>
