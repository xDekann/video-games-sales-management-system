<template>
  <div id="app">
    <color-mode />
    <language />
    <router-view :translations="translations" />
  </div>
</template>

<script>
import ColorMode from '@/components/ColorMode.vue';
import Language from '@/components/Language.vue';

export default {
  components: {
    ColorMode,
    Language
  },
  computed: {
    translations() {
      return this.$store.state.translations;
    },
  },
};
</script>
<style scoped>

</style>