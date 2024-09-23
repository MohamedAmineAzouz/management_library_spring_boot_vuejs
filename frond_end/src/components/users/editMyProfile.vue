<template>
    <div>
      <NavBar />
      <div @click="goBack" class="button_back">
        <img src="../../assets/img/back.png" alt="Back" class="back_icon" />
      </div>
      <div class="edit_profile">
        <h2>Edit Profile</h2>
        <form @submit.prevent="updateProfile">
          <div class="form">
            <label for="firstname">First Name:</label>
            <input v-model="user.firstname" type="text" id="firstname" required />
          </div>
          <div class="form">
            <label for="lastname">Last Name:</label>
            <input v-model="user.lastname" type="text" id="lastname" required />
          </div>
          <div class="form">
            <label for="email">Email:</label>
            <input v-model="user.email" type="email" id="email" required />
          </div>
          <div class="form">
            <label for="gsm">GSM:</label>
            <input v-model="user.gsm" type="text" id="gsm" required />
          </div>
          <div class="form">
            <label for="address">Address:</label>
            <input v-model="user.address" type="text" id="address" required />
          </div>
          <div class="form">
            <label for="sector">Sector:</label>
            <select id="sector" v-model="user.sector" required>
              <option value="Telecommunications & Networks">Telecommunications & Networks</option>
              <option value="Electronic Systems & Automation">Electronic Systems & Automation</option>
              <option value="Computer Engineering (Software Engineering, Information Systems)">Computer Engineering (Software Engineering, Information Systems)</option>
              <option value="Industrial Engineering & Logistics">Industrial Engineering & Logistics</option>
            </select>
          </div>
          <div class="form">
            <label for="password">New Password:</label>
            <input v-model="user.password" type="password" id="password" />
          </div>
          <div class="form">
            <label for="confirm-password">Confirm Password:</label>
            <input v-model="confirmPassword" type="password" id="confirm-password" />
          </div>
          <button type="submit" class="save_button">Save Changes</button>
        </form>
      </div>
    </div>
  </template>
  
  
  <script>
  import axios from 'axios';
  import NavBar from '../NavBar.vue';
  import Swal from 'sweetalert2';
  
  export default {
    name: 'EditProfile',
    components: {
      NavBar,
    },
    data() {
      return {
        user: {
          firstname: '',
          lastname: '',
          email: '',
          gsm: '',
          address: '',
          sector: '', 
          password: '',
        },
        confirmPassword: '',
        accessToken: localStorage.getItem('accessToken'),
        username: sessionStorage.getItem('username'),
      };
    },
    methods: {
      fetchUserProfile() {
        axios
          .get(`http://localhost:8080/users/getByUsername/${this.username}`, {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
            },
          })
          .then((response) => {
            this.user = response.data;
          })
          .catch((error) => {
            console.error('Error fetching user profile:', error);
            if (error.response && error.response.status === 401) {
                this.refreshAccessToken();
            }
          });
      },
      updateProfile() {
        if(this.user.password){
          if (this.user.password !== this.confirmPassword) {
            Swal.fire({
              icon: 'error',
              title: 'Password Mismatch',
              text: 'The password and confirm password do not match.',
            });
            return;
          }
        }
  
        axios
          .put(`http://localhost:8080/users/update/${this.user.userId}`, this.user, {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
            },
          })
          .then(() => {
            Swal.fire({
              icon: 'success',
              title: 'Profile updated!',
              text: 'Your profile has been updated successfully.',
            });
            this.$router.go(-1);
          })
          .catch((error) => {
            console.error('Error updating profile:', error);
            Swal.fire({
              icon: 'error',
              title: 'Update failed',
              text: 'There was an error updating your profile. Please try again.',
            });
          });
      },
      goBack() {
        this.$router.go(-1);
      }
    },
    mounted() {
      this.username = sessionStorage.getItem('username');
      this.fetchUserProfile();
    },
  };
  </script>
  
<style scoped>
.edit_profile {
  max-width: 600px;
  margin: 0 auto;
  margin-top: 40px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: relative;
}

.button_back {
  position: absolute;
  top: 80px;
  left: 20px;
  cursor: pointer;
}

.back_icon {
  width: 30px;
  height: 30px;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.form {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input[type="text"],
input[type="email"],
input[type="password"],
select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-sizing: border-box;
}

.save_button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.save_button:hover {
  background-color: #0056b3;
}
</style>
