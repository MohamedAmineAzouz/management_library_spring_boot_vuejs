<template>
    <div>
      <NavBar />
      <div class="edit-book-container">
        <div @click="back()" id="back">
          <img src="../../assets/img/back.png" alt="back" class="back"/>
        </div>
        <h2>Edit Book</h2>
        <form @submit.prevent="updateBook">
          <div class="form">
            <label for="title">Title</label>
            <input
              type="text"
              id="title"
              v-model="book.title"
              class="form-control"
              required
            />
          </div>
          <div class="form">
            <label for="author">Author</label>
            <input
              type="text"
              id="author"
              v-model="book.author"
              class="form-control"
              required
            />
          </div>
          <div class="form">
            <label for="year">Year</label>
            <input
              type="text"
              id="year"
              v-model="book.year"
              class="form-control"
              required
            />
          </div>
          <div class="form">
            <label for="type">Type</label>
            <input
              type="text"
              id="type"
              v-model="book.type"
              class="form-control"
              required
            />
          </div>
          <div class="form">
            <label for="quantity">Quantity</label>
            <input
              type="number"
              id="quantity"
              v-model="book.quantity"
              class="form-control"
              required
            />
          </div>
          <div class="form">
            <label for="picture">Picture</label>
            <input
              type="file"
              id="picture"
              @change="handleFileUpload"
              class="form-control"
            />
            <img v-if="book.picture" :src="`http://localhost:8080${book.picture.path}?access_token=${this.accessToken}`" alt="Current Book Picture" class="current-picture" />
          </div>
          <button type="submit" class="btn btn-primary">Save Changes</button>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import NavBar from '../NavBar.vue';
  import Swal from 'sweetalert2';
  
  export default {
    name: 'EditBook',
    components: {
      NavBar
    },
    data() {
      return {
        book: {
          title: '',
          author: '',
          year: '',
          type: '',
          quantity: 1,
          picture: null
        },
        newPicture: null,
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken')
      };
    },
    created() {
      this.fetchBook();
    },
    methods: {
      async fetchBook() {
        const bookId = this.$route.params.id;
        try {
          const config = {
            method: 'get',
            url: `http://localhost:8080/books/getById/${bookId}`,
            headers: {
              'Authorization': `Bearer ${this.accessToken}`
            }
          };
  
          const response = await axios(config);
          this.book = response.data;
        } catch (error) {
          if (error.response && error.response.status === 401) {
            this.refreshAccessToken();
          } else {
            console.error('Error fetching book:', error);
          }
        }
      },
      handleFileUpload(event) {
        this.newPicture = event.target.files[0];
      },
      async refreshAccessToken() {
        try {
          const response = await axios.post('http://localhost:8080/refresh_token', {
            token: this.refreshToken
          });
  
          this.accessToken = response.data.accessToken;
          localStorage.setItem('accessToken', this.accessToken);
          this.fetchBook();
        } catch (error) {
          console.error('Error refreshing access token:', error);
          this.$router.push(`/`);
        }
      },
      async updateBook() {
        const formData = new FormData();
        formData.append('book', JSON.stringify({
            title: this.book.title,
            author: this.book.author,
            year: this.book.year,
            type: this.book.type,
            quantity: this.book.quantity
        }));
        if (this.newPicture) {
          const config1 = {
            method: 'delete',
            url: `http://localhost:8080/pictures/deleteByBookId/${this.book.bookId}`,
            headers: {
            'Authorization': `Bearer ${this.accessToken}`
            }
          };
          await axios(config1);
          formData.append('file', this.newPicture);
        }

        try {
            const response = await axios.put(`http://localhost:8080/books/update/${this.book.bookId}`, formData, {
            headers: {
                'Authorization': `Bearer ${this.accessToken}`,
                'Content-Type': 'multipart/form-data'
            }
            });
            Swal.fire({
            icon: 'success',
            title: 'Book updated successfully',
            text: response.data["message"],
            timer: 2000,
            showConfirmButton: false
            });
            this.$router.push('/books');
        } catch (error) {
            console.error('Error updating book:', error);
        }
    },

      back() {
        this.$router.push('/books');
      }
    }
  };
  </script>
  
  <style scoped>
  .edit-book-container {
    max-width: 98%;
    margin: 0 auto;
    margin-bottom: 5%;
  }
  
  h2 {
    margin-top: 20px;
  }
  
  .form {
    margin-bottom: 15px;
  }
  
  .current-picture {
    max-width: 150px;
    max-height: 150px;
    object-fit: cover;
    border-radius: 4px;
    margin-top: 10px;
  }
  
  .back {
    width: 100%;
    height: 100%;
    cursor: pointer;
  }
  
  #back {
    margin-top: 1%;
    width: 30px;
    height: 30px;
  }
  
  button.btn-primary {
    background-color: green;
    color: white;
  }
  </style>
  