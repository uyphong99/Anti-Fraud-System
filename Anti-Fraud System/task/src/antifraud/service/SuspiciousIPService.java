package antifraud.service;

import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.SuspiciousIP;
import antifraud.repository.SuspiciousIPRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class SuspiciousIPService {

    @Autowired
    SuspiciousIPRepository ipRepository;

    public void addIpAddress(SuspiciousIP ip){
        String ipAddress = ip.getIp();

        if (!isValidIP(ipAddress)) {
            throw new BadRequestException();
        }

        if(ipRepository.existsSuspiciousIPByIp(ipAddress)){
            throw new ConflictException();
        }

        ipRepository.save(ip);
    }

    public List<SuspiciousIP> findAll(){
        return new ArrayList<>(ipRepository.findAll());
    }

    @Transactional
    public Map<String, String> deleteIp(String ip) {
        if (!isValidIP(ip)) {
            throw new BadRequestException();
        }

        if (!ipRepository.existsSuspiciousIPByIp(ip)) {
            throw new NotFoundException();
        }

        ipRepository.deleteSuspiciousIPByIp(ip);
        return Map.of("status", String.format("IP %s successfully removed!", ip));
    }

    public boolean isValidIP(String ip) {
        String[] ips = ip.split("\\.");

        if(ips.length != 4) {
            return false;
        }

        for (String ipAddress: ips) {
            int number = Integer.parseInt(ipAddress);
            if (number < 0 || number > 255) {
                return false;
            }
        }

        return true;
    }
}
