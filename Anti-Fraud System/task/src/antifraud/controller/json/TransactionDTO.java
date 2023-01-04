package antifraud.controller.json;

import antifraud.exception.BadRequestException;
import antifraud.model.Transaction;
import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Getter
public class TransactionDTO {
    private long amount;
    private String ip;
    private String number;
    private String region;
    private String date;

    public Transaction toEntity() {
        Transaction transaction = new Transaction();

        Date transactionDate;

        try {
            Instant instant = Instant.parse(date);
            transactionDate = Date.from(instant);
        } catch (DateTimeParseException exception) {
            throw new BadRequestException();
        }

        transaction.setAmount(amount);
        transaction.setIp(ip);
        transaction.setNumber(number);
        transaction.setRegion(region);
        transaction.setDate(transactionDate);

        return transaction;
    }

}
