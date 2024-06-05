import http from "../http-common";

class authService {
  registerUser(data) {
    return http.post(`/auth/register`, data);
  }
  loginUser(data) {
    return http.post(`/auth/authenticate`, data);
  }
  getUserByToken() {
    return http.get(`/auth/user-token`, {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    });
  }
  logoutUser() {
    return http.get(`/auth/logout`, {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    });
  }
  deleteUser(data){
    
    return http.post(`/auth/delete-account`, 
    {
      password: data
    },
    {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      },
    });
  }
  changePassword(data){
    return http.patch(`/users/change-password`, data, {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    });
  }
  updateDetails(data){
    return http.patch(`/users/update-details`, data, {
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    });
  }
}

export default new authService();
