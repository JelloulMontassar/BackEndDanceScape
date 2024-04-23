package com.dance.mo.Repositories;

import com.dance.mo.Entities.Reclamation;
import com.dance.mo.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    List<Reclamation> findByResult(String notResolved);
    List<Reclamation> findByUser(User user);

}
