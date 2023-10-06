<template>
  <div id="app">
    <color-mode />
    <router-view />
  </div>
</template>

<script>
import ColorMode from '@/components/ColorMode.vue';

export default {
  components: {
    ColorMode,
  },
};
</script>
<style scoped>

</style>