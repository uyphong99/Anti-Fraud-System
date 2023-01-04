package antifraud.controller;

import antifraud.model.StolenCard;
import antifraud.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CardController {

    @Autowired
    CardService cardService;

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<?> addCard(@RequestBody StolenCard card) {
        cardService.addCard(card);

        return new ResponseEntity<>(card, HttpStatus.CREATED);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity<?> delCard(@PathVariable String number) {
        return new ResponseEntity<>(cardService.deleteCard(number), HttpStatus.OK);
    }

    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(cardService.findAll(), HttpStatus.OK);
    }
}
