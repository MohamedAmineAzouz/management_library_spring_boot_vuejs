<template>
    <div>
      <NavBar />
      <div class="edit_user">
        <div @click="back()" id="back">
          <img src="../../assets/img/back.png" alt="back" class="back" />
        </div>
        <h2>Edit User</h2>
        <form @submit.prevent="updateUser">
          <div class="form">
            <label for="firstname">First Name</label>
            <input type="text" v-model="user.firstname" id="firstname" required />
          </div>
          <div class="form">
            <label for="lastname">Last Name</label>
            <input type="text" v-model="user.lastname" id="lastname" required />
          </div>
          <div class="form">
            <label for="userName">UserName</label>
            <input type="text" disabled v-model="user.userName" id="userName" required />
          </div>
          <div class="form">
            <label for="role">Role</label>
            <select v-model="user.role" id="role" required>
              <option value="ADMIN">Admin</option>
              <option value="USER">User</option>
            </select>
          </div>
          <div class="form">
            <label for="email">Email</label>
            <input type="email" v-model="user.email" id="email" required />
          </div>
          <div class="form">
            <label for="gsm">GSM</label>
            <input type="text" v-model="user.gsm" id="gsm" required />
          </div>
          <div class="form">
            <label for="address">Address</label>
            <input type="text" v-model="user.address" id="address" required />
          </div>
          <div class="form">
            <label for="sector">Sector</label>
            <select id="sector" v-model="user.sector" required>
              <option value="Telecommunications & Networks">Telecommunications & Networks</option>
              <option value="Electronic Systems & Automation">Electronic Systems & Automation</option>
              <option value="Computer Engineering (Software Engineering, Information Systems)">Computer Engineering (Software Engineering, Information Systems)</option>
              <option value="Industrial Engineering & Logistics">Industrial Engineering & Logistics</option>
            </select>
          </div>
          <div class="form">
            <label for="cne">CNE</label>
            <input type="text" v-model="user.cne" id="cne" required />
          </div>
          <div class="form">
            <label for="password">New Password</label>
            <input type="password" v-model="user.password" id="password" />
          </div>
          <div class="form">
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" v-model="confirmPassword" id="confirmPassword" />
          </div>
          <button type="submit" class="btn btn-primary">Update User</button>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import NavBar from '../NavBar.vue';
  import Swal from 'sweetalert2';
  
  export default {
    name: 'EditUser',
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
        accessToken: localStorage.getItem('accessToken')
      };
    },
    created() {
      this.fetchUser();
    },
    methods: {
  async fetchUser() {
    try {
      const response = await axios.get(`http://localhost:8080/users/getById/${this.$route.params.id}`, {
        headers: {
          'Authorization': `Bearer ${this.accessToken}`
        }
      });
      this.user = response.data;
    } catch (error) {
      console.error('Error fetching user:', error);
    }
  },
  back() {
    this.$router.push('/users');
  },
  async updateUser() {
    if (this.user.password){
        if (this.user.password !== this.confirmPassword) {
            Swal.fire({
                icon: 'error',
                title: 'Passwords do not match',
                text: 'Please make sure the passwords match.',
            });
            return;
        }
    }

    const userData = { ...this.user };

    if (!this.user.password) {
      delete userData.password;
    }

    try {
      await axios.put(`http://localhost:8080/users/update/${this.$route.params.id}`, userData, {
        headers: {
          'Authorization': `Bearer ${this.accessToken}`
        }
      });
      Swal.fire({
        icon: 'success',
        title: 'User updated successfully',
        timer: 2000,
        showConfirmButton: false
      });
      this.$router.push('/users');
    } catch (error) {
      console.error('Error updating user:', error);
    }
  }
}};
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

  .edit_user {
    max-width: 98%;
    margin: 0 auto;
    padding: 20px;
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
    padding: 10px 15px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    background-color: green;
    color: white;
  }
  </style>
  