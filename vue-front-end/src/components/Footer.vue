<template>
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="content">
            <span>&copy; Video Games Sales Management System 2023</span>
            by Oskar Ożóg
          </div>
        </div>
      </div>
    </footer>
  </template>
  
  <script>
  export default {
    name: 'Footer',
  };
  </script>
  
  <style scoped>
  .footer {
    background-color: hsl(0, 3%, 12%);
    color: white;
    padding: 10px 0;
    position: fixed;
    bottom: 0;
    width: 100%; 
  }
  
  .footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .content {
    font-size: 12px;
    margin: 0 auto;
  }
  
  </style>
  