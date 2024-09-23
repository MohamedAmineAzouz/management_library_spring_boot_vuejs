    import App from './App.vue';
    import { createRouter, createWebHistory } from 'vue-router';
    import authenticate from './components/authenticate.vue';
    import home from './components/home.vue';
    import home2 from './components/home2.vue';
    import users from './components//users/users.vue';
    import books from './components//books/books.vue';
    import { createApp } from 'vue';
    import { isAuthenticated , getUserRole } from './auth';
    import addUser from '@/components/users/addUser.vue';
    import editMyProfile from '@/components/users/editMyProfile.vue';
    import addBook from '@/components/books/addBook.vue';
    import bookDetails from '@/components/books/bookDetails.vue';
    import editUser from '@/components/users/editUser.vue';
    import editBook from '@/components/books/editBook.vue';
    import borrower from '@/components/borrower/borrower.vue';
    import borrowerAdmin from '@/components/borrower/borrowerAdmin.vue';
    import myLoans from '@/components/borrower/myLoans.vue';

    const routes = [
        { path: '/', component: authenticate },
        { path: '/users',            
          component: users,
          meta: { requiresAuth: true,  requiresAdmin: true }
         },
         {
            path: '/home',
            component: home,
            meta: { requiresAuth: true,  requiresAdmin: true }
        },
        {
            path: '/home2',
            component: home2,
            meta: { requiresAuth: true }
        },
        {
            path: '/users/add',
            name: 'AddUser',
            component: addUser,
            meta:{requiresAuth: true , requiresAdmin: true}
        },
        {
            path: '/users/edit/:id',
            name: 'EditUser',
            component: editUser,
            meta:{requiresAuth: true , requiresAdmin: true}
        },
        {
            path: '/books',            
            component: books,
            meta:{requiresAuth: true , requiresAdmin: true}
        },
        {
            path: '/books/add',
            name: 'addBook',
            component: addBook,
            meta:{requiresAuth: true , requiresAdmin: true}
        },
        {
            path: '/books/edit/:id',
            name: 'EditBook',
            component: editBook,
            meta:{requiresAuth: true , requiresAdmin: true}

        },
        {
            path: '/bookDetails/:id',
            name: 'BookDetails',
            component: bookDetails,
            meta:{requiresAuth: true }

        },
        {
            path: '/borrower/:id',
            name: 'borrower',
            component: borrower,
            meta:{requiresAuth: true }

        },
        {
            path: '/MyProfile',
            name: 'editMyProfile',
            component: editMyProfile,
            meta:{requiresAuth: true }

        },
        {
            path: '/borrower',
            name: 'borrowerAdmin',
            component: borrowerAdmin,
            meta:{requiresAuth: true , requiresAdmin: true }

        },
        {
            path: '/MyLoans',
            name: 'MyLoans',
            component: myLoans,
            meta:{requiresAuth: true}

        },
      ];

    const router = createRouter({
        history: createWebHistory(),
        routes
    });

    router.beforeEach(async (to, from, next) => {
        if (to.matched.some(record => record.meta.requiresAuth)) {
            if (!isAuthenticated()) {
                next('/');
            } else if (to.matched.some(record => record.meta.requiresAdmin)) {
                const role = await getUserRole();
                if (role == 'ADMIN') {
                    next();
                } else {
                    next('/home2');
                }
            } else {
                next();
            }
        } else {
            next();
        }
    });
    

    const app = createApp(App);
    app.use(router);
    app.mount('#app');

    export default router;
