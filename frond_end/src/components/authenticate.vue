<template>
  <nav class="navbar">
    <div class="nav_title">Management Library</div>
    <div class="nav_buttons">
      <button 
        class="nav_button" 
        :class="{'active-button': isLoginVisible, 'inactive-button': !isLoginVisible}" 
        @click="showLogin"
      >
        Login
      </button>
      <button 
        class="nav_button" 
        :class="{'active-button': isRegisterVisible, 'inactive-button': !isRegisterVisible}" 
        @click="showRegister"
      >
        Register
      </button>
    </div>
  </nav>
  <div class="body">
    <div v-if="isLoginVisible" class="form_container">
      <h2>Login</h2>
      <form @submit.prevent="submitLogin">
        <table class="login_table">
          <tr>
            <td><label for="username">Username:</label></td>
            <td><input type="text" id="username" v-model="loginForm.username" required></td>
          </tr>
          <tr>
            <td><label for="password">Password:</label></td>
            <td><input type="password" id="password" v-model="loginForm.password" required minlength="8"></td>
          </tr>
        </table>
        <button type="submit" class="submit_button">Login</button>
      </form>
    </div>

    <div v-if="isRegisterVisible" class="form_container">
      <h2>Register</h2>
      <form @submit.prevent="submitRegister">
        <table class="register_table">
          <tr>
            <td><label for="firstName">First Name:</label></td>
            <td><input type="text" id="firstName" v-model="registerForm.firstName" required></td>
          </tr>
          <tr>
            <td><label for="lastName">Last Name:</label></td>
            <td><input type="text" id="lastName" v-model="registerForm.lastName" required></td>
          </tr>
          <tr>
            <td><label for="registerUsername">Username:</label></td>
            <td><input type="text" id="registerUsername" v-model="registerForm.username" required></td>
          </tr>
          <tr>
            <td><label for="registercne">CNE:</label></td>
            <td><input type="text" id="registercne" v-model="registerForm.cne" required></td>
          </tr>
          <tr>
            <td><label for="registerAddress">Address:</label></td>
            <td><input type="text" id="registerAddress" v-model="registerForm.Address" required></td>
          </tr>
          <tr>
            <td><label for="email">Email:</label></td>
            <td><input type="email" id="email" v-model="registerForm.email" required></td>
          </tr>
          <tr>
            <td><label for="gsm">GSM:</label></td>
            <td><input type="number" id="gsm" v-model="registerForm.gsm" required></td>
          </tr>
          <tr>
            <td><label for="sector">Sector:</label></td>
            <td>
              <select id="sector" v-model="registerForm.sector" required>
                <option value="Telecommunications & Networks">Telecommunications & Networks</option>
                <option value="Electronic Systems & Automation">Electronic Systems & Automation</option>
                <option value="Computer Engineering (Software Engineering, Information Systems)">Computer Engineering (Software Engineering, Information Systems)</option>
                <option value="Industrial Engineering & Logistics">Industrial Engineering & Logistics</option>
              </select>
            </td>
          </tr>
          <tr>
            <td><label for="registerPassword">Password:</label></td>
            <td><input type="password" id="registerPassword" v-model="registerForm.password" required minlength="8"></td>
          </tr>
          <tr>
            <td><label for="confirmPassword">Confirm Password:</label></td>
            <td><input type="password" id="confirmPassword" v-model="registerForm.confirmPassword" required></td>
          </tr>
        </table>
        <button type="submit" class="submit_button">Register</button>
      </form>
    </div>
        
  </div>

</template>

<script>
import axios from 'axios';
import Swal from 'sweetalert2';

export default {
name: 'AUthenticate',
data() {
  return {
    isLoginVisible: true,
    isRegisterVisible: false,
    loginForm: {
      username: '',
      password: ''
    },
    registerForm: {
      firstName: '',
      lastName: '',
      username: '',
      email: '',
      gsm: '',
      sector: '',
      password: '',
      confirmPassword: '',
      cne: '',
      address: ''
    }
  };
},
methods: {
  showLogin() {
    this.isLoginVisible = true;
    this.isRegisterVisible = false;
  },
  showRegister() {
    this.isLoginVisible = false;
    this.isRegisterVisible = true;
  },
  async submitLogin() {
    try {
      const response = await axios.post('http://localhost:8080/login', {
        "userName": this.loginForm.username,
        "password": this.loginForm.password
      });

      localStorage.setItem('accessToken', response.data['access_token']);
      localStorage.setItem('refreshToken', response.data['refresh_token']);
      localStorage.setItem('userRole', response.data['role']);
      sessionStorage.setItem('username', this.loginForm.username);

      Swal.fire({
        icon: 'success',
        title: 'Login success',
        text: response.data["message"],
        timer: 2000,
        showConfirmButton: false
      });

      if (response.data["role"] == "ADMIN") {
        this.$router.push('/home');
      } else {
        this.$router.push('/home2');
      }

    } catch (error) {
      Swal.fire({
        icon: 'error',
        title: 'Login failed',
        text: 'Password or username is invalid',
        timer: 5000,
        showConfirmButton: false
      });
    }
  },
  async submitRegister() {
    if (this.registerForm.password !== this.registerForm.confirmPassword) {
      Swal.fire({
        icon:'error',
        title: 'Passwords do not match!',
        text: 'Please confirm a password and try again',
        timer: 5000,
        showConfirmButton: false
      })
      return;
    }
    try {
        const response = await axios.post('http://localhost:8080/register', {
        firstname: this.registerForm.firstName,
        lastname: this.registerForm.lastName,
        email: this.registerForm.email,
        gsm: this.registerForm.gsm,
        address: this.registerForm.address,
        cne: this.registerForm.cne,
        sector: this.registerForm.sector,
        userName: this.registerForm.username,
        password: this.registerForm.password,
        role: 'USER'
      });

      if (response.data["message"] == "User already exist"){
        Swal.fire({
          icon:'error',
          title: 'User already exist',
          text: 'Please choose a different username and try again',
          timer: 5000,
          showConfirmButton: false
        })
        return;
      }
      Swal.fire({
        icon:'success',
        title: 'Success',
        text: response.data["message"],
        timer: 3000,
        showConfirmButton: false
      })
      this.isLoginVisible = true;
      this.isRegisterVisible = false;
    } catch (error) {
      console.error('Error registering user', error);
    }
  }
}
};
</script>

<style scoped>
.body {
  /* margin-top: -60px; */
  padding-top: 8%;
  font-family: 'Arial', sans-serif;
  background-image: url("../assets/img/background.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  min-height: 90vh;
}


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

.nav_buttons {
display: flex;
}

.nav_button {
padding: 10px 20px;
margin: 0 10px;
border: none;
cursor: pointer;
font-size: 16px;
border-radius: 5px;
transition: background-color 0.3s, color 0.3s;
}

.active-button {
background-color: black;
color: white;
}

.inactive-button {
background-color: white;
color: black;
border: 2px solid black;
}

.form_container {
max-width: 500px;
margin: auto;
padding: 20px;
background-color: #fff;
border-radius: 10px;
box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.form_container h2 {
margin-bottom: 20px;
font-size: 24px;
color: #333;
text-align: center;
}

.login_table,
.register_table {
width: 100%;
border-collapse: collapse;
}

.login_table td,
.register_table td {
padding: 10px;
vertical-align: middle;
}

.login_table label,
.register_table label {
display: block;
font-weight: bold;
width: 120%;
}

.login_table input,
.register_table input,
.register_table select {
width: 90%;
padding: 10px;
font-size: 14px;
border-radius: 5px;
border: 1px solid #ccc;
box-sizing: border-box;
margin-left: 10px;
}

.login_table input:focus,
.register_table input:focus,
.register_table select:focus {
border-color: #007BFF;
outline: none;
box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

.submit_button {
width: 100%;
padding: 10px;
background-color: black;
color: white;
border: none;
cursor: pointer;
font-size: 16px;
border-radius: 5px;
transition: background-color 0.3s, color 0.3s;
}

.submit_button:hover {
background-color: #333;
}
</style>
