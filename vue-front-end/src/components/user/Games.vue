<template>
  <div class="container mt-5">
    <!-- Upper side panel -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <!-- Logout Button (Top Right) -->
      <LogoutButton class="btn btn-danger"></LogoutButton>
      <div class="profile-cart-container">
        <!-- Profile Button (Top Right) -->
        <button class="btn btn-primary me-2" @click="redirectToProfile">Profile</button>
        <!-- Shopping Cart (Top Right) -->
        <div class="cart-container" @click="redirectToCart">
          <img src="shopping-cart.svg" alt="Shopping Cart" class="cart-icon" />
          <div class="cart-count">
            <span class="price">{{ cartCount }}</span>
            <span class="currency-separator"></span> <!-- Added separator element -->
            <span class="currency">PLN</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Category search -->
    <div class="label-container">
      <label for="category" class="category-label">Category:</label> <!-- Added category-label class for styling -->
      <select id="category" v-model="selectedCategory" class="form-select mt-1"> <!-- Added mt-1 for separation -->
        <option value="">Select Category</option>
        <option v-for="category in gameCategories" :key="category.label" :value="category.value">{{ category.label }}</option>
      </select>
    </div>

    <!-- Searchbar -->
    <div class="search-container mt-2"> <!-- Added mt-2 for separation -->
      <input v-model="searchPhrase" @input="handleInput" placeholder="Search games..." class="form-control" />
      <ul class="fill-options" v-show="fillOptions.length > 0">
        <li v-for="option in fillOptions" :key="option.id" @click="selectSuggestion(option)" class="fill-option">
          {{ option.name }}
        </li>
      </ul>
    </div>
    <div class="search-controls mt-2">
      <button class="btn btn-primary me-2" @click="searchGames">Search</button>
      <button class="btn btn-secondary" @click="clear">Clear</button>
    </div>

    <!-- Displayed games -->
    <div class="list-container mt-3">
      <ul class="list-unstyled" style="list-style: none;">
        <template v-if="games.length > 0">
          <li v-for="game in games" :key="game.id" class="game-item border p-2">
            <div class="game-info">
              <strong>{{ game.name }}</strong>
              <p>Price: {{ game.price }} PLN</p> <!-- Add "PLN" on the right -->
              <p>Amount Available: {{ game.amount }}</p>
              <p>Producer: {{ game.producer }}</p>
            </div>
            <div class="game-buttons">
              <button @click="addToCart(game)" class="btn btn-sm btn-success btn-smaller">Add to Cart</button>
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

    <AddToCartModal v-if="showAddToCartModal" :game="selectedGame" @close="closeAddToCartModal" @add-to-cart="addToCartBackend" />

    <!-- Pagination controls -->
    <div class="d-flex justify-content-center align-items-center mt-4">
      <button @click="previousPage" :disabled="currentPage === 0" class="btn btn-secondary me-2">Previous</button>
      <span class="page-number">Page {{ currentPage + 1 }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages - 1" class="btn btn-secondary ms-2">Next</button>
    </div>
  </div>
</template>

<script>
// Add pop-ups for operations like stock/unstock (error/success)
import axios from 'axios';
import { generateGameCategories } from '@/components/gameCategories';
import { debounce } from 'lodash';
import LogoutButton from '@/components/LogoutButton.vue';
import AddToCartModal from '@/components/user/children-components/AddToCartModal.vue'; // Create this modal component

export default {
  mounted() {
    this.clear();
    this.updateCartCount();
    //document.addEventListener('click', this.handleDocumentClick);
  },
  before() {
    //document.removeEventListener('click', this.handleDocumentClick);
  },
  components: {
    AddToCartModal,
    LogoutButton
  },
  data() {
    return {
      searchPhrase: '',
      searchPhraseMinLetters: 3,
      isSearchClicked: false,
      games: [],
      selectedCategory: '',
      gameCategories: generateGameCategories(),
      size: 2,
      fillOptions: [],
      fillSize: 5,
      fillPage: 0,
      currentPage: 0,
      totalPages: 0,
      showingPrompt: false,
      selectedGame: null,
      showAddToCartModal: false,
      cartCount: 0,
    };
  },
  methods: {
    searchGames() {
      if (this.searchPhrase.length < this.searchPhraseMinLetters) {
        this.clear();
        return;
      }
      this.currentPage = 0;
      this.isSearchClicked = true;
      this.fetchGames(this.searchPhrase, this.selectedCategory, this.currentPage, this.size, false);
      this.fillOptions = []
    },
    // for the sake of filling user's input
    handleInput: debounce(function() {
      if (this.searchPhrase.length >= this.searchPhraseMinLetters) {
        this.fetchGames(this.searchPhrase, this.selectedCategory, this.fillPage, this.fillSize, true);
      } else {
        this.fillOptions = [];
      }
    }, 300),
    selectSuggestion(option) {
      this.searchPhrase = option.name;
      this.fillOptions = [];
    },
    clear() {
      this.currentPage = 0;
      this.searchPhrase = '';
      this.selectedCategory = '';
      this.isSearchClicked = false;
      this.fetchGames(this.searchPhrase, this.selectedCategory, this.currentPage, this.size, false);
    },
    clearFill() {
      this.fillOptions = [];
    },
    async previousPage() {
      if (this.currentPage > 0) {
        await new Promise(resolve => setTimeout(resolve, 300));
        this.currentPage--;
        this.fetchGames(this.searchPhrase, this.selectedCategory, this.currentPage, this.size, false);    
      }
    },
    async nextPage() {
      if (this.currentPage < this.totalPages) {
        await new Promise(resolve => setTimeout(resolve, 300));
        this.currentPage++;
        this.fetchGames(this.searchPhrase, this.selectedCategory, this.currentPage, this.size, false);     
      }
    },
    async closePrompt() {
      this.showingPrompt = false;
      await new Promise(resolve => setTimeout(resolve, 300)); // make sure the timeout is correct
      this.fetchGames(this.searchPhrase, this.selectedCategory, this.currentPage, this.size, false);
    },
    fetchGames(phrase, category, currentPage, size, isFill) {
      if (!this.isSearchClicked && !isFill) {
        phrase = '';
        category = '';
      }
      axios
        .get("/v1/user/game", {
          params: {
            phrase,
            category,
            page: currentPage,
            size,
          },
        })
        .then((response) => {
          if (isFill) {
            this.fillOptions = response.data.products;
            console.log(this.fillOptions);
          } else {
            this.games = response.data.products;
            console.log(this.games);
            this.totalPages = response.data.totalPages;
          }
        })
        .catch((error) => {
          console.error("Error fetching games:", error);
        });
    },
    addToCart(game) {
      this.selectedGame = game;
      this.showAddToCartModal = true;
    },
    closeAddToCartModal() {
      this.selectedGame = null;
      this.showAddToCartModal = false;
    },
    addToCartBackend(quantity) {
      console.log(this.selectedGame.id);
      console.log(quantity);
      axios
        .post("/v1/user/game", null, {
          params: {
            id: this.selectedGame.id,
            amount: quantity,
          },
        })
        .then((response) => {
          if (response.status === 200) {
            // Show a success message using a JavaScript alert
            alert("Game added to cart successfully.");
          } else if (response.status === 204) {
            // Show an "unavailable" message using a JavaScript alert
            alert("Product is unavailable.");
          } else if (response.status === 400) {
            alert("Invalid input.");
          } else {
            // Show an error message using a JavaScript alert
            alert("An error has occurred, please try again later.");
          }
          this.closeAddToCartModal();
        })
        .catch((error) => {
          // Show an error message using a JavaScript alert
          alert("An error has occurred, please try again later.");
          console.error("Error adding game to cart:", error);
          this.closeAddToCartModal();
        });
      this.updateCartCount();
    },
    redirectToProfile() {
      // Implement the logic to redirect the user to their profile page
      // You can use Vue Router for this navigation.
    },
    async updateCartCount() {
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
    redirectToCart() {
      this.$router.push('/cart');
    },
  },
};
</script>

<style scoped>
/* Custom style for the cart icon image */
.cart-icon {
  width: 40px; /* Adjust the width as needed */
  height: auto; /* Auto-adjust the height to maintain aspect ratio */
  margin-right: 10px; /* Add spacing between the cart icon and count */
  cursor: pointer; /* Change cursor to hand on hover */
}

/* Add a border and padding to the game items */
.game-item {
  border: 1px solid #ccc;
  padding: 10px;
  margin-bottom: 10px;
}

/* Make the "Add to Cart" button smaller */
.btn-smaller {
  padding: 5px 10px;
  font-size: 12px;
}

/* Center the "No results found" message */
.no-results {
  text-align: center;
  font-size: 18px;
  color: #777;
}

.search-container {
  position: relative;
  z-index: 1000;
}

/* Adjust the z-index for .fill-options */
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

.currency-separator {
    margin-right: 4px; /* Adjust the margin as needed for separation */
  }
</style>
