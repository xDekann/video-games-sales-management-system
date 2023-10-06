<template>
  <label class="switch">
    <input type="checkbox" @change="toggleMode" :checked="isDarkTheme" />
    <span class="slider round">
      <span class="sun-icon">☀️</span>
      <span class="moon-icon">🌙</span>
    </span>
  </label>
</template>
  
<script>
  import Cookies from 'js-cookie';

  export default {
    data() {
      return {
        isDarkTheme: false
      };
    },
    mounted() {
      const darkThemeCookie = Cookies.get('DARKTHEME');
      if (darkThemeCookie === 'true') {
        this.isDarkTheme = true;
        this.setDarkMode();
      } else {
        this.isDarkTheme = false;
        this.setLightMode();
      }
    },
    methods: {
      toggleMode() {
        this.isDarkTheme = !this.isDarkTheme;
        if (this.isDarkTheme) {
          this.setDarkMode();
          Cookies.set('DARKTHEME', 'true', {expires: 365});
        } else {
          this.setLightMode();
          Cookies.set('DARKTHEME', 'false', {expires: 365});
        }
      },
      setDarkMode() {
        document.documentElement.style.setProperty('--bg-color', '#292626');
        document.documentElement.style.setProperty('--text-color', '#ffffff');
      },
      setLightMode() {
        document.documentElement.style.setProperty('--bg-color', '#ffffff');
        document.documentElement.style.setProperty('--text-color', '#000000');
      },
    },
  };
</script>
  
<style scoped>
.switch {
  position: fixed;
  display: inline-block;
  width: 60px;
  height: 34px;
  top: 0px;
  left: 0px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: 0.4s;
  transition: 0.4s;
  border-radius: 34px;
}

.slider:before {
  position: absolute;
  content: '';
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: 0.4s;
  transition: 0.4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}
.sun-icon, .moon-icon {
  position: absolute;
  display: inline-block;
  user-select: none;
}

.sun-icon {
  left: 6px;
  bottom: 6px;
}

.moon-icon {
  right: 6px;
  bottom: 6px;
}
</style>
  