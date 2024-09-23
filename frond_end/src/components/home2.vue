<template>
    <NavBar />
    <div class="home2">
      <div class="sidebar">
        <button @click="goToLoans" class="my_loans_button">My Loans</button>
        <button @click="editUser" class="edit_user_button">My Profile</button>
      </div>
      <div class="content">
        <div class="search_bar">
          <input v-model="searchQuery" placeholder="Search books by title or author..." />
          <select v-model="sortOption" class="sort_select">
            <option value="title">Sort by Title</option>
            <option value="author">Sort by Author</option>
          </select>
          <select v-model="sortDirection" class="sort_direction_select">
            <option value="asc">A to Z</option>
            <option value="desc">Z to A</option>
          </select>
        </div>
        <div class="books_grid">
          <div class="book_card" v-for="book in sortedAndFilteredBooks" :key="book.bookId">
            <img :src="`http://localhost:8080${book.picture.path}?access_token=${this.accessToken}`" alt="Book Image" class="book-image" />
            <h3>{{ truncateTitle(book.title, 10) }}</h3>
            <p>{{ book.author }}</p>
            <div class="buttons">
              <button @click="borrowBook(book.bookId , book.quantity)" class="borrow_button">Borrow this Book</button>
              <button @click="viewDetails(book.bookId)" class="details_button">Details</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import NavBar from './NavBar.vue';
  import Swal from 'sweetalert2';
  
  export default {
    name: "HOme2",
    components: {
      NavBar
    },
    data() {
      return {
        books: [],
        searchQuery: '',
        sortOption: 'title', 
        sortDirection: 'asc',
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken')
      };
    },
    computed: {
      filteredBooks() {
        return this.books.filter(book =>
          book.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          book.author.toLowerCase().includes(this.searchQuery.toLowerCase())
        );
      },
      sortedAndFilteredBooks() {
        return this.filteredBooks.slice().sort((a, b) => {
          let comparison = 0;
          if (this.sortOption === 'title') {
            comparison = a.title.localeCompare(b.title);
          } else if (this.sortOption === 'author') {
            comparison = a.author.localeCompare(b.author);
          }
          return this.sortDirection === 'asc' ? comparison : -comparison;
        });
      }
    },
    methods: {
      fetchBooks() {
        axios
          .get('http://localhost:8080/books/all', {
            headers: {
              Authorization: `Bearer ${this.accessToken}`
            }
          })
          .then((response) => {
            this.books = response.data;
          })
          .catch((error) => {
            if (error.response && error.response.status === 401) {
              this.refreshAccessToken();
            } else {
              console.error('Error fetching books:', error);
            }
          });
      },
      borrowBook(bookId, quantity) {
        if (quantity === 0) {
          Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'This book is currently out of stock!',
          });
          return;
        }
        this.$router.push(`/borrower/${bookId}`);
      },
      viewDetails(bookId) {
        this.$router.push(`/bookDetails/${bookId}`);
      },
      goToLoans() {
        this.$router.push('/MyLoans');
      },
      editUser() {
        this.$router.push('/MyProfile');
      },
      refreshAccessToken() {
        axios
          .post('http://localhost:8080/refresh_token', {
            refreshToken: this.refreshToken
          })
          .then((response) => {
            this.accessToken = response.data.accessToken;
            localStorage.setItem('accessToken', this.accessToken);
            this.fetchBooks();
          })
          .catch((error) => {
            console.error('Error refreshing access token:', error);
            this.$router.push('/');
          });
      },
      truncateTitle(title, length) {
        return title.length > length ? title.substring(0, length) + '...' : title;
      }
    },
    mounted() {
      this.fetchBooks();
    }
  };
  </script>
  
<style scoped>
.home2 {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 20%;
  padding: 20px;
  padding-top: 20%;
  position: fixed;
  top: 0;
  bottom: 0;
  overflow-y: auto;
}


.my_loans_button {
  width: 100%;
  border-radius: 5px;
  padding: 10px;
  background-color: #333;
  color: white;
  border: none;
  cursor: pointer;
  margin-top: 10px;
}

.my_loans_button:hover {
  background-color: #333;
}

.edit_user_button {
  width: 100%;
  padding: 10px;
  border-radius: 5px;
  background-color: #333;
  color: white;
  border: none;
  cursor: pointer;
  margin-top: 10px;
}

.edit_user_button:hover {
  background-color: #333;
}

.content {
  margin-left: 20%;
  width: 80%;
  padding: 20px;
  overflow-y: auto;
  height: 100vh;
}

.search_bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.search_bar input {
  width: 50%;
  padding: 8px;
  margin-right: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.sort_select,
.sort_direction_select {
  margin-left: 10px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.books_grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.book_card {
  width: calc(25% - 20px);
  background-color: #fff;
  border: 1px solid #ddd;
  padding: 15px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.book-image {
  width: 80%;
  height: 300px;
  object-fit: cover;
  border-radius: 5px;
}

.buttons {
  margin-top: 10px;
}

.borrow_button,
.details_button {
  width: 100%;
  padding: 8px;
  margin-top: 5px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
}

.borrow_button:hover {
  background-color: #0056b3;
}

.details_button {
  background-color: #69717a;
}

.details_button:hover {
  background-color: #4f565d;
}

</style>
