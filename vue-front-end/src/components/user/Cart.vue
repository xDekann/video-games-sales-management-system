<template>
    <div class="container mt-5">
      <!-- Upper side panel -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <!-- Logout Button (Top Right) -->
        <LogoutButton class="btn btn-danger"></LogoutButton>
        <!-- Back Button (Top Right) -->
        <button class="btn btn-secondary" @click="redirectToGames">Back</button>
      </div>
  
      <!-- Cart Items Table -->
      <table class="table table-bordered">
        <thead class="thead-dark">
          <tr>
            <th>Product</th>
            <th>Price (PLN)</th>
            <th>Amount</th>
            <th>Action</th> <!-- New column for action buttons -->
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in cartItems" :key="item.itemId">
            <td>{{ item.name }}</td>
            <td>{{ item.price }}</td>
            <td>{{ item.amount }}</td>
            <td>
              <!-- Button to delete the item -->
              <button @click="deleteCartItem(item.itemId)" class="btn btn-danger">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
  
      <!-- Checkout Container -->
      <div class="d-flex justify-content-between align-items-center mt-4">
        <!-- Total Cart Price -->
        <div class="font-weight-bold">
          Total Cart Price: {{ cartCount }} PLN
        </div>
  
        <!-- Checkout Button -->
        <button class="btn btn-success" @click="checkout">Check Out</button>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import LogoutButton from '@/components/LogoutButton.vue'; // Import LogoutButton component
  
  export default {
    components: {
      LogoutButton,
    },
    data() {
      return {
        cartItems: [], // Array to store cart items
        cartCount: 0,
      };
    },
    mounted() {
      // Fetch cart items from the API
      this.fetchCartItems();
      this.updatePrice();
    },
    methods: {
      fetchCartItems() {
        // Make a GET request to the /v1/user/cart API to fetch cart items
        axios
          .get('/v1/user/cart')
          .then((response) => {
            this.cartItems = response.data;
          })
          .catch((error) => {
            console.error('Error fetching cart items:', error);
          });
      },
      redirectToGames() {
        // Use Vue Router to navigate back to the "/games" route
        this.$router.push('/games');
      },
      checkout() {
        // Implement the checkout logic (e.g., sending a request to the server)
        // This is where you would handle the checkout process
        // For now, it's left as a placeholder
        alert('Checkout functionality will be implemented in the future.');
      },
      async updatePrice() {
        try {
            const response = await axios.get('/v1/user/price');
          if (response.status === 200) {
            this.cartCount = response.data; // Update cartCount with the response data
          } else {
            console.error('Failed to fetch user cart price:', response.statusText);
          }
        } catch (error) {
          console.error('An error occurred while fetching user cart price:', error);
        }
     },
      async deleteCartItem(itemId) {
        // Show a confirmation dialog to confirm the deletion
        const confirmDelete = confirm('Are you sure you want to delete this item from your cart?');

        if (confirmDelete) {
            try {
            // Send a DELETE request to the server with the itemId as a parameter
            const response = await axios.delete('/v1/user/cart', {
                params: {
                  itemId: itemId,
                },
            });

            if (response.status === 200) {
                // Item deleted successfully, refresh the cart items
                this.fetchCartItems(); // Await for the updated cart items
                this.updatePrice(); // Update the cart count
            } else {
                console.error('Failed to delete cart item:', response.statusText);
            }
            } catch (error) {
            console.error('An error occurred while deleting cart item:', error);
            }
        } else {
            // User canceled the deletion, do nothing or provide feedback to the user.
        }
    },
    },
  };
  </script>
  
  <style scoped>

  </style>
  
  