<template>
  <div>
    <!-- Upper side panel -->
    <div class="button-container">
      <button class="add-user-button" @click="goToAddUser">Add User</button>
      <LogoutButton class="logout-button"></LogoutButton>
    </div>
    <!-- Searchbar -->
    <div class="search-container" v-click-away="clearFill">
      <input v-model="searchPhrase" @input="handleInput" placeholder="Search users..."/>
      <ul class="fill-options">
        <li v-for="option in fillOptions" :key="option" @click="selectSuggestion(option)">{{ option.username }}</li>
      </ul>
    </div>
    <div class="search-controls">
      <button class="search-button" @click="searchUsers">Search</button>
      <button class="clear-button" @click="clear">Clear</button>
    </div>
    <!-- Displayed users -->
    <ul class="user-list">
      <li v-for="user in users" :key="user.id" class="user-item">
        <div class="user-info">
          <strong>{{ user.name }} {{ user.surname }}</strong>
          <p>Username: {{ user.username }}</p>
          <p>Email: {{ user.email }}</p>
        </div>
        <div class="user-buttons">
          <button @click="editUser(user.id)" class="edit-user-button">Edit User</button>
          <button v-if="user.enabled" @click="removeUser(user.id, user.username, user.enabled)" class="remove-user-button"> Remove User </button>
          <button v-else @click="removeUser(user.id, user.username, user.enabled)" class="enable-user-button"> Enable User </button>
        </div>
      </li>
    </ul>

    <!-- Pagination controls -->
    <div class="pagination-controls">
      <button @click="previousPage" :disabled="currentPage === 0">Previous</button>
      <span class="page-number">{{ currentPage + 1 }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages - 1">Next</button>
    </div>
  </div>
</template>
  
<script>
import axios from "axios";
import { debounce } from "lodash";
import LogoutButton from "@/components/LogoutButton.vue";

export default {
    mounted() {
        this.clear();
    },
  // ... (your existing code)
    components: {
        LogoutButton,
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
            this.$router.push("/userpanel/adduser"); // Replace with your route
        },
        searchUsers() {
            if (this.searchPhrase.length < this.searchPhraseMinLetters) {
                this.clear();
                return;
            }
            this.currentPage = 0;
            this.isSearchClicked = true;
            this.fetchUsers(this.searchPhrase, this.currentPage, this.size, false);
            this.fillOptions = []
        },
        handleInput: debounce(function() {
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
        // ... (your existing methods)
        
        editUser(userId) {
          console.log(userId);
          this.$router.push({ name: 'edit-user', params: { id: userId } });
        },
        async removeUser(userId, username, isEnabled) {
          const confirmMessage = isEnabled
        ? `Are you sure you want to delete the user '${username}'?`
        : `Are you sure you want to enable the user '${username}'?`;

          if (window.confirm(confirmMessage)) {
            try {
              await axios.delete(`/v1/admin/user`, {
                params: {
                  id: userId,
                }
              });
              // Refresh the user list after successful deletion
              this.clear();
              } catch (error) {
              console.error("Error deleting user:", error);
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
                  fill: isFill,
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
                console.error("Error fetching games:", error);
            });
        },
    },
};
</script>
  
<style scoped>
.search-container {
  position: relative; /* Make the container a reference for absolute positioning */
  display: inline-block; /* Display the input and dropdown inline */
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
  align-items: center; /* Center items vertically */
  justify-content: center; /* Center items horizontally */
  margin-top: 20px;
}

.pagination-controls button {
  padding: 6px 12px; /* Adjust padding for a smaller size */
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
  margin-bottom: 10px;
}

button.search-button {
  background-color: #28a745;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button.clear-button {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.page-number {
  font-size: 18px;
  color: #333;
  margin: 0px 10px 0; /* Add horizontal spacing */
}
.button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.logout-button {
  background-color: #ccc;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-controls {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.user-list {
  list-style-type: none; /* Remove bullets */
  padding: 0; /* Remove default padding */
}

.user-item {
  border: 1px solid #ccc;
  padding: 10px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  flex: 1;
}

.user-buttons {
  display: flex;
  flex-direction: column;
}

.user-buttons button {
  color: white;
  padding: 6px 12px;
  width: 120px; /* Set a fixed width for the buttons */
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 6px;
}

.user-buttons button.edit-button {
  background-color: #007bff;
}

.user-buttons button:hover {
  background-color: #c82333;
}

.enable-user-button {
  background-color: #28a745; /* Green color */
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 6px;
}

.remove-user-button {
    background-color: #dc3545;
}

.edit-user-button {
  background-color: #007bff;
}
/* Other existing styles... */

/* Media query for smaller screens */
@media (max-width: 600px) {
  .button-container {
    flex-direction: column;
  }

  .add-user-button,
  .logout-button {
    width: 100%;
    padding: 10px;
    margin-bottom: 10px;
  }
}
</style>