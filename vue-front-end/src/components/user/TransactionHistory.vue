<template>
    <div class="transaction-history">
    <LogoutButton class="btn btn-danger"></LogoutButton>
      <!-- Transaction history table -->
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Value</th>
            <th>Status</th>
            <th>Date</th>
            <th>Delivery Method</th>
          </tr>
        </thead>
        <tbody>
          <!-- Loop through PurchaseDto items -->
          <tr v-for="purchase in purchases" :key="purchase.id">
            <td>{{ purchase.id }}</td>
            <td>{{ purchase.value }} PLN</td>
            <td>{{ purchase.status }}</td>
            <td>{{ purchase.date }}</td>
            <td>{{ purchase.deliveryMethod }}</td>
          </tr>
        </tbody>
      </table>
  
      <!-- Pagination controls -->
      <div class="d-flex justify-content-center align-items-center mt-4">
      <button @click="previousPage" :disabled="currentPage === 0" class="btn btn-secondary me-2">Previous</button>
      <template v-if="purchases.length > 0">
        <span class="page-number">Page {{ currentPage + 1 }} of {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages - 1" class="btn btn-secondary ms-2">Next</button>
      </template>
      <template v-else>
        <span class="page-number">Page {{ currentPage + 1 }} of 1</span>
        <button @click="nextPage" :disabled="true" class="btn btn-secondary ms-2">Next</button>
      </template>
      </div>
    </div>
  </template>
  <script>
  import axios from 'axios';
  import LogoutButton from '@/components/LogoutButton.vue';

  
  export default {
    mounted() {
      this.fetchPurchases(); // Fetch initial data when the component is mounted
      console.log(this.purchases);
    },
    components: {
        LogoutButton
    },
    data() {
      return {
        purchases: [], // Store PurchaseDto items here
        currentPage: 0,
        totalPages: 0,
        size: 2, // Number of items per page
      };
    },
    methods: {
      fetchPurchases() {
        axios
        .get("/transactionservice/v1/purchase", {
          params: {
            page: this.currentPage,
            size: this.size,
          },
        })
        .then((response) => {
            if (response.status === 200) {
                this.purchases = response.data.purchases;
                this.totalPages = response.data.totalPages;
            }
        })
        .catch((error) => {
          return error;
        });
      },
      async previousPage() {
        if (this.currentPage > 0) {
          await new Promise(resolve => setTimeout(resolve, 300));
          this.currentPage--;
          this.fetchPurchases();
        }
      },
      async nextPage() {
        if (this.currentPage < this.totalPages) {
        await new Promise(resolve => setTimeout(resolve, 300));
          this.currentPage++;
          this.fetchPurchases();
        }
      },
    },
  };
  </script>