<template>
  <div class="prompt-overlay">
    <div class="prompt">
      <label for="quantity">Enter quantity:</label>
      <input type="number" id="quantity" v-model="quantity" class="form-control" />
      <div class="button-container mt-3">
        <button @click="confirm" class="btn btn-primary">Confirm</button>
        <button @click="cancel" class="btn btn-secondary">Cancel</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  props: ["gameId", "action"],
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
          console.error("Error has occurred during the update:", error);
        });
      this.$emit("close");
    },
    cancel() {
      // Hide the prompt
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
/* Style for the prompt overlay */
.prompt-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent dark overlay */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001; /* Adjust the z-index to ensure it's above other content */
}

/* Style for the prompt dialog */
.prompt {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  text-align: center;
  z-index: 1002; /* Ensure the dialog is above the overlay */
}

/* Style for the buttons container */
.button-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px; /* Add spacing between buttons */
}
</style>
