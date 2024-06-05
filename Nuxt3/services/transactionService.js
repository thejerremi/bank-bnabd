import http from "../http-common";

class transactionService {
    fetchLastTransactions() {
        return http.get(`/transactions/last5`, {
            headers: {
              Authorization: 'Bearer ' + localStorage.getItem('token')
            }
          });
    }
    fetchPaginatedTransactions(page) {
        return http.get(`/transactions?page=${page}`, {
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        });
    }
    atmDeposit(data) {
        return http.post(`/transactions/atm_deposit`,
            {
                amount: data
            },
            {
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('token')
                },
            });
    }
    sendTransfer(data){
        return http.post(`/transactions/transfer`, data, {
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        });
    }

}

export default new transactionService();
