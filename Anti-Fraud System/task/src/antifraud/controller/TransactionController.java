package antifraud.controller;

import antifraud.controller.json.Feedback;
import antifraud.controller.json.TransactionDTO;
import antifraud.service.TransactionService;
import antifraud.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("api/antifraud/transaction")
    public ResponseEntity<?> postTransaction(@RequestBody TransactionDTO transaction) throws ParseException {
        return new ResponseEntity<>(transactionService.checkTransaction(transaction), HttpStatus.OK);
    }

    @DeleteMapping("api/antifraud/transaction")
    public ResponseEntity<?> deleteAll() {
        transactionService.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/antifraud/history")
    public ResponseEntity<?> findAll() {
        return new ResponseEntity<>(transactionService.findAll(), HttpStatus.OK);
    }

    @PutMapping("/api/antifraud/transaction")
    public ResponseEntity<?> putFeedback(@RequestBody Feedback feedback) {
        return new ResponseEntity<>(transactionService.addFeedback(feedback), HttpStatus.OK);
    }
}
