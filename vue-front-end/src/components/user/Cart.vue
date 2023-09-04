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
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in cartItems" :key="item.itemId">
            <td>{{ item.name }}</td>
            <td>{{ item.price }}</td>
            <td>{{ item.amount }}</td>
            <td>
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
  import LogoutButton from '@/components/LogoutButton.vue';
  
  export default {
    components: {
      LogoutButton,
    },
    data() {
      return {
        cartItems: [],
        cartCount: 0,
      };
    },
    mounted() {
      this.fetchCartItems();
      this.updatePrice();
    },
    methods: {
      fetchCartItems() {
        axios
          .get('/v1/user/cart')
          .then((response) => {
            this.cartItems = response.data;
          })
          .catch((error) => {
            return error;
          });
      },
      redirectToGames() {
        this.$router.push('/games');
      },
      checkout() {
        const confirmCheckout = confirm('Are you sure you want to proceed with the checkout?');
        if (confirmCheckout) {
          this.$router.push("/checkout");
        }
      },
      async updatePrice() {
        try {
            const response = await axios.get('/v1/user/price');
          if (response.status === 200) {
            this.cartCount = response.data;
          } else {
            return;
          }
        } catch (error) {
          return error;
        }
     },
      async deleteCartItem(itemId) {
        const confirmDelete = confirm('Are you sure you want to delete this item from your cart?');
        if (confirmDelete) {
            try {
              const response = await axios.delete('/v1/user/cart', {
                  params: {
                    itemId: itemId,
                  },
              });
              if (response.status === 200) {
                  this.fetchCartItems();
                  this.updatePrice();
              } else {
                  return;
              }
            } catch (error) {
              return error;
            }
        } else {
            return;
        }
    },
    },
  };
  </script>
  
  