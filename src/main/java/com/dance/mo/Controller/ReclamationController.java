package com.dance.mo.Controller;

import com.dance.mo.Entities.Notification;
import com.dance.mo.Entities.Reclamation;
import com.dance.mo.Entities.User;
import com.dance.mo.Services.NotificationService;
import com.dance.mo.Services.ReclamationService;
import com.dance.mo.Services.UserService;
import com.dance.mo.auth.DTO.ReclamationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/reclamations")
public class ReclamationController {
    @Autowired
    private ReclamationService reclamationService;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService ;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ReclamationController(ReclamationService reclamationService) {
        this.reclamationService = reclamationService;
    }

    @GetMapping
    public List<Reclamation> getAllReclamations() {
        return reclamationService.getAllReclamations();
    }
    @GetMapping("/reclamation/{Id}")
    public ResponseEntity<ReclamationDTO> getReclamationById(@PathVariable Long Id) {
        Reclamation rec = reclamationService.getReclamationById(Id);
        ReclamationDTO recDTO = mapReclamationToDTO(rec);
        return new ResponseEntity<>(recDTO, HttpStatus.ACCEPTED);
    }
    @PostMapping("/reclamation/resolve/{Id}")
    public ResponseEntity<ReclamationDTO> resolveReclamation(@PathVariable Long Id) {
        Reclamation rec = reclamationService.getReclamationById(Id);
        rec.setResult("resolved");
        Reclamation updated = reclamationService.createReclamation(rec);
        ReclamationDTO recDTO = mapReclamationToDTO(updated);
        return new ResponseEntity<>(recDTO, HttpStatus.ACCEPTED);
    }

    private ReclamationDTO mapReclamationToDTO(Reclamation rec) {
        ReclamationDTO recDTO = new ReclamationDTO();
        recDTO.setId(rec.getId());
        recDTO.setReclamationDate(rec.getReclamationDate());
        recDTO.setStatus(rec.getStatus());
        recDTO.setDescription(rec.getDescription());
        recDTO.setUserId(rec.getUser());
        recDTO.setResult(rec.getResult());
        return recDTO;
    }


    @PostMapping("/{userID}")
    public ResponseEntity<Reclamation> createReclamation(@PathVariable Long userID,@RequestBody Reclamation reclamation) {
        User user = userService.getUserById(userID);
        System.out.println(user.getEmail());
        reclamation.setUser(user);
        reclamation.setResult("Not resolved");
        Reclamation createdReclamation = reclamationService.createReclamation(reclamation);
        Notification notification = new Notification();
        User user2 = userService.getUserById(1L);
        notification.setReceiver(user2);
        notification.setStatus(reclamation.getStatus());
        notification.setMessage("A new reclamation has been created.");
        notification.setSendDate(Date.valueOf(reclamation.getReclamationDate()));
        notification.setReclamationId(createdReclamation.getId());
        notification.setSeen(false);
        notificationService.sendNotification(notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        return new ResponseEntity<>(createdReclamation, HttpStatus.CREATED);
    }
}
