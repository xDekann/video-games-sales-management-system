<template>
    <div class="modal-overlay">
      <div class="modal">
        <h2>Add to Cart</h2>
        <p>{{ game.name }}</p>
        <label for="quantity">Quantity:</label>
        <input type="number" id="quantity" v-model="quantity" min="1" />
        <div class="modal-buttons">
          <button @click="addToCart">Add</button>
          <button @click="closeModal">Cancel</button>
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
    },
    data() {
      return {
        quantity: 1, // Default quantity
      };
    },
    methods: {
      addToCart() {
        // Validate quantity if needed
  
        // Emit an event to add the game to the cart
        this.$emit("add-to-cart", this.quantity);
      },
      closeModal() {
        // Emit an event to close the modal
        this.$emit("close");
      },
    },
  };
  </script>
  
  <style scoped>
  /* Style for the modal overlay */
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent background */
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999; /* Ensure it appears above other content */
  }
  
  /* Style for the modal dialog */
  .modal {
    background-color: white;
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.5);
    text-align: center;
  }
  
  /* Style for the buttons */
  .modal-buttons {
    margin-top: 10px;
  }
  
  .modal-buttons button {
    margin: 0 10px;
    padding: 8px 16px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .modal-buttons button:first-child {
    background-color: #28a745; /* Green color for "Add" button */
    color: white;
  }
  
  .modal-buttons button:last-child {
    background-color: #dc3545; /* Red color for "Cancel" button */
    color: white;
  }
  </style>
  