<template>
    <div class="checkout-container mt-5">
      <!-- User Details -->
      <div class="user-details">
        <h3>User Details</h3>
        <ul class="list-unstyled">
          <li><strong>Name:</strong> {{ userDetails.name }}</li>
          <li><strong>Surname:</strong> {{ userDetails.surname }}</li>
          <li><strong>Email:</strong> {{ userDetails.email }}</li>
          <li><strong>City:</strong> {{ userDetails.city }}</li>
          <li><strong>Address:</strong> {{ userDetails.address }}</li>
        </ul>
      </div>
  
      <!-- Cart Items -->
      <div class="cart-items">
        <h3>Cart Items</h3>
        <ul class="list-group">
          <li class="list-group-item" v-for="item in cartItems" :key="item.itemId">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <strong>{{ item.name }}</strong><br>
                Price (PLN): {{ item.price }}<br>
                Amount: {{ item.amount }}
              </div>
            </div>
          </li>
        </ul>
        <div><strong>Total Cart Price:</strong> {{ totalCartAmount }} PLN</div>
      </div>
  
      <!-- Payment Methods -->
      <div class="payment-methods mt-3">
        <h3>Payment Method</h3>
        <div class="d-flex">
          <label v-for="method in paymentMethods" :key="method" class="mr-3">
            <input type="radio" v-model="selectedPaymentMethod" :value="method" />
            <img v-if="method === 'Card'" src="visa-and-mastercard.png" alt="Card logo" class="payment-logo" />
            <img v-if="method === 'Paypal'" src="paypal.png" alt="Paypal Logo" class="payment-logo" />
          </label>
        </div>
      </div>
  
      <!-- Delivery Methods -->
      <div class="delivery-methods mt-2">
        <h3>Delivery Method</h3>
        <div class="d-flex">
          <label v-for="method in deliveryMethods" :key="method" class="mr-3">
            <input type="radio" v-model="selectedDeliveryMethod" :value="method" />
            <img v-if="method === 'Courier'" src="courier.jpg" alt="Courier Logo" class="delivery-logo" />
            <img v-if="method === 'Self Pickup'" src="selfpickup.png" alt="Self Pickup Logo" class="delivery-logo" />
            <img v-if="method === 'Paczkomat'" src="inpost.png" alt="Paczkomat Logo" class="delivery-logo" />
            <span v-if="method === 'Self Pickup'" class="method-name">{{ method }}</span>
          </label>
        </div>
      </div>
  
      <!-- Buttons -->
      <div class="buttons mt-4">
        <button @click="goBackToCart" class="btn btn-secondary">Back to Cart</button>
        <button @click="payNow" class="btn btn-success">Pay Now</button>
      </div>
    </div>
    <Footer></Footer>
  </template>
  
  <script>
  import axios from "axios";
  import Footer from '@/components/Footer.vue';
  import 'sweetalert2/dist/sweetalert2.min.css';
  
  export default {
    data() {
      return {
        userDetails: {},
        cartItems: [],
        totalCartAmount: 0,
        paymentMethods: ["Card", "Paypal"],
        selectedPaymentMethod: "Card",
        deliveryMethods: ["Courier", "Self Pickup", "Paczkomat"],
        selectedDeliveryMethod: "Courier",
      };
    },
    components: {
      Footer,
   },
    mounted() {
      this.fetchTotalCartAmount();
      this.fetchUserDetails();
      this.fetchCartItems();
    },
    methods: {
      fetchUserDetails() {
        axios
          .get("/v1/user")
          .then((response) => {
            this.userDetails = response.data;
          })
          .catch((error) => {
            if (error.response.status === 403) {
              this.$router.push("/login");
            }
            return error;
          });
      },
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
      async fetchTotalCartAmount() {
        try {
            const response = await axios.get('/v1/user/price');
          if (response.status === 200) {
            this.totalCartAmount = response.data;
            if (this.totalCartAmount == 0) {
              this.$router.push("/games");
            }
          } else {
            return;
          }
        } catch (error) {
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          return error;
        }
      },
      goBackToCart() {
        this.$router.push("/cart");
      },
      async payNow() {
        try {
          const paymentData = {
            selectedPaymentMethod: this.selectedPaymentMethod,
            selectedDeliveryMethod: this.selectedDeliveryMethod,
          };

          const response = await axios.get('/v1/user/checkout', {
            params: paymentData,
          });
          if (response.status === 200 && response.data) {
            window.location.href = response.data;
          }
          if (response.status === 204) {
            await this.$swal.fire({
              title: 'Error',
              text: 'Some of the picked items are no longer available.',
              icon: 'error',
              confirmButtonText: 'OK',
            });
          } else {
            this.$router.push('/cart');
          }
        } catch (error) {
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          await this.$swal.fire({
              title: 'Error',
              text: 'An error has occurred while processing your payment',
              icon: 'error',
              confirmButtonText: 'OK',
            });
          this.$router.push('/cart');
        }
      },
    },
  };
  </script>
  
  <style scoped>
  .checkout-container {
    max-width: 600px;
    margin: 0 auto;
    margin-bottom: 50px;
  }
  
  .user-details ul,
  .cart-items ul {
    list-style: none;
    padding: 0;
  }
  
  .payment-methods label,
  .delivery-methods label {
    display: flex;
    align-items: center;
  }
  
  .payment-logo,
  .delivery-logo {
    width: 40px;
    margin-right: 10px;
    margin-left: 5px;
  }
  
  .buttons button {
    margin-right: 10px;
  }

  .method-name {
    margin-right: 10px;
  }
  </style>
  