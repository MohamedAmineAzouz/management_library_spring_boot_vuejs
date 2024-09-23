<template>
    <div>
      <NavBar />
      <div class="add_user">
        <div @click="back()" id="back">
          <img src="../../assets/img/back.png" alt="back" class="back"/>
        </div>
        <h2>Add New User</h2>
        <form @submit.prevent="submitForm">
          <div class="form">
            <label for="firstname">First Name:</label>
            <input type="text" id="firstname" v-model="user.firstname" required />
          </div>
          <div class="form">
            <label for="lastname">Last Name:</label>
            <input type="text" id="lastname" v-model="user.lastname" required />
          </div>
          <div class="form">
            <label for="userName">UserName:</label>
            <input type="text" id="userName" v-model="user.userName" required />
          </div>
          <div class="form">
            <label for="role">Role:</label>
            <select id="role" v-model="user.role" required>
              <option value="">Select Role</option>
              <option value="ADMIN">Admin</option>
              <option value="USER">User</option>
            </select>
          </div>
          <div class="form">
            <label for="email">Email:</label>
            <input type="email" id="email" v-model="user.email" required />
          </div>
          <div class="form">
            <label for="gsm">GSM:</label>
            <input type="text" id="gsm" v-model="user.gsm" required />
          </div>
          <div class="form">
            <label for="address">Address:</label>
            <input type="text" id="address" v-model="user.address" required />
          </div>
          <div class="form">
            <label for="sector">Sector:</label>
            <select id="sector" v-model="user.sector" required>
              <option value="" disabled>Choose your sector</option>
              <option value="Telecommunications & Networks">Telecommunications & Networks</option>
              <option value="Electronic Systems & Automation">Electronic Systems & Automation</option>
              <option value="Computer Engineering (Software Engineering, Information Systems)">Computer Engineering (Software Engineering, Information Systems)</option>
              <option value="Industrial Engineering & Logistics">Industrial Engineering & Logistics</option>
            </select>
          </div>
          <div class="form">
            <label for="cne">CNE:</label>
            <input type="text" id="cne" v-model="user.cne" required />
          </div>
          <div class="form">
            <label for="password">Password:</label>
            <input type="password" id="password" v-model="user.password" required />
          </div>
          <div class="form">
            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" v-model="confirmPassword" required />
          </div>
          <button type="submit" class="btn btn-primary">Add User</button>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import NavBar from '../NavBar.vue';
  import Swal from 'sweetalert2';
  
  export default {
    name: 'AddUser',
    components: {
      NavBar
    },
    data() {
      return {
        user: {
          firstname: '',
          lastname: '',
          userName: '',
          role: '',
          email: '',
          gsm: '',
          address: '',
          sector: '',
          cne: '',
          password: ''
        },
        confirmPassword: '',
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken')
      };
    },
    methods: {
      async submitForm() {
        if (this.user.password !== this.confirmPassword) {
          Swal.fire({
            icon: 'error',
            title: 'Passwords do not match',
            text: 'Please make sure the passwords match.',
            timer: 2000,
            showConfirmButton: false
          });
          return;
        }
  
        try {
          const response = await axios.post('http://localhost:8080/users/add', this.user, {
            headers: {
              'Authorization': `Bearer ${this.accessToken}`
            }
          });
          Swal.fire({
            icon: 'success',
            title: 'User added successfully',
            text: response.data.message,
            timer: 2000,
            showConfirmButton: false
          });
          this.$router.push('/users');
        } catch (error) {
            Swal.fire({
            icon: 'error',
            title: "User addition failed",
            text: "This username already exists",
            timer: 2000,
            showConfirmButton: false
          });
        }
      },
      async back() {
        this.$router.push('/users');
      },
      async refreshAccessToken() {
        try {
          const response = await axios.post('http://localhost:8080/refresh_token', {
            token: this.refreshToken
          });
          this.accessToken = response.data.accessToken;
          localStorage.setItem('accessToken', this.accessToken);

        } catch (error) {
          this.$router.push(`/`);
        }
      }
    }
  };
  </script>
  
  <style scoped>
  .back {
    width: 100%;
    height: 100%;
    cursor: pointer;
  }

  #back {
    width: 30px;
    height: 30px;
  }

  .add_user {
    max-width: 98%;
    margin: 0 auto;
    padding: 20px;
  }

  h2 {
    margin-bottom: 20px;
  }

  .form {
    margin-bottom: 15px;
  }

  label {
    display: block;
    margin-bottom: 5px;
  }

  input, select {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }

  button {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .btn-primary {
    background-color: green;
    color: white;
  }
  </style>
  