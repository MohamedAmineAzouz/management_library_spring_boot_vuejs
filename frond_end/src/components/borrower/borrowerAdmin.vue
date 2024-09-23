<template>
    <div>
      <NavBar />
      <div @click="goBack" class="button_back">
        <img src="../../assets/img/back.png" alt="Back" class="back-icon" />
      </div>
      <div class="borrower_list" v-if="borrowers.length">
        <h2>Borrower List</h2>
        <div class="search_bar">
          <input v-model="searchQuery" placeholder="Search by book title, author, or user name..." />
          <select v-model="confirmReturnFilter">
            <option value="" disabled>Trie</option>
            <option value="">All</option>
            <option value="true">Confirmed Returns</option>
            <option value="false">Unconfirmed Returns</option>
          </select>
        </div>
        <table>
          <thead>
            <tr>
              <th>Borrower ID</th>
              <th>User Name</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Book Title</th>
              <th>Book Author</th>
              <th>Borrow Date</th>
              <th>Return Date</th>
              <th>Confirm Return</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="borrower in paginatedBorrowers" :key="borrower.borrowerId">
              <td>{{ borrower.borrowerId }}</td>
              <td>{{ borrower.user.username }}</td>
              <td>{{ borrower.user.firstname }}</td>
              <td>{{ borrower.user.lastname }}</td>
              <td>{{ borrower.book.title }}</td>
              <td>{{ borrower.book.author }}</td>
              <td>{{ formatDate(borrower.borrowerDate) }}</td>
              <td>{{ formatDate(borrower.returnedDate) }}</td>
              <td>{{ borrower.confirmReturn ? 'Yes' : 'No' }}</td>
              <td>
                <button 
                  @click="confirmReturn(borrower.borrowerId)" 
                  :disabled="borrower.confirmReturn"
                  class="button_confirm"
                >
                  {{ borrower.confirmReturn ? 'Confirmed' : 'Confirm Return' }}
                </button>
                <button 
                  @click="deleteBorrower(borrower.borrowerId)" 
                  class="button_delete"
                >
                  Delete
                </button>
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
        <p>Loading borrower details...</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import NavBar from '../NavBar.vue';
  import Swal from 'sweetalert2';
  
  export default {
    name: 'BorrowerAdmin',
    components: {
      NavBar,
    },
    data() {
        return {
            borrowers: [],
            searchQuery: '',
            confirmReturnFilter: '',
            accessToken: localStorage.getItem('accessToken'),
            currentPage: 1, 
            itemsPerPage: 5, 
        };
    },
    computed: {
        filteredBorrowers() {
            return this.borrowers.filter(borrower => {
            const bookExists = borrower.book && borrower.book.title && borrower.book.author;
            const userExists = borrower.user && borrower.user.username;

            const matchesSearchQuery =
                bookExists &&
                (borrower.book.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                borrower.book.author.toLowerCase().includes(this.searchQuery.toLowerCase())) ||
                (userExists && borrower.user.username.toLowerCase().includes(this.searchQuery.toLowerCase()));

            const matchesConfirmReturn =
                this.confirmReturnFilter === '' ||
                (this.confirmReturnFilter === 'true' && borrower.confirmReturn) ||
                (this.confirmReturnFilter === 'false' && !borrower.confirmReturn);

            return matchesSearchQuery && matchesConfirmReturn;
            });
        },
        paginatedBorrowers() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            const end = start + this.itemsPerPage;
            return this.filteredBorrowers.slice(start, end);
        },
        totalPages() {
            return Math.ceil(this.filteredBorrowers.length / this.itemsPerPage);
        },
    },

    methods: {
      fetchAllBorrowers() {
        axios
          .get('http://localhost:8080/borrowers/all', {
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
      confirmReturn(borrowerId) {
        Swal.fire({
          title: 'Are you sure?',
          text: "Do you want to confirm the return of this book?",
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#3085d6',
          cancelButtonColor: '#d33',
          confirmButtonText: 'Yes, confirm it!',
        }).then((result) => {
          if (result.isConfirmed) {
            axios
              .put(
                `http://localhost:8080/borrowers/${borrowerId}`,
                { confirmReturn: true },
                {
                  headers: {
                    Authorization: `Bearer ${this.accessToken}`,
                  },
                }
              )
              .then(() => {
                this.fetchAllBorrowers();
                Swal.fire(
                  'Confirmed!',
                  'The book return has been confirmed.',
                  'success'
                );
              })
              .catch((error) => {
                console.error('Error updating borrower return status:', error);
                Swal.fire(
                  'Error!',
                  'There was an error confirming the return. Please try again.',
                  'error'
                );
              });
          }
        });
      },
      deleteBorrower(borrowerId) {
        Swal.fire({
          title: 'Are you sure?',
          text: "This action will delete the borrower record!",
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#d33',
          cancelButtonColor: '#3085d6',
          confirmButtonText: 'Yes, delete it!',
        }).then((result) => {
          if (result.isConfirmed) {
            axios
              .delete(`http://localhost:8080/borrowers/${borrowerId}`, {
                headers: {
                  Authorization: `Bearer ${this.accessToken}`,
                },
              })
              .then(() => {
                this.fetchAllBorrowers();
                Swal.fire(
                  'Deleted!',
                  'The borrower record has been deleted.',
                  'success'
                );
              })
              .catch((error) => {
                console.error('Error deleting borrower:', error);
                Swal.fire(
                  'Error!',
                  'There was an error deleting the borrower. Please try again.',
                  'error'
                );
              });
          }
        });
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
        },
    },
    mounted() {
      this.fetchAllBorrowers();
    },
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
  
  .back-icon {
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
    width: 80%;
    padding: 8px;
    margin-right: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
  }
  select{
    width:25%;
  }
  .search_bar select {
    padding: 8px;
    margin-left: 10px;
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

  .button_confirm {
    width: 130px;
    background-color: #28a745;
    color: white;
    padding: 8px 5px;
    border: none;
    border-radius: 4px;
    margin-left:10px;
    cursor: pointer;
    }

    .button_confirm:disabled {
    background-color: #ccc;
    cursor: not-allowed;
    }

    .button_delete {
    background-color: #dc3545;
    width: 130px;
    color: white;
    padding: 8px 5px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    margin-left:10px;
    margin-top:5px;
    }

    .button_delete:hover {
    background-color: #c82333;
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
  