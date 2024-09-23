<template>
    <nav class="navbar">
      <div class="nav_title">Management Library</div>
      <div class="nav_welcome">
        Welcome, {{ username }}
      </div>
      <button class="nav_button logout_button" @click="confirmLogout">
        Logout
      </button>
    </nav>
  </template>
  
  <script>
  import Swal from 'sweetalert2';

  export default {
    name: 'NavBar',
    data() {
      return {
        username: sessionStorage.getItem('username') || 'Guest'
      };
    },
    methods: {
      confirmLogout() {
      Swal.fire({
        title: 'Are you sure you want to logout?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, logout'
      }).then((result) => {
        if (result.isConfirmed) {
          this.logout();
        }
      });
    },
    logout() {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      sessionStorage.removeItem('username');
      this.$router.push('/');
    }
    }
  };
  </script>
  
  <style scoped>
  .navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #fff;
    padding: 10px 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .nav_title {
    font-size: 24px;
    font-weight: bold;
    color: black;
  }
  
  .nav_welcome {
    font-weight: bold;
    font-size: 16px;
    margin-left: -8%;
    color: black;
  }
  
  .nav_button.logout_button {
    padding: 10px 20px;
    margin-right: 1%;
    background-color: #333;
    color: white;
    border: none;
    cursor: pointer;
    font-size: 16px;
    border-radius: 5px;
    transition: background-color 0.3s, color 0.3s;
  }
  
  .nav_button.logout_button:hover {
    background-color: black;
  }
  </style>
  