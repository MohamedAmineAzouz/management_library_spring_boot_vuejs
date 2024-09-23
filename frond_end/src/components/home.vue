<template>
    <div>
      <NavBar />
  
      <div class="home">
        <div class="management_section"  @click="navigateTo('users')">
          <img src="../assets/img/managment_users.png" alt="Management Users" class="management_image"/>
        </div>
        <div class="management_section" @click="navigateTo('books')">
          <img src="../assets/img/management_books.png" alt="Management Books" class="management_image"/>
        </div>
        <div class="management_section" @click="navigateTo('borrower')">
          <img src="../assets/img/borrower.png" alt="Management Borrower" class="management_image"/>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import NavBar from './NavBar.vue';
  
  export default {
    name: 'HOme',
    components: {
      NavBar
    },
    methods: {
      navigateTo(route) {
        this.$router.push(`/${route}`);
      }
    }
  };
  </script>
  
  <style scoped>
  .home {
    display: flex;
    justify-content: space-around;
    align-items: flex-start;
    padding: 20px;
    margin-top: 6%;
  }
  
  .management_section {
    width: 20%;
    margin: 10px;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    text-align: center;
    cursor: pointer;
    transition: transform 0.2s ease-in-out;
  }
  
  .management_section:hover {
    transform: scale(1.05);
  }
  
  .management_image {
    width: 100%;
    height: auto;
    border-radius: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  </style>
  