<template>
    <div>
      <!-- Upper side panel -->
      <div class="button-container">
        <LogoutButton class ="logout-button"></LogoutButton>
        <div class="profile-cart-container">
            <!-- Profile Button -->
            <button class="profile-button" @click="redirectToProfile">Profile</button>
            <!-- Shopping Cart -->
            <div class="cart-container" @click="redirectToCart">
              <img src="shopping-cart.svg" alt="Shopping Cart" class="cart-icon" />
              <div class="cart-count">
                <span class="price">{{ cartCount }}</span>
              </div>
            </div>
        </div>
      </div>

      <!-- Category search -->
      <div class="label-container">
        <label for="category">Category:</label>
        <select id="category" v-model="selectedCategory">
          <option value="">Select Category</option>
          <option v-for="category in gameCategories" :key="category.label" :value="category.value">{{ category.label }}</option>
        </select>
      </div>
      
      <!-- Searchbar -->
      <div class="search-container" v-click-away="clearFill">
        <input v-model="searchPhrase" @input="handleInput" placeholder="Search games..." />
        <ul class="fill-options">
          <li v-for="option in fillOptions" :key="option" @click="selectSuggestion(option)">{{ option.name }}</li>
        </ul>
      </div>
      <div class="search-controls">
        <button class="search-button" @click="searchGames">Search</button>
        <button class="clear-button" @click="clear">Clear</button>
      </div>
  
      <!-- Displayed games -->
      <ul>
        <li v-for="game in games" :key="game.id" class="game-item">
          <div class="game-info">
            <strong>{{ game.name }}</strong>
            <p>Price: {{ game.price }}</p>
            <p>Amount Available: {{ game.amount }}</p>
            <p>Producer: {{ game.producer }}</p>
          </div>
          <div class="game-buttons">
            <button @click="addToCart(game)">Add to Cart</button>
          </div>
        </li>
      </ul>

      <AddToCartModal v-if="showAddToCartModal" :game="selectedGame" @close="closeAddToCartModal" @add-to-cart="addToCartBackend"/>
  
      <!-- Pagination controls -->
      <div class="pagination-controls">
        <button @click="previousPage" :disabled="currentPage === 0">Previous</button>
        <span class="page-number">{{ currentPage + 1 }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages - 1">Next</button>
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
      // for sake of filling user's input
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
  .game-item {
    border: 1px solid #ccc;
    border-radius: 4px;
    padding: 10px;
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    /* margin-left: 10px; */ /* Remove this line */
  }
  
  .game-info {
    flex: 1;
    margin-right: 10px;
  }
  
  .game-buttons {
    display: flex;
    flex-direction: column; /* Change the flex direction to column */
    gap: 10px;
    align-items: flex-start; /* Align items to the start of the column */
  }
  
  .game-buttons button {
    width: 100%; /* Set the width to 100% for all buttons */
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
  
  /* Style for the "Add Game" button */
  button {
    background-color: #007bff;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    margin-bottom: 10px; /* Add a bottom margin for spacing */
  }
  
  /* Style for the label and select input */
  label {
    font-weight: bold;
    margin-right: 10px;
  }
  
  /* Add padding to the label and select input container */
  .label-container {
    display: flex;
    align-items: center;
    margin-bottom: 10px; /* Add a bottom margin for spacing */
  }
  
  select {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  
  /* Style for the search container */
  .search-container {
    position: relative;
    display: inline-block;
  }
  
  /* Style for the search input */
  .search-container input {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    width: 200px;
  }
  
  /* Style for the fill options dropdown */
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
  
  .search-controls {
    display: flex;
    gap: 10px;
    margin-top: 10px;
  }
  
  /* Style for the "Search" button */
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
    margin: -8px 10px 0; /* Add horizontal spacing */
  }
  .button-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
  }
  
  .add-game-button {
    background-color: #007bff;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .profile-button,
  .logout-button {
    background-color: #007bff;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    margin-bottom: 10px; /* Add a bottom margin for spacing */
    margin-top: 7px; /* Remove top margin */
    margin-left: 0; /* Remove left margin */
    margin-right: 0; /* Remove right margin */
  }
  
  ul {
    list-style: none;
    margin: 0; /* Reset margin */
    padding: 0; /* Reset padding */
  }
  
/* Style for the profile button */

/* Style for the cart container */
.cart-container {
  display: flex;
  align-items: center; /* Align items vertically */
}

/* Style for the cart icon */
.cart-icon {
    width: 40px; /* Set the width and height as needed */
    height: 40px;
    margin-left: 10px; /* Add spacing between the profile button and cart icon */
    margin-top: 0; /* Remove top margin */
}

.cart-count {
  background-color: #007bff;
  color: white;
  width: 100px; /* Increase the width to make the dot bigger */
  height: 30px; /* Increase the height to make the dot bigger */
  display: flex;
  justify-content: center; /* Center the text horizontally */
  align-items: center; /* Center the text vertically */
  font-size: 16px; /* Increase the font size for the price */
  margin-left: 5px; /* Add spacing between the cart icon and count */
}
.cart-count .price {
  font-size: 16px; /* Increase the font size for the price */
}

/* Style for the "PLN" text */
.cart-count::after {
  content: "PLN";
  font-size: 12px; /* Adjust the font size for "PLN" as needed */
  margin-left: 2px; /* Add spacing between the price and "PLN" */
}

.profile-cart-container {
  display: flex;
  align-items: center; /* Align items vertically */
  justify-content: flex-end; /* Align items to the right */
  margin-bottom: 10px; /* Add margin as needed */
  gap: 10px; /* Add spacing between Profile and Cart */
}

  @media (max-width: 600px) {
    .button-container {
      flex-direction: column; /* Change to column layout on smaller screens */
    }
    
    .add-game-button,
    .logout-button {
      width: 100%; /* Make the button take full width on shrink */
      padding: 10px; /* Add padding for smaller screens */
      margin-bottom: 10px; /* Add margin between the buttons on smaller screens */
    }
  }
  </style>
    
    
    