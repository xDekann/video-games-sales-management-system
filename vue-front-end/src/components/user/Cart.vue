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
    <div class="table-responsive">
      <table class="table table-bordered">
        <thead class="thead-dark">
          <tr>
            <th>Product</th>
            <th>Price (PLN)</th>
            <th>Amount</th>
            <th>Amount left</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <!-- When the game amount <=0, display "unavailable" instead of 0 and make it darker-->
          <tr v-for="item in cartItems" :key="item.itemId">
            <td>{{ item.name }}</td>
            <td>{{ item.price }}</td>
            <td>{{ item.amount }}</td>
            <td>
              <p :class="{'unavailable-text': item.amountLeft <= 0}">
                {{ item.amountLeft <= 0 ? 'Unavailable' : item.amountLeft }}
              </p>
            </td>
            <td>
              <button @click="deleteCartItem(item.itemId)" class="btn btn-danger">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Checkout Container -->
    <div class="d-flex justify-content-between align-items-center mt-4">
      <!-- Total Cart Price -->
      <div class="font-weight-bold">
        Total Cart Price: {{ cartCount }} PLN
      </div>

      <!-- Checkout Button -->
      <button class="btn btn-success" @click="checkout" v-if="cartCount != 0">Check Out</button>
    </div>
  </div>
  <Footer></Footer>
</template>
  
<script>
import axios from 'axios';
import LogoutButton from '@/components/LogoutButton.vue';
import Footer from '@/components/Footer.vue';
import 'sweetalert2/dist/sweetalert2.min.css';

export default {
  components: {
    LogoutButton,
    Footer,
  },
  data() {
    return {
      cartItems: [],
      cartCount: 0,
    };
  },
  created() {
    const transaction = this.$route.query.transaction;
    if (transaction === "cancelled") {
      this.$swal.fire({
          title: 'Error',
          text: 'Transaction has ben cancelled',
          icon: 'error',
          confirmButtonText: 'OK',
      });
    }
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
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          return error;
        });
    },
    redirectToGames() {
      this.$router.push('/games');
    },
    async checkout() {
      const confirmResult = await this.$swal({
        title: 'Are you sure?',
        text: 'Are you sure you want to proceed with the checkout?',
        icon: 'info',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes',
      });
      if (confirmResult.isConfirmed) {
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
        if (error.response.status === 403) {
          this.$router.push("/login");
        }
        return error;
      }
    },
    async deleteCartItem(itemId) {
      const confirmDelete = await this.$swal({
        title: 'Are you sure?',
        text: 'Are you sure you want to delete product from cart?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes',
      });
      if (confirmDelete.isConfirmed) {
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
            if (error.response.status === 403) {
              this.$router.push("/login");
            }
            return error;
          }
      } else {
          return;
      }
  },
  },
};
</script>
<style scoped>
.unavailable-text {
  color: red; /* Text color for "Unavailable" text */
}
</style>
  
  