<template>
    <button class="logout-button" @click="logout">Logout</button>
</template>
  
<script>
import axios from 'axios';

export default {
methods: {
    logout() {
    axios.post('/v1/logout')
        .then(() => {
        this.$router.push('/login');
        })
        .catch(error => {
        console.error('Error logging out:', error);
        });
    }
}
}
</script>
  
<style scoped>
</style>