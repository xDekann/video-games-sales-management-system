<template>
  <div class="container mt-5">
    <!-- Upper side panel -->
    <div class="button-container">
        <LogoutButton class="btn btn-danger" :translations="translations"></LogoutButton>
        <button class="btn btn-secondary" @click="redirectToGames">{{ translations.user.history.backToGames }}</button>

    </div>

    <!-- User Details -->
    <div>
      <h3>{{ translations.user.history.userDetails }}</h3>
      <ul class="list-unstyled">
        <li><strong>{{ translations.user.history.name }}</strong> {{ userDetails.name }}</li>
        <li><strong>{{ translations.user.history.surname }}</strong> {{ userDetails.surname }}</li>
        <li><strong>{{ translations.user.history.email }}</strong> {{ userDetails.email }}</li>
        <li><strong>{{ translations.user.history.city }}</strong> {{ userDetails.city }}</li>
        <li><strong>{{ translations.user.history.address }}</strong> {{ userDetails.address }}</li>
      </ul>
    </div>

    <!-- Transaction history table -->
    <div class="table-responsive mb-4">
      <table class="table mt-4">
        <thead>
          <tr>
            <th>{{ translations.user.history.id }}</th>
            <th>{{ translations.user.history.value }}</th>
            <th>{{ translations.user.history.status }}</th>
            <th>{{ translations.user.history.date }}</th>
            <th>{{ translations.user.history.deliveryMethod }}</th>
            <th>{{ translations.user.history.options }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(purchase, index) in purchases" :key="purchase.id" :class="{'clickable-row': clickedRowIndex === index}">
            <td>
              {{ purchase.id }}
              <ul v-if="clickedRowIndex === index" class="item-list">
                <li v-for="item in purchase.items" :key="item.name">
                  {{ item.name }} - {{ item.price }} PLN - {{ translations.user.history.amount }} {{ item.amount }}
                </li>
              </ul>
            </td>
            <td>{{ purchase.value }} PLN</td>
            <td>{{ purchase.status }}</td>
            <td>{{ purchase.date }}</td>
            <td>{{ purchase.deliveryMethod }}</td>
            <td class="button-cell">
              <div class="button-group">
                <button @click="getInvoice(purchase)" class="btn btn-sm btn-secondary invoice-button">{{ translations.user.history.invoice }}</button>
                <button @click="toggleRow(index)" class="btn btn-sm btn-secondary">{{ translations.user.history.showDetails }}</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination controls -->
    <div class="pagination-container">
      <button @click="previousPage" :disabled="currentPage === 0" class="btn btn-secondary me-2">{{ translations.user.history.previous }}</button>
      <template v-if="purchases.length > 0">
        <span class="page-number">{{ translations.user.history.page }} {{ currentPage + 1 }} {{ translations.user.history.of }} {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages - 1" class="btn btn-secondary ms-2">{{ translations.user.history.next }}</button>
      </template>
      <template v-else>
        <span class="page-number">{{ translations.user.history.page }} {{ currentPage + 1 }} {{ translations.user.history.of }} 1</span>
        <button @click="nextPage" :disabled="true" class="btn btn-secondary ms-2">{{ translations.user.history.next }}</button>
      </template>
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

  mounted() {
    this.fetchPurchases();
    this.fetchUserDetails();
  },
  created() {
    const transaction = this.$route.query.transaction;
    if (transaction === "success") {
      this.$swal.fire({
          title: this.translations.user.history.stitle,
          text: this.translations.user.history.stitle,
          icon: 'success',
          confirmButtonText: this.translations.user.history.sconfirmButtonText,
      });
    }
  },
  components: {
    LogoutButton,
    Footer,
  },
  data() {
    return {
      userDetails: {},
      purchases: [],
      currentPage: 0,
      totalPages: 0,
      size: 2,
      clickedRowIndex: -1,
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
        this.clickedRowIndex = -1;
      } else {
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
              title: this.translations.user.history.nstitle,
              text: this.translations.user.history.nstext,
              icon: 'error',
              confirmButtonText: this.translations.user.history.nsconfirmButtonText,
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
.container {
  margin-bottom: 50px;
}

.button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.item-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
  font-size: 12px;
}

.item-list li {
  margin: 5px 0;
}

.clickable-row {
  cursor: pointer;
}

.disabled-button {
  background-color: #f0f0f0;
  color: #777;
  cursor: not-allowed;
}

.button-cell {
  display: flex;
  align-items: center;
}

.button-group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
}

.button-group button {
  width: 100%;
  margin-bottom: 5px;
}

.d-flex.justify-content-center.align-items-center {
  text-align: center;
}

.pagination-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.table {
  border-collapse: collapse;
}

.table-responsive {
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.4);
}

.table th,
.table td {
  border: none;
}

@media (max-width: 768px) {
  .table-responsive table th,
  .table-responsive table td {
    white-space: nowrap;
  }
}
</style>
