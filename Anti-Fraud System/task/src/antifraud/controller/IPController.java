package antifraud.controller;

import antifraud.model.SuspiciousIP;
import antifraud.service.SuspiciousIPService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class IPController {

    @Autowired
    SuspiciousIPService ipService;

    @PostMapping("api/antifraud/suspicious-ip")
    public ResponseEntity<?> addIPAddress(@RequestBody SuspiciousIP ip) {
        ipService.addIpAddress(ip);

        return new ResponseEntity<>(ip, HttpStatus.CREATED);
    }

    @GetMapping("api/antifraud/suspicious-ip")
    public ResponseEntity<?> findAll() {
        return new ResponseEntity<>(ipService.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<?> deleteIp(@PathVariable String ip) {

        return new ResponseEntity<>(ipService.deleteIp(ip), HttpStatus.OK);
    }
}
