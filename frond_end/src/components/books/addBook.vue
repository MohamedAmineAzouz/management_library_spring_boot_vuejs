<template>
    <div>
      <NavBar />
      <div @click="back()" id="back">
          <img src="../../assets/img/back.png" alt="back" class="back"/>
        </div>
        <h2>Add New Book</h2>
      <div class="add_book">

        <form @submit.prevent="submitBook">
          <div class="form">
            <label for="title">Title</label>
            <input type="text" v-model="book.title" id="title" required />
          </div>
          <div class="form">
            <label for="author">Author</label>
            <input type="text" v-model="book.author" id="author" required />
          </div>
          <div class="form">
            <label for="year">Year</label>
            <input type="number" v-model="book.year" id="year" required />
          </div>
          <div class="form">
            <label for="numberPages">Number of Pages</label>
            <input type="number" v-model="book.numberPages" id="numberPages" required />
          </div>
          <div class="form">
            <label for="type">Type</label>
            <input type="text" v-model="book.type" id="type" required />
          </div>
          <div class="form">
            <label for="quantity">Quantity</label>
            <input type="number" v-model="book.quantity" id="quantity" required />
          </div>
          <div class="form">
            <label for="picture">Picture</label>
            <input type="file" @change="handleFileUpload" id="picture" />
          </div>
          <button type="submit" class="btn btn-primary">Add Book</button>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import NavBar from '../NavBar.vue';
  
  export default {
    name: 'AddBook',
    components: {
      NavBar
    },
    data() {
     return {
        book: {
        title: '',
        author: '',
        year: '',
        numberPages: 0,
        type: '',
        quantity: 0
        },
        selectedFile: null,
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken')
    };
  },    
    methods: {
  back() {
    this.$router.push('/books');
  },
  handleFileUpload(event) {
    this.selectedFile = event.target.files[0];
  },
  async submitBook() {
    const formData = new FormData();
    formData.append('book', JSON.stringify({
            title: this.book.title,
            author: this.book.author,
            year: this.book.year,
            type: this.book.type,
            quantity: this.book.quantity
        }));
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }
    
    try {
      await this.postBook(formData);
    } catch (error) {
      console.error('Error adding book:', error);
    }
  },
  async postBook(formData) {
    try {
      const response = await axios.post('http://localhost:8080/books/add', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${this.accessToken}`
        }
      });
      console.log('Book added:', response.data);
      this.$router.push('/books');
    } catch (error) {
      if (error.response && error.response.status === 401) {
        await this.refreshAccessToken();
      } else {
        throw error;
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
    } catch (error) {
      console.error('Error refreshing access token:', error);
      this.$router.push('/');
    }
  }
}
}
</script>
  
  <style scoped>
  .add_book {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
  }

  h2{
    margin-left :40px;
  }
  
  .back {
    width: 100%;
    height: 100%;
    cursor: pointer;
  }
  
  #back {
    width: 30px;
    height: 30px;
    margin-bottom: 20px;
    margin-top: 20px;
    margin-left: 40px;
  }
  
  .form {
    margin-bottom: 15px;
  }
  
  label {
    display: block;
    margin-bottom: 5px;
  }
  
  input {
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
  