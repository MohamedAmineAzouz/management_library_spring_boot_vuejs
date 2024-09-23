export function isAuthenticated() {
    const token = localStorage.getItem('accessToken');
    return !!token;
  }
  

export async function getUserRole() {
    return localStorage.getItem('userRole');
}
