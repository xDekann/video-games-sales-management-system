<template>
  <div class="container mt-5">

    <!-- Upper side panel -->
    <div class="button-container">
      <LogoutButton class="btn btn-danger logout-button"></LogoutButton>
      <button class="btn btn-primary add-user-button" @click="goToAddUser">Add User</button>
    </div>

    <!-- Searchbar -->
    <div class="search-container" v-click-away="clearFill">
      <input v-model="searchPhrase" @input="handleInput" class="form-control" placeholder="Search users..." />
      <ul class="fill-options" v-show="fillOptions.length > 0">
        <li v-for="option in fillOptions" :key="option.id" @click="selectSuggestion(option)" class="fill-option">
          {{ option.username }}
        </li>
      </ul>
    </div>
    <div class="search-controls mt-2">
      <button class="btn btn-primary search-button" @click="searchUsers">Search</button>
      <span style="margin: 0 5px;"></span>
      <button class="btn btn-secondary clear-button" @click="clear">Clear</button>
    </div>

    <!-- Displayed users -->
    <div class="list-container mt-2">
      <ul class="user-list mt-2">
        <template v-if="users.length > 0">
        <li v-for="user in users" :key="user.id" class="user-item border p-2">
          <div class="user-info">
            <strong>{{ user.name }} {{ user.surname }}</strong>
            <p>Username: {{ user.username }}</p>
            <p>Email: {{ user.email }}</p>
            <div class="user-buttons mt-2">
              <button @click="editUser(user.id)" class="btn btn-success btn-sm btn-smaller">Edit User</button>
              <span style="margin: 0 2px;"></span>
              <button v-if="user.enabled" @click="removeUser(user.id, user.username, user.enabled)" class="btn btn-danger btn-sm btn-smaller">Remove User</button>
              <button v-else @click="removeUser(user.id, user.username, user.enabled)" class="btn btn-warning btn-sm btn-smaller">Enable User</button>
            </div>
          </div>
        </li>
      </template>
      <template v-else>
          <div class="no-results">
            <p>No results found</p>
          </div>
        </template>
      </ul>
    </div>
    
    <!-- Pagination controls -->
    <div class="d-flex justify-content-center align-items-center mt-4">
      <button @click="previousPage" :disabled="currentPage === 0" class="btn btn-secondary me-2">Previous</button>
      <template v-if="users.length > 0">
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
import { debounce } from "lodash";
import LogoutButton from "@/components/LogoutButton.vue";
import axios from "axios";
import Footer from '@/components/Footer.vue';

export default {
  mounted() {
    this.clear();
  },
  components: {
    LogoutButton,
    Footer,
  },
  data() {
    return {
      searchPhrase: "",
      searchPhraseMinLetters: 3,
      isSearchClicked: false,
      users: [],
      size: 2,
      fillOptions: [],
      fillSize: 5,
      fillPage: 0,
      currentPage: 0,
      totalPages: 0,
    };
  },
  methods: {
    goToAddUser() {
      this.$router.push("/userpanel/adduser");
    },
    searchUsers() {
      if (this.searchPhrase.length < this.searchPhraseMinLetters) {
        this.clear();
        return;
      }
      this.currentPage = 0;
      this.isSearchClicked = true;
      this.fetchUsers(this.searchPhrase, this.currentPage, this.size, false);
      this.fillOptions = [];
    },
    handleInput: debounce(function () {
      if (this.searchPhrase.length >= this.searchPhraseMinLetters) {
        this.fetchUsers(this.searchPhrase, this.currentPage, this.fillSize, true);
      } else {
        this.fillOptions = [];
      }
    }, 300),
    selectSuggestion(option) {
      this.searchPhrase = option.username;
      this.fillOptions = [];
    },
    editUser(userId) {
      this.$router.push({ name: 'edit-user', params: { id: userId } });
    },
    async removeUser(userId, username, isEnabled) {
      const confirmResult = await this.$swal({
        title: 'Are you sure?',
        text: isEnabled ? 'Are you sure you want to delete user ' + username + '?' : 'Are you sure you want to enable user ' + username + '?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes',
      });
      if (confirmResult.isConfirmed) {
        try {
          await axios.delete(`/v1/admin/user`, {
            params: {
              id: userId,
            },
          });
          this.clear();
        } catch (error) {
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          return;
        }
      }
    },
    clear() {
      this.currentPage = 0;
      this.searchPhrase = '';
      this.isSearchClicked = false;
      this.fetchUsers(this.searchPhrase, this.currentPage, this.size, false);
    },
    clearFill() {
      this.fillOptions = [];
    },
    async previousPage() {
      if (this.currentPage > 0) {
        await new Promise(resolve => setTimeout(resolve, 300));
        this.currentPage--;
        this.fetchUsers(this.searchPhrase, this.currentPage, this.size, false);
      }
    },
    async nextPage() {
      if (this.currentPage < this.totalPages) {
        await new Promise(resolve => setTimeout(resolve, 300));
        this.currentPage++;
        this.fetchUsers(this.searchPhrase, this.currentPage, this.size, false);
      }
    },
    fetchUsers(phrase, currentPage, size, isFill) {
      if (!this.isSearchClicked && !isFill) {
        phrase = '';
      }
      axios
        .get("/v1/admin/users", {
          params: {
            phrase,
            page: currentPage,
            size,
          },
        })
        .then((response) => {
          if (isFill) {
            this.fillOptions = response.data.users;
          } else {
            this.users = response.data.users;
            this.totalPages = response.data.totalPages;
          }
        })
        .catch((error) => {
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
        });
    },
  },
};
</script>

<style scoped>
.form-control.is-invalid {
  border-color: #dc3545;
}

.button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.add-user-button {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.logout-button {
  background-color: #dc3545;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-container {
  position: relative;
  display: inline-block;
}

.fill-options {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  list-style: none;
  background-color: white;
  border: 1px solid #ccc;
  border-top: none;
  padding: 0;
  margin: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.fill-options li {
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.fill-options li:hover {
  background-color: #f0f0f0;
}

.pagination-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;
}

.pagination-controls button {
  padding: 6px 12px;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.pagination-controls button:hover {
  background-color: #ddd;
}

.pagination-controls button:disabled {
  background-color: #e0e0e0;
  cursor: not-allowed;
}

.search-controls-spacing {
  flex: 1;
}

.page-number {
  font-size: 18px;
  color: #333;
  margin: 0px 3px 0;
}

.user-list {
  list-style-type: none;
  padding: 0;
}

.user-item {
  border: 1px solid #ccc;
  padding: 10px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-buttons button {
  color: white;
  padding: 6px 12px;
  width: 120px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 6px;
}

.user-buttons button.edit-user-button {
  background-color: #007bff;
}

.user-buttons button:hover {
  background-color: #c82333;
}

.user-buttons button.remove-user-button {
  background-color: #dc3545;
}

.user-buttons button.enable-user-button {
  background-color: #28a745;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-buttons {
  justify-content: space-between;
  margin-top: 10px;
}

.user-buttons button {
  width: auto;
  padding: 4px 8px;
  margin: 0;
}

.fill-options {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  list-style: none;
  background-color: white;
  border: 1px solid #ccc;
  border-top: none;
  padding: 0;
  margin: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1001;
}

.fill-options li {
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.fill-options li:hover {
  background-color: #f0f0f0;
}

.no-results {
  font-size: 18px;
  color: #777;
}
</style>
