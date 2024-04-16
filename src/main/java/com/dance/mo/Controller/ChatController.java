package com.dance.mo.Controller;

import com.dance.mo.Entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("*")
@Controller
public class ChatController {
    @Autowired
    private final SimpMessageSendingOperations messagingTemplate;
    public ChatController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receivePublicMessage(@Payload Message chatMessage) {
        return chatMessage;
    }
    @MessageMapping("/private-message/{session}")
    public void handlePrivateMessage(@Payload Message message, @DestinationVariable("session") String user) {
        System.out.println("Received message: " + message);
        System.out.println("Receiver name: " + message.getReceiverName());
        messagingTemplate.convertAndSendToUser(user, "/private", message);
    }


}
