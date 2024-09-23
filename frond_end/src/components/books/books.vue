<template>
  <div>
    <NavBar />
    <div @click="back()" class="back-button">
      <img src="../../assets/img/back.png" alt="Back" class="back-icon" />
    </div>
    <div class="books_container">
      <div class="header">
        <h2>Books List</h2>
        <button class="btn btn-primary" @click="addBook">Add Book</button>
      </div>
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Search books"
        class="form-control search-input"
      />
      <div v-if="paginatedBooks.length">
        <table>
          <thead>
            <tr>
              <th>Title</th>
              <th>Author</th>
              <th>Year</th>
              <th>Type</th>
              <th>Quantity</th>
              <th>Picture</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="book in paginatedBooks" :key="book.bookId">
              <td>{{ book.title }}</td>
              <td>{{ book.author }}</td>
              <td>{{ book.year }}</td>
              <td>{{ book.type }}</td>
              <td>{{ book.quantity }}</td>
              <td>
                <img v-if="book.picture" :src="`http://localhost:8080${book.picture.path}?access_token=${this.accessToken}`" alt="Book Picture" class="book-picture" />
                <span v-else>No Picture</span>
              </td>
              <td>
                <button class="btn btn-secondary" @click="editBook(book)">Edit</button>
                <button class="btn btn-danger" @click="confirmDeleteBook(book.bookId)">Delete</button>
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
        <p>No books found.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import NavBar from '../NavBar.vue';
import Swal from 'sweetalert2';

export default {
  name: 'BOoks',
  components: {
    NavBar
  },
  data() {
    return {
      books: [],
      searchQuery: '',
      accessToken: localStorage.getItem('accessToken'),
      refreshToken: localStorage.getItem('refreshToken'),
      currentPage: 1,
      itemsPerPage: 5 
    };
  },
  computed: {
    filteredBooks() {
      return this.books.filter(book =>
        book.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        book.author.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        book.year.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        book.type.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    },
    paginatedBooks() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.filteredBooks.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.filteredBooks.length / this.itemsPerPage);
    }
  },
  created() {
    this.fetchBooks();
  },
  methods: {
    async fetchBooks() {
      try {
        const config = {
          method: 'get',
          url: 'http://localhost:8080/books/all',
          headers: { 
            'Authorization': `Bearer ${this.accessToken}`
          }
        };

        const response = await axios(config);
        this.books = response.data;
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.refreshAccessToken();
        } else {
          console.error('Error fetching books:', error);
        }
      }
    },
    async refreshAccessToken() {
      try {
        const response = await axios.post('http://localhost:8080/refresh_token', {
          token: this.refreshToken
        });

        this.accessToken = response.data.accessToken;
        localStorage.setItem('accessToken', this.accessToken);
        this.fetchBooks();
      } catch (error) {
        console.error('Error refreshing access token:', error);
        this.$router.push(`/`);
      }
    },
    back() {
      this.$router.push('/home');
    },
    addBook() {
      this.$router.push('/books/add');
    },
    editBook(book) {
      this.$router.push(`/books/edit/${book.bookId}`);
    },
    confirmDeleteBook(bookId) {
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
          this.deleteBook(bookId);
        }
      });
    },
    async deleteBook(bookId) {
      try {
        const response = await axios.delete(`http://localhost:8080/books/delete/${bookId}`, {
          headers: {
            'Authorization': `Bearer ${this.accessToken}`
          }
        });
        Swal.fire({
          icon: 'success',
          title: 'Book deleted successfully',
          text: response.data["message"],
          timer: 2000,
          showConfirmButton: false
        });
        this.fetchBooks();
      } catch (error) {
        console.error('Error deleting book:', error);
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
.book-picture {
  max-width: 150px;
  max-height: 150px;
  object-fit: cover;
  border-radius: 4px;
}

.back-button {
  margin-top: 1%;
  width: 30px;
  height: 30px;
  cursor: pointer;
  margin-left:15px;
}

.back-icon {
  width: 100%;
  height: 100%;
}

.books_container {
  max-width: 93%;
  margin: 0 auto;
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
  text-align: center;
}

td {
  font-size: large;
}

th {
  background-color: #333;
  color: white;
}

button {
  max-width: 80%;
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
