<template>
  <div class="container mt-5 main-container">
    <!-- Upper side panel -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <LogoutButton class="btn btn-danger" :translations="translations"></LogoutButton>
      <div class="profile-cart-container">
        <button class="btn btn-primary me-2" @click="redirectToProfile">{{ translations.user.games.profile }}</button>
        <div class="cart-container" @click="redirectToCart">
          <img src="shopping-cart.svg" alt="Shopping Cart" class="cart-icon" />
          <div class="cart-count">
            <span class="price">{{ cartCount }}</span>
            <span class="currency-separator"></span>
            <span class="currency">PLN</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Category search -->
    <div class="label-container">
      <select id="category" v-model="selectedCategory" class="form-select mt-1">
        <option value="">{{ translations.user.games.selectCategory }}</option>
        <option v-for="category in gameCategories" :key="category.label" :value="category.value">{{ category.label }}</option>
      </select>
    </div>

    <!-- Searchbar -->
    <div class="search-container mt-2" v-click-away="clearFill">
      <input v-model="searchPhrase" @input="handleInput" :placeholder="translations.user.games.searchGames" class="form-control" />
      <ul class="fill-options" v-show="fillOptions.length > 0">
        <li v-for="option in fillOptions" :key="option.id" @click="selectSuggestion(option)" class="fill-option">
          {{ option.name }}
        </li>
      </ul>
    </div>
    <div class="search-controls mt-2">
      <button class="btn btn-primary me-2" @click="searchGames">{{ translations.user.games.search }}</button>
      <button class="btn btn-secondary" @click="clear">{{ translations.user.games.clear }}</button>
    </div>

  <!-- Displayed games -->
  <div class="list-container mt-3" style="list-style: none;">
      <ul class="list-unstyled">
        <template v-if="games.length > 0">
          <li v-for="game in games" :key="game.id" :class="{'unavailable-row': game.amount <= 0}" class="game-item">
            <!-- Game Title -->
            <div class="game-title">
              <strong>{{ game.name }}</strong>
              <br><br>
                <button v-if="game.amount > 0" @click="addToCart(game)" class="btn btn-sm btn-success">{{ translations.user.games.addToCart }}</button>
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
              <strong>{{ game.amount <= 0 ? "" : game.amount }} </strong>
              </p>
              {{ translations.user.games.price }}<br> <strong>{{ game.price }} PLN </strong>
            </div>

            <!-- Game Image -->
            <div class="game-image-container">
              <img :src="'https://picsum.photos/seed/' + game.id + '/300/300'"
                   loading="lazy"
                   @error="setAltImg"
                   class="game-image" />
            </div>

          </li>
        </template>
        <template v-else>
          <div class="no-results">
            <p>{{ translations.user.games.noResults }}</p>
          </div>
        </template>
      </ul>
    </div>

    <AddToCartModal v-if="showAddToCartModal" :game="selectedGame" @close="closeAddToCartModal" @add-to-cart="addToCartBackend" :translations="translations" />

    <!-- Pagination controls -->
    <div class="d-flex justify-content-center align-items-center mt-4 paginations">
      <button @click="previousPage" :disabled="currentPage === 0" class="btn btn-secondary me-2">{{ translations.user.games.previous }}</button>
      <template v-if="games.length > 0">
        <span class="page-number">{{ translations.user.games.page }} {{ currentPage + 1 }} {{ translations.user.games.of }} {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages - 1" class="btn btn-secondary ms-2">{{ translations.user.games.next }}</button>
      </template>
      <template v-else>
        <span class="page-number">{{ translations.user.games.page }} {{ currentPage + 1 }} {{ translations.user.games.of }} 1</span>
        <button @click="nextPage" :disabled="true" class="btn btn-secondary ms-2">{{ translations.user.games.next }}</button>
      </template>
    </div>
  </div>
  <Footer :translations="translations"></Footer>
</template>

<script>
import axios from 'axios';
import { generateGameCategories } from '@/components/gameCategories';
import { debounce } from 'lodash';
import LogoutButton from '@/components/LogoutButton.vue';
import AddToCartModal from '@/components/user/children-components/AddToCartModal.vue';
import Footer from '@/components/Footer.vue';
import 'sweetalert2/dist/sweetalert2.min.css';


export default {
  props: ['translations'],

  mounted() {
    this.clear();
    this.updateCartCount();
  },
  components: {
    AddToCartModal,
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
      selectedGame: null,
      showAddToCartModal: false,
      cartCount: 0,
    };
  },
  methods: {
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
          } else {
            this.games = response.data.products;
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
    addToCart(game) {
      this.selectedGame = game;
      this.showAddToCartModal = true;
    },
    closeAddToCartModal() {
      this.selectedGame = null;
      this.showAddToCartModal = false;
    },
    async addToCartBackend(quantity) {
      axios
        .post("/v1/user/game", null, {
          params: {
            id: this.selectedGame.id,
            amount: quantity,
          },
        })
        .then((response) => {
          if (response.status === 200) {
            this.$swal.fire({
              title: this.translations.user.games.stitle,
              text: this.translations.user.games.stext,
              icon: 'success',
              confirmButtonText: this.translations.user.games.sconfirmButtonText,
            });
          } else if (response.status === 204) {
            this.$swal.fire({
              title: this.translations.user.games.nstitle,
              text: this.translations.user.games.nstext,
              icon: 'error',
              confirmButtonText: this.translations.user.games.nsconfirmButtonText,
            });
          } else if (response.status === 400) {
            this.$swal.fire({
              title: this.translations.user.games.nftitle,
              text: this.translations.user.games.nftext,
              icon: 'error',
              confirmButtonText: this.translations.user.games.nfconfirmButtonText,
            });
          } else {
            this.$swal.fire({
              title: this.translations.user.games.etitle,
              text: this.translations.user.games.etext,
              icon: 'error',
              confirmButtonText: this.translations.user.games.econfirmButtonText,
            });
          }
          this.closeAddToCartModal();
        })
        .catch((error) => {
          if (error.response.status === 403) {
            this.$router.push("/login");
          }
          this.closeAddToCartModal();
        });
      this.updateCartCount();
    },
    redirectToProfile() {
      this.$router.push('/profile')
    },
    async updateCartCount() {
      await new Promise(resolve => setTimeout(resolve, 700));
      try {
        const response = await axios.get('/v1/user/price');
        if (response.status === 200) {
          this.cartCount = response.data;
        } else {
          return;
        }
      } catch (error) {
        if (error.response.status === 403) {
            this.$router.push("/login");
        }
        return error;
      }
    },
    redirectToCart() {
      this.$router.push('/cart');
    },
    setAltImg(event) { 
      event.target.src = 'noimg.png'
    }, 
  },
};
</script>

<style scoped>

.main-container {
  margin-bottom: 50px;
}
.cart-icon {
  width: 40px;
  height: auto;
  margin-right: 10px;
  cursor: pointer;
}

.game-item {
  display: grid;
  grid-template-columns: 1fr 0.05fr 1fr 0.05fr 1fr auto;
  align-items: center;
  border: 1px solid #ccc;
  margin-bottom: 10px;
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

.currency-separator {
    margin-right: 4px;
}

.unavailable-row {
  opacity: 0.6;
}

.unavailable-text {
  color: red;
}

@media (max-width: 1050px) {
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
    width: 50%;
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
