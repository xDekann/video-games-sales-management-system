<template>
  <div class="container mt-5">
    <!-- Upper side panel -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <LogoutButton class="btn btn-danger"></LogoutButton>
      <button class="btn btn-primary add-game-button" @click="goToAddGame">Add Game</button>
    </div>

    <!-- Category search -->
    <div class="label-container">
      <select id="category" v-model="selectedCategory" class="form-select mt-1">
        <option value="">Select Category</option>
        <option v-for="category in gameCategories" :key="category.label" :value="category.value">{{ category.label }}</option>
      </select>
    </div>

    <!-- Searchbar -->
    <div class="search-container mt-2" v-click-away="clearFill">
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
              <p>Price: {{ game.price }} PLN</p>
              <p>Amount Available: {{ game.amount }}</p>
              <p>Category: {{ game.category }}</p>
              <p>Producer: {{ game.producer }}</p>
            </div>
            <div class="game-buttons mt-2">
              <button @click="stockGame(game.id, true)" class="btn btn-success btn-sm btn-smaller mr-1">Stock</button>
              <button @click="stockGame(game.id, false)" class="btn btn-warning btn-sm btn-smaller mr-1">Unstock</button>
              <button @click="removeGame(game.id)" class="btn btn-danger btn-sm btn-smaller">Remove</button>
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

    <!-- Action confirmation prompt -->
    <StockUnstockPrompt v-if="showingPrompt" :gameId="selectedGameId" :action="action" @close="closePrompt" />

    <!-- Pagination controls -->
    <div class="d-flex justify-content-center align-items-center mt-4">
      <button @click="previousPage" :disabled="currentPage === 0" class="btn btn-secondary me-2">Previous</button>
      <template v-if="games.length > 0">
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
import { generateGameCategories } from '../gameCategories';
import { debounce } from 'lodash';
import StockUnstockPrompt from './children-components/StockUnstockPrompt.vue';
import LogoutButton from '@/components/LogoutButton.vue';
import Footer from '@/components/Footer.vue';

export default {
  mounted() {
    this.clear();
  },
  components: {
    StockUnstockPrompt,
    LogoutButton,
    Footer,
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
      if (this.searchPhrase.length < this.searchPhraseMinLetters && this.selectedCategory == '') {
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
      await new Promise(resolve => setTimeout(resolve, 300));
      this.fetchGames(this.searchPhrase, this.selectedCategory, this.currentPage, this.size, false);
    },
    async removeGame(gameId) {
      const confirmed = window.confirm("Are you sure you want to remove this game?");
      if (confirmed) {
        try {
          const response = await axios.delete(`/v1/employee/game`, {params: {id: gameId}});
          if (response.status === 200) {
            this.clear();
          }
          else {
            return;
          }
        } catch (error) {
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          return error;
        }
      }
    },
    fetchGames(phrase, category, currentPage, size, isFill) {
      if (!this.isSearchClicked && !isFill) {
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
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          console.error("Error fetching games:", error);
        });
    },
  },
};
</script>
<style scoped>
.game-item {
  border: 1px solid #ccc;
  padding: 10px;
  margin-bottom: 10px;
}

.btn-smaller {
  padding: 5px 10px;
  font-size: 12px;
}

.no-results {
  font-size: 18px;
  color: #777;
}

.search-container {
  position: relative;
  z-index: 1000;
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

.game-buttons button {
  margin-right: 5px;
}
</style>
  
  
  