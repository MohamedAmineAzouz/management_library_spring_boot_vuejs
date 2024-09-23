<template>
    <div>
      <NavBar />
      <div class="details_page">
        <div @click="back" id="back" class="back_button">
          <img src="../../assets/img/back.png" alt="Back" class="back-icon" />
        </div>
        <div class="book_details" v-if="book">
          <div class="book-image-container">
            <img 
              v-if="book.picture && book.picture.path"
              :src="`http://localhost:8080${book.picture.path}?access_token=${this.accessToken}`" 
              alt="Book Image" 
              class="book-image" 
            />
          </div>
          <div class="book_info">
            <div class="info_header">
              <h1>{{ book.title }}</h1>
              <button @click="borrowBook(book.bookId)" class="borrow_button">
                Borrow this Book
              </button>
            </div>
            <p><strong>Author:</strong> {{ book.author }}</p>
            <p><strong>Year:</strong> {{ book.year }}</p>
            <p><strong>Number of Pages:</strong> {{ book.numberPages }}</p>
            <p><strong>Type:</strong> {{ book.type }}</p>
            <p><strong>Quantity:</strong> {{ book.quantity }}</p>
          </div>
        </div>
        <div v-else>
          <p>Loading book details...</p>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import NavBar from '../NavBar.vue';
  import Swal from 'sweetalert2';
  
  export default {
    name: 'BookDetails',
    components: {
      NavBar,
    },
    data() {
      return {
        book: null,
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken'),
      };
    },
    methods: {
      async fetchBookDetails() {
        const bookId = this.$route.params.id;
        await axios
          .get(`http://localhost:8080/books/getById/${bookId}`, {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
            },
          })
          .then((response) => {
            this.book = response.data;
          })
          .catch((error) => {
            if (error.response && error.response.status === 401) {
              this.refreshAccessToken();
            } else {
              console.error('Error fetching book details:', error);
            }
          });
      },
      refreshAccessToken() {
        axios
          .post('http://localhost:8080/refresh_token', {
            refreshToken: this.refreshToken,
          })
          .then((response) => {
            this.accessToken = response.data.accessToken;
            localStorage.setItem('accessToken', this.accessToken);
            this.fetchBookDetails();
          })
          .catch((error) => {
            console.error('Error refreshing access token:', error);
            this.$router.push('/');
          });
      },
      async borrowBook(bookId) {
        if (this.book.quantity === 0) {
          Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'This book is currently out of stock!',
          });
          return;
        }
        this.$router.push(`/borrower/${bookId}`)
      },
      back() {
        this.$router.push('/home');
      }
    },
    mounted() {
      this.fetchBookDetails();
    },
  };
  </script>
  
  <style scoped>
  .details_page {
    position: relative;
    padding: 20px;
    display: flex;
    justify-content: center;
  }
  
  .back_button {
    position: absolute;
    top: 20px;
    left: 20px;
    cursor: pointer;
  }
  
  .back-icon {
    width: 30px;
    height: 30px;
  }
  
  .book_details {
    margin-top: 40px;
    display: flex;
    align-items: flex-start;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    padding: 20px;
    width: 80%;
    max-width: 1200px;
  }
  
  .book-image-container {
    flex-shrink: 0;
    width: 35%;
    padding-right: 20px;
  }
  
  .book-image {
    width: 90%;
    max-height: 400px;
    border-radius: 10px;
  }
  
  .book_info {
    flex: 1;
    display: flex;
    flex-direction: column;
    position: relative;
  }
  
  .info_header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .borrow_button {
    padding: 10px 20px;
    background-color: #007bff;
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 5px;
  }
  
  .borrow_button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  
  .borrow_button:hover:not(:disabled) {
    background-color: #0056b3;
  }
  </style>
  