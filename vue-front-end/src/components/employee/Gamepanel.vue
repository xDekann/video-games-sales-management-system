<template>
  <div>
    <!-- Upper side panel -->
    <div class="button-container">
      <button class="add-game-button" @click="goToAddGame">Add Game</button>
      <LogoutButton class ="logout-button"></LogoutButton>
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
          <button @click="stockGame(game.id, true)">Stock</button>
          <button @click="stockGame(game.id, false)">Unstock</button>
          <button @click="removeGame(game.id)">Remove</button>
        </div>
      </li>
    </ul>

    <!-- Action confirmation prompt -->
    <StockUnstockPrompt v-if="showingPrompt" :gameId="selectedGameId" :action="action" @close="closePrompt" />

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
import { generateGameCategories } from './gameCategories';
import { debounce } from 'lodash';
import StockUnstockPrompt from './children-components/StockUnstockPrompt.vue';
import LogoutButton from '@/components/LogoutButton.vue';

export default {
  mounted() {
      this.clear();
      document.addEventListener('click', this.handleDocumentClick);
    },
  before() {
    document.removeEventListener('click', this.handleDocumentClick);
  },
  components: {
    StockUnstockPrompt,
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
    };
  },
  methods: {
    goToAddGame() {
      this.$router.push('/gamepanel/addgame');
    },
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
    stockGame(gameId, givenAction) {
      this.showingPrompt = true;
      this.selectedGameId = gameId;
      this.action = givenAction;
    },
    async closePrompt() {
      this.showingPrompt = false;
      await new Promise(resolve => setTimeout(resolve, 300)); // make sure the timeout is correct
      this.fetchGames(this.searchPhrase, this.selectedCategory, this.currentPage, this.size, false);
    },
    async removeGame(gameId) {
      const confirmed = window.confirm("Are you sure you want to remove this game?");
      if (confirmed) {
        try {
          const response = await axios.delete(`/v1/employee/game`, {params: {id: gameId}});
          if (response.status === 200) {
            this.clear();
          } else {
            console.error("Error removing game:", response.statusText);
          }
        } catch (error) {
          console.error("Error removing game:", error);
        }
      }
    },
    fetchGames(phrase, category, currentPage, size, isFill) {
      if (!this.isSearchClicked) {
        phrase = '';
        category = '';
      }
      axios
        .get("/v1/employee/game", {
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
          } else {
            this.games = response.data.products;
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
.game-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.game-info {
  flex: 1;
  margin-right: 10px;
}

.game-buttons {
  display: flex;
  gap: 10px;
}
.search-container {
  position: relative; /* Make the container a reference for absolute positioning */
  display: inline-block; /* Display the input and dropdown inline */
}

.fill-options {
  position: absolute;
  top: 100%; /* Position the dropdown below the input */
  left: 0;
  width: 100%; /* Make the dropdown width match the input width */
  list-style: none; /* Remove default bullet points */
  background-color: white; /* Set the background color */
  border: 1px solid #ccc; /* Add a border for a clean separation */
  border-top: none; /* Remove top border to connect with input */
  padding: 0; /* Remove default padding */
  margin: 0; /* Remove default margin */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Add a subtle shadow */
}

.fill-options li {
  padding: 8px 12px; /* Add padding to each option */
  cursor: pointer;
  transition: background-color 0.2s; /* Add a smooth hover effect */
}

.fill-options li:hover {
  background-color: #f0f0f0; /* Change background color on hover */
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
  margin-right: 10px; /* Add right margin for spacing */
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

.logout-button {
  background-color: #ccc;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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
  
  
  