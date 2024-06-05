import http from "../http-common";

class loanService {
    fetchLoan(){
        return http.get(`/loans`, {
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        });
    }
    applyForLoan(data){
        return http.post(`/loans/application`, data, {
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        });
    }
    payMonthlyRate(){
        return http.post(`/loans/payRate`, {}, {
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        });
    }
    payLoanRepayment(amount){
        return http.post(`/loans/repayment?amount=${amount}`, {}, {
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        });
    }
    fetchPendingLoans(){
        return http.get(`/admin/loans`, {
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        });
    }
    pendingLoanDecision(decision, id){
        return http.post(`/admin/loans?decision=${decision}&loanId=${id}`, {}, {
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        });
    }
}

export default new loanService();
