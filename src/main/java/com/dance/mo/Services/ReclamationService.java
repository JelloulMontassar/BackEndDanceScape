package com.dance.mo.Services;

import com.dance.mo.Entities.Reclamation;
import com.dance.mo.Repositories.ReclamationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReclamationService {

    private final ReclamationRepository reclamationRepository;

    @Autowired
    public ReclamationService(ReclamationRepository reclamationRepository) {
        this.reclamationRepository = reclamationRepository;
    }

    public List<Reclamation> getAllReclamations() {
        return reclamationRepository.findAll();
    }

    public Reclamation createReclamation(Reclamation reclamation) {
        return reclamationRepository.save(reclamation);
    }

    public Reclamation getReclamationById(Long id) {
        return reclamationRepository.getById(id);
    }
}

