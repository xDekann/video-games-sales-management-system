<template>
  <div class="container mt-5">
    <!-- Upper side panel -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <!-- Logout Button (Top Right) -->
      <LogoutButton class="btn btn-danger"></LogoutButton>
      <!-- Back Button (Top Right) -->
      <button class="btn btn-secondary" @click="redirectToGames">Back To Games</button>
    </div>

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

    <!-- Transaction history table -->
    <table class="table mt-4">
      <thead>
        <tr>
          <th>ID</th>
          <th>Value</th>
          <th>Status</th>
          <th>Date</th>
          <th>Delivery Method</th>
          <th>Options</th> <!-- New column for "Get Invoice" button -->
        </tr>
      </thead>
      <tbody>
        <!-- Loop through PurchaseDto items -->
        <tr v-for="(purchase, index) in purchases" :key="purchase.id" :class="{'clickable-row': clickedRowIndex === index}">
          <td>
            {{ purchase.id }}
            <!-- Display the item list if the row is clicked -->
            <ul v-if="clickedRowIndex === index" class="item-list">
              <li v-for="item in purchase.items" :key="item.name">
                {{ item.name }} - {{ item.price }} PLN - Amount: {{ item.amount }}
              </li>
            </ul>
          </td>
          <td>{{ purchase.value }} PLN</td>
          <td>{{ purchase.status }}</td>
          <td>{{ purchase.date }}</td>
          <td>{{ purchase.deliveryMethod }}</td>
          <td>
            <button @click="getInvoice(purchase)" class="btn btn-secondary invoice-button">Get Invoice</button>
            <button @click="toggleRow(index)" class="btn btn-secondary">Show Details</button>
          </td>
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
  <Footer></Footer>
</template>

<script>
import axios from 'axios';
import LogoutButton from '@/components/LogoutButton.vue';
import Footer from '@/components/Footer.vue';
import 'sweetalert2/dist/sweetalert2.min.css';

export default {
  mounted() {
    this.fetchPurchases(); // Fetch initial data when the component is mounted
    this.fetchUserDetails();
    console.log(this.purchases);
  },
  components: {
    LogoutButton,
    Footer,
  },
  data() {
    return {
      userDetails: {},
      purchases: [], // Store PurchaseDto items here
      currentPage: 0,
      totalPages: 0,
      size: 2, // Number of items per page
      clickedRowIndex: -1, // Initialize as -1 to indicate no row is clicked
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
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          return error;
        });
    },
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
    toggleRow(index) {
      if (this.clickedRowIndex === index) {
        // If the same row is clicked again, hide it
        this.clickedRowIndex = -1;
      } else {
        // Otherwise, show the clicked row
        this.clickedRowIndex = index;
      }
    },
    async getInvoice(purchase) {
      axios
        .get("/transactionservice/v1/purchase/pdf", {
          params: {
            transactionId: purchase.id
          },
          responseType: 'blob'
        })
        .then((response) => {
          if (response.status === 204) {
            this.$swal.fire({
              title: 'Failure',
              text: 'Invalid transaction.',
              icon: 'error',
              confirmButtonText: 'OK',
            });
          } else {
            const blob = new Blob([response.data], { type: 'application/pdf' });
            const url = window.URL.createObjectURL(blob);

            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', purchase.id + '.pdf');
            document.body.appendChild(link);
            link.click();
            window.URL.revokeObjectURL(url);
          }
        })
        .catch((error) => {
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          console.error('Error fetching PDF:', error);
        });
    },
    redirectToGames() {
      this.$router.push('/games');
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

<style scoped>
/* Style for the hidden item list */
.item-list {
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-top: 1px solid #ccc; /* Add a 2px solid top border */
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.item-list li {
  margin: 5px 0;
}

/* Style for the clickable row */
.clickable-row {
  cursor: pointer;
  transition: background-color 0.2s;
}

.clickable-row:hover {
  background-color: #f5f5f5;
}

.disabled-button {
  background-color: #f0f0f0; /* Gray background color */
  color: #777; /* Gray text color */
  cursor: not-allowed; /* Change cursor to not-allowed */
}

.invoice-button {
  margin-right: 3px;
}
</style>
