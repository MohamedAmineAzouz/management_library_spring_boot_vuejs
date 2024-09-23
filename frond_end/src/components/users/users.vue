<template>
  <div>
    <NavBar />
      <div @click="back()" id="back">
        <img src="../../assets/img/back.png" alt="back" class="back"/>
      </div>
      <div class="users">

      <div class="header">
        <h2>Users List</h2>
        <button class="btn btn-primary" @click="addUser">Add User</button>
      </div>
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Search users"
        class="form-control search-input"
      />
      <div v-if="paginatedUsers.length">
        <table>
          <thead>
            <tr>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Username</th>
              <th>Role</th>
              <th>Email</th>
              <th>GSM</th>
              <th>Address</th>
              <th>Sector</th>
              <th>CNE</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in paginatedUsers" :key="user.userId">
              <td>{{ user.firstname }}</td>
              <td>{{ user.lastname }}</td>
              <td>{{ user.userName }}</td>
              <td>{{ user.role }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.gsm }}</td>
              <td>{{ user.address }}</td>
              <td>{{ user.sector }}</td>
              <td>{{ user.cne }}</td>
              <td>
                <button class="btn btn-secondary" @click="editUser(user)">Edit</button>
                <button class="btn btn-danger" @click="confirmDeleteUser(user.userId)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1">Previous</button>
          <button
            v-for="page in totalPages"
            :key="page"
            @click="goToPage(page)"
            :class="{ active: page === currentPage }"
          >
            {{ page }}
          </button>
          <button @click="nextPage" :disabled="currentPage === totalPages">Next</button>
        </div>
      </div>
      <div v-else>
        <p>No users found.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import NavBar from '../NavBar.vue';
import Swal from 'sweetalert2';

export default {
  name: 'USers',
  components: {
    NavBar
  },
  data() {
    return {
      users: [],
      searchQuery: '',
      accessToken: localStorage.getItem('accessToken'),
      refreshToken: localStorage.getItem('refreshToken'),
      currentPage: 1,
      itemsPerPage: 4
    };
  },
  computed: {
    filteredUsers() {
      return this.users.filter(user => 
        user.firstname.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        user.lastname.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        user.userName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        user.role.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        user.email.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        user.gsm.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        user.address.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        user.sector.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        user.cne.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    },
    paginatedUsers() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.filteredUsers.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
    }
  },
  created() {
    this.fetchUsers();
  },
  methods: {
    async fetchUsers() {
      try {
        const config = {
          method: 'get',
          url: 'http://localhost:8080/users/all',
          headers: { 
            'Authorization': `Bearer ${this.accessToken}`
          }
        };

        const response = await axios(config);
        this.users = response.data;
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.refreshAccessToken();
        } else {
          console.error('Error fetching users:', error);
        }
      }
    },
    async back(){
      this.$router.push(`/home`);
    },
    async refreshAccessToken() {
      try {
        const response = await axios.post('http://localhost:8080/refresh_token', {
          token: this.refreshToken
        });

        this.accessToken = response.data.accessToken;
        localStorage.setItem('accessToken', this.accessToken);
        this.fetchUsers();
      } catch (error) {
        console.error('Error refreshing access token:', error);
        this.$router.push(`/`);
      }
    },
    addUser() {
      this.$router.push('/users/add');
    },
    editUser(user) {
      this.$router.push(`/users/edit/${user.userId}`);
    },
    confirmDeleteUser(userId) {
      Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!'
      }).then((result) => {
        if (result.isConfirmed) {
          this.deleteUser(userId);
        }
      });
    },
    async deleteUser(userId) {
      try {
        const response = await axios.delete(`http://localhost:8080/users/delete/${userId}`, {
          headers: {
            'Authorization': `Bearer ${this.accessToken}`
          }
        });
        Swal.fire({
          icon: 'success',
          title: 'User deleted successfully',
          text: response.data["message"],
          timer: 2000,
          showConfirmButton: false
        });
        this.fetchUsers();
      } catch (error) {
        console.error('Error deleting user:', error);
      }
    },
    goToPage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage += 1;
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage -= 1;
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

#back{
  width: 30px;
  height: 30px;
  margin-left: 15px;
  margin-top :15px;
}

.users {
  max-width: 93%;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  align-items: center;
}

.search-input {
  margin-top: 20px;
  padding: 10px;
  width: 100%;
  box-sizing: border-box;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  border: 1px solid #ccc;
  padding: 10px;
  text-align: left;
}

th {
  background-color: #333;
  color: white;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-primary {
  background-color: green;
  color: white;
}

.btn-secondary {
  background-color: #007bff;
  width: 95%;
  margin-left: 5px;
  margin-bottom: 5px;
  color: white;
}

.btn-danger {
  width: 95%;
  background-color: #dc3545;
  margin-left: 5px;
  color: white;
}

button + button {
  margin-left: 5px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pagination button {
  padding: 8px 12px;
  margin: 0 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f8f9fa;
  cursor: pointer;
}

.pagination button.active {
  background-color: #007bff;
  color: white;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
