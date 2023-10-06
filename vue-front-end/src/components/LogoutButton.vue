<template>
    <button class="logout-button" @click="logout">{{translations.logout.text}}</button>
</template>
  
<script>
import axios from 'axios';

export default {
    props: ['translations'],
    
    methods: {
        logout() {
            axios.post('/v1/logout')
                .then(() => {
                    this.$router.push('/login?loggedout=true');
                })
                .catch(error => {
                    return error;
                });
        }
    }
}
</script>