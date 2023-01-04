package antifraud.service;

import antifraud.controller.json.Feedback;
import antifraud.controller.json.TransactionDTO;
import antifraud.exception.BadRequestException;
import antifraud.exception.InvalidTransactionAmountException;
import antifraud.exception.NotFoundException;
import antifraud.exception.UnprocessableException;
import antifraud.model.Transaction;
import antifraud.enums.TransactionState;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIPRepository;
import antifraud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;


@Service
@RequiredArgsConstructor
public class TransactionService {
    @Autowired
    StolenCardRepository cardRepository;
    @Autowired
    SuspiciousIPRepository ipRepository;
    @Autowired
    CardService cardService;
    @Autowired
    SuspiciousIPService ipService;
    @Autowired
    TransactionRepository transactionRepository;

    private final List<String> REGIONS = List.of("EAP", "ECA", "HIC", "LAC", "MENA", "SA", "SSA");

    private final List<String> VALID_FEEDBACK = List.of("ALLOWED", "MANUAL_PROCESSING", "PROHIBITED");
    private int ALLOW_LIMIT = 200;
    private int MANUAL_LIMIT = 1500;

    public void changeLimit(String allowOrManualLimit, String decreaseOrIncrease, long valueFromTransaction) {
        if (allowOrManualLimit.equals("ALLOW")) {
            if (decreaseOrIncrease.equals("decrease")) {
                ALLOW_LIMIT = (int) Math.ceil(ALLOW_LIMIT * 0.8 - valueFromTransaction * 0.2);
            } else {
                ALLOW_LIMIT = (int) Math.ceil(ALLOW_LIMIT * 0.8 + valueFromTransaction * 0.2);
            }
        } else {
            if (decreaseOrIncrease.equals("increase")) {
                MANUAL_LIMIT = (int) Math.ceil(MANUAL_LIMIT * 0.8 + valueFromTransaction * 0.2);
            } else {
                MANUAL_LIMIT = (int) Math.ceil(MANUAL_LIMIT * 0.8 - valueFromTransaction * 0.2);
            }
        }
    }

    public Transaction addFeedback(Feedback feedback) {
        Long transactionId = feedback.getTransactionId();
        String transactionFeedback = feedback.getFeedback();

        if (!transactionRepository.existsById(transactionId)) {
            throw new NotFoundException();
        }

        if (!VALID_FEEDBACK.contains(transactionFeedback)) {
            throw new BadRequestException();
        }

        Transaction transaction = transactionRepository.findTransactionsById(transactionId);
        Long transactionAmount = transaction.getAmount();
        String transactionResult = transaction.getResult();

        if (transactionFeedback.equals(transactionResult)) {
            throw new UnprocessableException();
        }

        if (transactionResult.equals("ALLOWED")) {
            changeLimit("ALLOW", "decrease", transactionAmount);
            if (transactionFeedback.equals("PROHIBITED")) {
                changeLimit("MANUAL", "decrease", transactionAmount);
            }
        } else if (transactionResult.equals("PROHIBITED")) {
            changeLimit("MANUAL", "increase", transactionAmount);
            if (transactionFeedback.equals("ALLOWED")) {
                changeLimit("ALLOW", "increase", transactionAmount);
            }
        } else {
            if (transactionFeedback.equals("ALLOWED")) {
                changeLimit("ALLOW", "increase", transactionAmount);
            } else {
                changeLimit("MANUAL", "decrease", transactionAmount);
            }
        }

        transaction.setFeedback(transactionFeedback);
        transactionRepository.save(transaction);

        return transaction;
    }
    public TransactionState checkAmountValidity(Long amount) {
        TransactionState validity;

        if (amount == null || amount < 1 ) {
            throw new InvalidTransactionAmountException("400 BAD REQUEST");
        } else if (amount > MANUAL_LIMIT) {
            validity = TransactionState.PROHIBITED;
        } else if (amount > ALLOW_LIMIT) {
            validity = TransactionState.MANUAL_PROCESSING;
        } else {
            validity = TransactionState.ALLOWED;
        }
        return validity;
    }

    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public List<Integer> getInformationInLastHour(TransactionDTO transaction) {
        Date transactionDate;

        try {
            Instant instant = Instant.parse(transaction.getDate());
            transactionDate = Date.from(instant);
        } catch (DateTimeParseException exception) {
            throw new BadRequestException();
        }
        //Instant instant = Instant.parse(transaction.getDate());
        //Date transactionDate = Date.from(instant);
        //Date transactionDate = DatatypeConverter.parseDateTime(transaction.getDate()).getTime();
        Date transactionDateLastHour = addHoursToJavaUtilDate(transactionDate, -1);
        String cardNumber = transaction.getNumber();

        HashSet<String> regionAndIp = new HashSet<>();
        int region = 0;
        int ip = 0;

        List<Transaction> transactions = transactionRepository.findTransactionsByDateBetweenAndNumber(
                transactionDateLastHour,
                transactionDate,
                cardNumber
        );

        for (Transaction tran: transactions) {
            if (!regionAndIp.contains(tran.getRegion())) {
                region++;
                regionAndIp.add(tran.getRegion());
            }

            if (!regionAndIp.contains(tran.getIp())) {
                ip++;
                regionAndIp.add(tran.getIp());
            }
        }

        return List.of(region, ip);
    }

    public Map<String, String> checkTransaction(TransactionDTO transaction) throws ParseException {
        String finalInfo;
        StringBuilder infor = new StringBuilder();
        String result = checkAmountValidity(transaction.getAmount()).toString();

        List<Integer> regionAndIp = getInformationInLastHour(transaction);

        int region = regionAndIp.get(0);
        int ip = regionAndIp.get(1);

        if (!REGIONS.contains(transaction.getRegion())) {
            throw new BadRequestException();
        }


        if(!cardService.isValidCardNumber(transaction.getNumber()) ||
            !ipService.isValidIP(transaction.getIp())) {
            throw new BadRequestException();
        }

        if (!result.equals("ALLOWED")) {
            infor.append("amount, ");
        }

        if (cardRepository.existsStolenCardByNumber(transaction.getNumber())) {
            result = "PROHIBITED";
            infor.append("card-number, ");
        }

        if (ipRepository.existsSuspiciousIPByIp(transaction.getIp())) {
            result = "PROHIBITED";
            infor.append("ip, ");
        }

        if (region >= 3 || ip >= 3) {
            result = "PROHIBITED";
        } else if (region == 2 || ip == 2) {
            result = "MANUAL_PROCESSING";
        }

        if (ip >= 2) {
            infor.append("ip-correlation, ");
        }

        if (region >= 2) {
            infor.append("region-correlation, ");
        }

        if (result.equals("ALLOWED")) {
            infor.setLength(0);
            infor.append("none");
            finalInfo = infor.toString();
        } else {
            finalInfo = infor.substring(0, infor.length() - 2);
        }

        Transaction entityTransaction = transaction.toEntity();
        entityTransaction.setResult(result);

        transactionRepository.save(entityTransaction);

        return Map.of("result", result, "info", finalInfo);
    }

    public void deleteAll() {
        transactionRepository.deleteAll();
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactionRepository.findAll());
    }
}
