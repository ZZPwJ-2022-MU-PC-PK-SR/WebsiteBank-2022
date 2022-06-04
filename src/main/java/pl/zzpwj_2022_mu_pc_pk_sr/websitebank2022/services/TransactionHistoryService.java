package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Transaction;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.TransactionResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionHistoryRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TransactionHistoryService {

    @Autowired
    private TransactionHistoryRepository repository;

    public List<Transaction> getTransactionsHistory(Long id,
                                                    Double greaterThanAmount,
                                                    Double lowerThanAmount,
                                                    Date greaterThanDate,
                                                    Date lowerThanDate,
                                                    int page,
                                                    int size,
                                                    List<String> sortList,
                                                    String sortOrder) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sortList, sortOrder)));
        if (lowerThanDate == null) {
            lowerThanDate = new Timestamp(new Date().getTime());
        }
        if (greaterThanDate == null) {
            greaterThanDate = new Timestamp(0);
        }
        return repository.findBy(
                id, greaterThanAmount, lowerThanAmount, greaterThanDate, lowerThanDate, pageable);
    }

    private List<Sort.Order> createSortOrder(List<String> sortList, String sortDirection) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort.Direction direction;
        for (String sort : sortList) {
            direction = Sort.Direction.fromString(sortDirection);
            sorts.add(new Sort.Order(direction, sort));
        }
        return sorts;
    }

    public ArrayList<TransactionResponse> getTransactionHistoryStructure(List<Transaction> transactionsList) {
        ArrayList<TransactionResponse> list = new ArrayList<>();
        for (Transaction t : transactionsList) {
            list.add(new TransactionResponse(t));
        }
        return list;
    }

}