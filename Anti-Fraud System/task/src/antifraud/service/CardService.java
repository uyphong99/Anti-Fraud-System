package antifraud.service;

import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.StolenCard;
import antifraud.repository.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CardService {

    @Autowired
    StolenCardRepository cardRepository;
    public boolean isValidCardNumber(String cardNumber)
    {
        int nDigits = cardNumber.length();

        int nSum = 0;
        boolean isSecond = false;

        for (int i = nDigits - 1; i >= 0; i--)
        {

            int d = cardNumber.charAt(i) - '0';

            if (isSecond)
                d = d * 2;

            // We add two digits to handle
            // cases that make two digits
            // after doubling
            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }

    public void addCard(StolenCard card) {
        String cardNumber = card.getNumber();

        if(cardRepository.existsStolenCardByNumber(cardNumber)) {
            throw new ConflictException();
        }

        if (!isValidCardNumber(cardNumber)) {
            throw new BadRequestException();
        }

        cardRepository.save(card);
    }

    @Transactional
    public Map<String, String> deleteCard(String cardNumber) {
        if(!cardRepository.existsStolenCardByNumber(cardNumber)) {
            throw new NotFoundException();
        }

        if (!isValidCardNumber(cardNumber)) {
            throw new BadRequestException();
        }

        cardRepository.deleteStolenCardByNumber(cardNumber);

        return Map.of("status",String.format("Card %s successfully removed!", cardNumber));
    }

    public List<StolenCard> findAll() {
        return new ArrayList<>(cardRepository.findAll());
    }
}
