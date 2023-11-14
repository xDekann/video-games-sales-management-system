<template>
  <div class="container mt-5">
    <!-- Upper side panel -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <LogoutButton class="btn btn-danger" :translations="translations"></LogoutButton>
      <button class="btn btn-primary add-game-button" @click="goToAddGame">{{translations.employee.gamepanel.addgame}}</button>
    </div>

    <!-- Category search -->
    <div class="label-container">
      <select id="category" v-model="selectedCategory" class="form-select mt-1">
        <option value="">{{translations.employee.gamepanel.selectCategory}}</option>
        <option v-for="category in gameCategories" :key="category.label" :value="category.value">{{ category.label }}</option>
      </select>
    </div>

    <!-- Searchbar -->
    <div class="search-container mt-2" v-click-away="clearFill">
      <input v-model="searchPhrase" @input="handleInput" :placeholder="translations.employee.gamepanel.searchGames" class="form-control" />
      <ul class="fill-options" v-show="fillOptions.length > 0">
        <li v-for="option in fillOptions" :key="option.id" @click="selectSuggestion(option)" class="fill-option">
          {{ option.name }}
        </li>
      </ul>
    </div>
    <div class="search-controls mt-2">
      <button class="btn btn-primary me-2" @click="searchGames">{{translations.employee.gamepanel.search}}</button>
      <button class="btn btn-secondary" @click="clear">{{translations.employee.gamepanel.clear}}</button>
    </div>

    <!-- Displayed games -->
    <div class="list-container mt-3">
      <ul class="list-unstyled" style="list-style: none;">
        <template v-if="games.length > 0">
          <li v-for="game in games" :key="game.id" class="game-item">
              <!-- Game Title -->
              <div class="game-title">
              <strong>{{ game.name }}</strong>
              <br><br>
              <div class="game-buttons">
                  <button @click="stockGame(game.id, true)" class="btn btn-success btn-sm btn-smaller mr-1">{{translations.employee.gamepanel.stock}}</button>
                  <button @click="stockGame(game.id, false)" class="btn btn-warning btn-sm btn-smaller mr-1">{{translations.employee.gamepanel.unstock}}</button>
                  <button @click="removeGame(game.id, game.name)" class="btn btn-danger btn-sm btn-smaller">{{translations.employee.gamepanel.remove}}</button>
              </div>
            </div>

            <!-- Separator Line -->
            <div class="separator-line"></div>

            <!-- Game Category and Producer -->
            <div class="game-category-producer">
              {{ translations.user.games.category }}<br> <strong>{{ game.category }} </strong><br><br>
              {{ translations.user.games.producer }}<br> <strong>{{ game.producer }} </strong>
            </div>

            <!-- Separator Line -->
            <div class="separator-line"></div>

            <!-- Availability and Price -->
            <div class="game-availability-price">
              <p :class="{'unavailable-text': game.amount <= 0}">
                {{ game.amount <= 0 ? translations.user.games.unavailable : translations.user.games.availability }}<br> 
              <strong>{{ game.amount }} </strong>
              </p>
              {{ translations.user.games.price }}<br> <strong>{{ game.price }} PLN </strong>
            </div>

            <!-- Game Image -->
            <div class="game-image-container">
              <img :src="game.imageUrl" @error="setAltImg" class="game-image" />
            </div>
          </li>
        </template>
        <template v-else>
          <div class="no-results">
            <p>{{translations.employee.gamepanel.noResults}}</p>
          </div>
        </template>
      </ul>
    </div>

    <!-- Action confirmation prompt -->
    <StockUnstockPrompt v-if="showingPrompt" :gameId="selectedGameId" :action="action" :translations="translations" @close="closePrompt" />

    <!-- Pagination controls -->
    <div class="d-flex justify-content-center align-items-center mt-4 paginations">
      <button @click="previousPage" :disabled="currentPage === 0" class="btn btn-secondary me-2">{{translations.employee.gamepanel.previous}}</button>
      <template v-if="games.length > 0">
        <span class="page-number">{{translations.employee.gamepanel.page}} {{ currentPage + 1 }} {{translations.employee.gamepanel.of}} {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages - 1" class="btn btn-secondary ms-2">{{translations.employee.gamepanel.next}}</button>
      </template>
      <template v-else>
        <span class="page-number">{{translations.employee.gamepanel.page}} {{ currentPage + 1 }} {{translations.employee.gamepanel.of}} 1</span>
        <button @click="nextPage" :disabled="true" class="btn btn-secondary ms-2">{{translations.employee.gamepanel.next}}</button>
      </template>
    </div>
  </div>
  <Footer :translations="translations"></Footer>
</template>

<script>
import axios from 'axios';
import { generateGameCategories } from '../gameCategories';
import { debounce } from 'lodash';
import StockUnstockPrompt from './children-components/StockUnstockPrompt.vue';
import LogoutButton from '@/components/LogoutButton.vue';
import Footer from '@/components/Footer.vue';

export default {
  props: ['translations'],

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
    async removeGame(gameId, gameName) {
      const confirmResult = await this.$swal({
        title: this.translations.employee.gamepanel.sureConfirm,
        text: this.translations.employee.gamepanel.confirmRemove + ' ' + gameName + '?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: this.translations.employee.gamepanel.confirmButtonText,
        cancelButtonText: this.translations.employee.gamepanel.cancelButtonText
      });      
      if (confirmResult.isConfirmed) {
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
        });
    },
    setAltImg(event) { 
      event.target.src = 'noimg.png'
    }, 
  },
};
</script>
<style scoped>
.container {
  margin-bottom: 50px;
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
  color: black;
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

.game-item {
  display: grid;
  grid-template-columns: 1fr 0.05fr 1fr 0.05fr 1fr auto;
  align-items: center;
  border: 1px solid #ccc;
  margin-bottom: 10px;
  overflow-x: auto;
  grid-gap: 20px;
}

.game-title, .game-category-producer, .game-availability-price {
  text-align: center;
}

.game-title {
  font-size: 28px;
  margin-left: 20px;
}

.separator-line {
  height: 50%;
  width: 1px;
  background-color: #ccc;
  align-self: center;
}

.game-category-producer strong, .game-availability-price strong {
  font-weight: bold;
}

.game-image-container {
  height: 100%;
}

.game-image {
  height: 100%;
  width: auto;
}

.game-buttons {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 10px;
}

.game-buttons button {
  width: 50%;
  margin-bottom: 5px;
}

.game-buttons button:last-child {
  margin-bottom: 0;
}

.list-container {
    overflow-x: auto;
}

@media (max-width: 768px) {
  .game-item {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto auto auto auto auto;
    grid-gap: 10px;
  }
  .game-image-container,
  .game-category-producer,
  .game-availability-price {
    text-align: center;
  }

  .game-image-container .game-image {
    width: 100%;
    height: auto;
  }

  .game-title, .btn-success {
    margin-left: 0;
    margin-right: 0;
    padding-left: 0;
    padding-right: 0;
    text-align: center;
  }

  .paginations {
    margin-bottom: 100px;
  }
}
</style>
  
  
  