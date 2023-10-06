<template>
  <div class="container mt-5">
    <!-- Upper side panel -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <LogoutButton class="btn btn-danger" :translations="translations"></LogoutButton>
      <button class="btn btn-secondary" @click="redirectToGames">{{ translations.user.cart.back }}</button>
    </div>

    <!-- Cart Items Table -->
    <div class="table-responsive">
      <table class="table table-bordered">
        <thead class="thead-dark">
          <tr>
            <th>{{ translations.user.cart.product }}</th>
            <th>{{ translations.user.cart.price }} (PLN)</th>
            <th>{{ translations.user.cart.amount }}</th>
            <th>{{ translations.user.cart.left }}</th>
            <th>{{ translations.user.cart.action }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in cartItems" :key="item.itemId">
            <td>{{ item.name }}</td>
            <td>{{ item.price }}</td>
            <td>{{ item.amount }}</td>
            <td>
              <p :class="{'unavailable-text': item.amountLeft <= 0}">
                {{ item.amountLeft <= 0 ?  this.translations.user.cart.unavailable : item.amountLeft }}
              </p>
            </td>
            <td>
              <button @click="deleteCartItem(item.itemId)" class="btn btn-danger">{{ translations.user.cart.delete }}</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Checkout Container -->
    <div class="d-flex justify-content-between align-items-center mt-4">
      <div class="font-weight-bold">
        {{ translations.user.cart.totalCartPrice }} {{ cartCount }} PLN
      </div>
      <button class="btn btn-success" @click="checkout" v-if="cartCount != 0">{{ translations.user.cart.checkOut }}</button>
    </div>
  </div>
  <Footer :translations="translations"></Footer>
</template>
  
<script>
import axios from 'axios';
import LogoutButton from '@/components/LogoutButton.vue';
import Footer from '@/components/Footer.vue';
import 'sweetalert2/dist/sweetalert2.min.css';

export default {
  props: ["translations"],

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
          title: this.translations.user.cart.cancelledTitle,
          text: this.translations.user.cart.cancelledText,
          icon: 'error',
          confirmButtonText: this.translations.user.cart.confirmCancelButtonText,
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
        title: this.translations.user.cart.checkTitle,
        text: this.translations.user.cart.checkText,
        icon: 'info',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: this.translations.user.cart.confirmButtonText,
        cancelButtonText: this.translations.user.cart.cancelButtonText
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
        title: this.translations.user.cart.deleteTitle,
        text: this.translations.user.cart.deleteText,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: this.translations.user.cart.deleteConfirmButtonText,
        cancelButtonText: this.translations.user.cart.deleteCancelButtonText
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
  color: red;
}
</style>
  
  