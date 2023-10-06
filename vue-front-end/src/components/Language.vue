<template>
  <div id="language-select-container">
    <select id="language-select" v-model="selectedLanguage" @change="changeLanguage">
      <option value="en">ENG</option>
      <option value="pl">PL</option>
    </select>
  </div>
</template>
  
<script>
  import { mapActions } from 'vuex';
  import Cookies from 'js-cookie';
  
  export default {
    data() {
      const savedLanguage = Cookies.get('LANG');
      return {
      selectedLanguage: savedLanguage || 'en',
     };
    },
    methods: {
     ...mapActions(['changeLanguage']),
     changeLanguage() {
        Cookies.set('LANG', this.selectedLanguage, { expires: 365 });
        this.$store.dispatch('changeLanguage', this.selectedLanguage);
     },
    },
  };
</script>
<style scoped>
#language-select-container {
  position: fixed;
  top: 10px;
  right: 10px;
}

#language-select {
  padding: 6px;
  border-radius: 4px;
  font-size: 12px;
  width: 60px;
}

#language-select:focus {
  outline: none;
  border-color: #007BFF;
}
</style>

  