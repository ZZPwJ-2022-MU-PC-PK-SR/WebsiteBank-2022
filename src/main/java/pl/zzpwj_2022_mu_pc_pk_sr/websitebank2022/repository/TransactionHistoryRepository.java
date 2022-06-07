//package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;
//
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Transaction;
//
//import java.util.Date;
//import java.util.List;
//
//@Repository
//public interface TransactionHistoryRepository extends JpaRepository<Transaction, Long> {
//
//    @Query("select t from Transaction t where t.from.user.id = ?1 and t.amount between ?2 and ?3 and t.date between ?4 and ?5")
//    List<Transaction> findBy(Long id,
//                             Double greaterThanAmount,
//                             Double lowerThanAmount,
//                             Date greaterThanDate,
//                             Date lowerThanDate,
//                             Pageable pageable);
//}
