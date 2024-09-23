<template>
    <div>
      <NavBar />
      <div @click="goBack" class="button_back">
        <img src="../../assets/img/back.png" alt="Back" class="back_icon" />
      </div>
      <div class="borrower_list" v-if="borrowers.length">
        <h2>My Loans</h2>
        <div class="search_bar">
          <input v-model="searchQuery" placeholder="Search by book title or author..." />
          <select v-model="returnDateSort">
            <option value="" disabled>Sort by Return Date</option>
            <option value="asc">Return Date (Ascending)</option>
            <option value="desc">Return Date (Descending)</option>
          </select>
        </div>
        <table>
          <thead>
            <tr>
              <th>Book Title</th>
              <th>Author</th>
              <th>Borrow Date</th>
              <th>Return Date</th>
              <th>Confirm Return</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="borrower in paginatedBorrowers" :key="borrower.borrowerId">
              <td>{{ borrower.book.title }}</td>
              <td>{{ borrower.book.author }}</td>
              <td>{{ formatDate(borrower.borrowerDate) }}</td>
              <td>{{ formatDate(borrower.returnedDate) }}</td>
              <td>{{ borrower.confirmReturn ? 'Yes' : 'No' }}</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1">Previous</button>
          <button v-for="page in totalPages" :key="page" @click="goToPage(page)">
            {{ page }}
          </button>
          <button @click="nextPage" :disabled="currentPage === totalPages">Next</button>
        </div>
      </div>
      <div v-else>
        <p>Loading loan details...</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import NavBar from '../NavBar.vue';
  
  export default {
    name: 'MyLoans',
    components: {
      NavBar,
    },
    data() {
        return {
            borrowers: [],
            searchQuery: '',
            returnDateSort: '',
            accessToken: localStorage.getItem('accessToken'),
            currentPage: 1,
            itemsPerPage: 5,
        };
    },
    computed: {
        filteredBorrowers() {
            return this.borrowers.filter(borrower =>
            borrower.book.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
            borrower.book.author.toLowerCase().includes(this.searchQuery.toLowerCase())
            );
        },
        sortedBorrowers() {
            const borrowersCopy = [...this.filteredBorrowers];
            return borrowersCopy.sort((a, b) => {
            if (this.returnDateSort === 'asc') {
                return new Date(a.returnedDate) - new Date(b.returnedDate);
            } else if (this.returnDateSort === 'desc') {
                return new Date(b.returnedDate) - new Date(a.returnedDate);
            }
            return 0;
            });
        },
        paginatedBorrowers() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            const end = start + this.itemsPerPage;
            return this.sortedBorrowers.slice(start, end);
        },
        totalPages() {
            return Math.ceil(this.sortedBorrowers.length / this.itemsPerPage);
        }
    },
    methods: {
        fetchBorrowers() {
            const username = sessionStorage.getItem('username');
            axios
            .get(`http://localhost:8080/borrowers/getByUsername/${username}`, {
                headers: {
                Authorization: `Bearer ${this.accessToken}`,
                },
            })
            .then((response) => {
                this.borrowers = response.data;
            })
            .catch((error) => {
                console.error('Error fetching borrowers:', error);
            });
        },
        formatDate(date) {
            if (!date) return 'N/A';
            const options = { year: 'numeric', month: 'short', day: 'numeric' };
            return new Date(date).toLocaleDateString(undefined, options);
        },
        goBack() {
            this.$router.go(-1);
        },
        goToPage(page) {
            this.currentPage = page;
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
    },

    mounted() {
      this.fetchBorrowers();
    }
  };
  </script>
  
  <style scoped>

.borrower_list {
    max-width: 90%;
    margin: 20px auto;
    margin-top: 50px;
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
  
  .search_bar {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
  }
  
  .search_bar input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 5px;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
  }
  
  th,
  td {
    border: 1px solid #ccc;
    padding: 10px;
    text-align: left;
  }
  
  th {
    background-color: #333;
    color: white;
    text-align: left;
    padding: 10px;
  }
  
  tbody tr:nth-child(even) {
    background-color: #f2f2f2;
  }
  
  .borrower_list {
    padding: 20px;
  }
  
  .search_bar {
    margin-bottom: 20px;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
  }
  
  th, td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
  }
  
  select {
    margin-left: 10px;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 5px;
  }

  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
    }

    .pagination button {
    padding: 8px 12px;
    margin: 0 5px;
    border: 1px solid #ddd;
    border-radius: 5px;
    background-color: #f8f9fa;
    cursor: pointer;
    }

    .pagination button:disabled {
    cursor: not-allowed;
    opacity: 0.5;
    }

  </style>
  