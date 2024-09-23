<template>
    <div>
      <NavBar />
      <div class="borrower_page">
        <div @click="goBack" class="back_button">
          <img src="../../assets/img/back.png" alt="Back" class="back_icon" />
        </div>
        <div class="borrower_details" v-if="book">
          <div class="book_image">
            <img 
              v-if="book.picture && book.picture.path"
              :src="`http://localhost:8080${book.picture.path}?access_token=${this.accessToken}`" 
              alt="Book Image" 
              class="book-image" 
            />
          </div>
          <div class="book-info">
            <h1>{{ book.title }}</h1>
            <p><strong>Author:</strong> {{ book.author }}</p>
            <label for="borrowDate"><strong>Borrow Date:</strong></label>
            <input 
              type="date"
              id="borrowDate" 
              :value="todayDate" 
              class="borrow_date_input"
              disabled
            />
            <label for="returnDate"><strong>Return Date:</strong></label>
            <input 
              type="date"
              id="returnDate" 
              v-model="returnDate" 
              class="return_date_input"
            />
            <button @click="confirmBorrow" class="confirm_borrow_button">Confirm Borrow</button>
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
    name: 'BOrrower',
    components: {
      NavBar,
    },
    data() {
      return {
        book: null,
        borrowDate: '',
        returnDate: '',
        todayDate: new Date().toISOString().split('T')[0],
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
      async confirmBorrow() {
        const bookId = this.$route.params.id;
        if (!this.returnDate) {
          Swal.fire({
            icon: 'warning',
            title: 'Oops...',
            text: 'Please select a return date.',
          });
          return;
        }
  
        try {
          const username =  sessionStorage.getItem('username');
          const role = localStorage.getItem('userRole')
          await axios.post('http://localhost:8080/borrowers/add', {
            borrowerDate: this.todayDate,
            returnedDate: this.returnDate,
            book: {
                bookId: bookId,
            },
            user: {
                role: role,
                userName: username,
            },
          }, {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
            },
          });
          Swal.fire({
            icon: 'success',
            title: 'Success',
            text: 'Book borrowed successfully!',
          });
          this.$router.push('/home2');
        } catch (error) {
          Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: "You have already borrowed this book and haven't returned it yet.",
          });
        }
      },
      goBack() {
        this.$router.go(-1);
      }
    },
    mounted() {
      this.fetchBookDetails();
    },
  };
  </script>
  
  <style scoped>
  .borrower_page {
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
  
  .back_icon {
    width: 30px;
    height: 30px;
  }
  
  .borrower_details {
    margin-top: 60px;
    display: flex;
    align-items: flex-start;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    padding: 20px;
    width: 80%;
    max-width: 800px;
  }
  
  .book_image {
    flex-shrink: 0;
    width: 50%;
    padding-right: 20px;
  }
  
  .book-image {
    width: 70%;
    max-height: 400px;
    object-fit: cover;
    border-radius: 10px;
  }
  
  .book-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    position: relative;
  }
  
  .borrow_date_input,
  .return_date_input {
    margin-top: 10px;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 5px;
    width: 100%;
  }
  
  .confirm_borrow_button {
    margin-top: 20px;
    padding: 10px 20px;
    background-color: #007bff;
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 5px;
  }
  
  .confirm_borrow_button:hover {
    background-color: #0056b3;
  }
  </style>
