package io.vutura.p21.controller;


import io.vutura.p21.exceptions.CrudException;
import io.vutura.p21.jpa.PaymentRepository;
import io.vutura.p21.jpa.ProgressRepository;
import io.vutura.p21.jpa.UserRepository;
import io.vutura.p21.jpa.VoucherRepository;
import io.vutura.p21.model.Payment;
import io.vutura.p21.model.Progress;
import io.vutura.p21.model.User;
import io.vutura.p21.model.Voucher;
import io.vutura.p21.util.JsonEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api")
public class Vutura {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private VoucherRepository voucherRepo;

    @Autowired
    private PaymentRepository paymentRepo;
    @Autowired
    private ProgressRepository progressRepo;

    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestBody User user) throws CrudException {
        HttpStatus status = HttpStatus.CREATED;
        Optional<User> found = userRepo.findAllByPhoneNumber(user.getPhoneNumber());
        if (found.isPresent()) {
            throw new CrudException(CrudException.ExceptionType.DataExisted, "Pendaftaran kamu gagal! cek kembali data yang kamu masukkan");
        }
        User savedUser = userRepo.save(user);
        Map<String, Object> env = JsonEnvelope.encloseMessage(status.value(), "Pendaftaran kamu berhasil!");
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }

    @GetMapping("/user/{phoneNumber}")
    public ResponseEntity<Object> readUser(@PathVariable String phoneNumber) throws CrudException {
        HttpStatus status = HttpStatus.OK;
        Map<String, Object> env;
        Optional<User> found = userRepo.findAllByPhoneNumber(phoneNumber);
        if (found.isPresent()) {
            env = JsonEnvelope.encloseSingleData(status.value(), found.get());
        } else {
            throw new CrudException(CrudException.ExceptionType.DataNotFound, "data user tidak ditemukan");
        }
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }

    @PostMapping("/plan")
    public ResponseEntity<Object> setUserPlan(@RequestBody User user) throws CrudException {
        HttpStatus status = HttpStatus.CREATED;
        Optional<User> found = userRepo.findAllByPhoneNumber(user.getPhoneNumber());
        if (found.isEmpty()) {
            throw new CrudException(CrudException.ExceptionType.DataNotFound, "");
        }
        User user1 = found.get();
        user1.setCourse(user.getPlan());
        User savedUser = userRepo.save(user1);
        Map<String, Object> env = JsonEnvelope.encloseMessage(status.value(), "Kamu berhasil memilih paket " + user.getPlan().toLowerCase());
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }
    @GetMapping("/plan/{phoneNumber}")
    public ResponseEntity<Object> readUserPlan(@PathVariable String phoneNumber) throws CrudException {
        HttpStatus status = HttpStatus.OK;
        Map<String, Object> env;
        Optional<User> found = userRepo.findAllByPhoneNumber(phoneNumber);
        if (found.isPresent()) {
            env = JsonEnvelope.encloseSingleData(status.value(), found.get().getCourse());
        } else {
            throw new CrudException(CrudException.ExceptionType.DataNotFound, "data user tidak ditemukan");
        }
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }

    @PostMapping("/voucher")
    public ResponseEntity<Object> createVoucer(@RequestBody Voucher voucher) throws CrudException {
        HttpStatus status = HttpStatus.CREATED;
        Optional<Voucher> found = voucherRepo.findAllByVoucher(voucher.getVoucher());
        if (found.isPresent()) {
            throw new CrudException(CrudException.ExceptionType.DataExisted, "cek kembali inputan yang kamu masukkan");
        }
        Voucher savedUser = voucherRepo.save(voucher);
        Map<String, Object> env = JsonEnvelope.encloseMessage(status.value(), "Data voucher berhasil disimpan");
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }
    @GetMapping("/voucher/{voucherCode}")
    public ResponseEntity<Object> readVoucher(@PathVariable String voucherCode) throws CrudException {
        HttpStatus status = HttpStatus.OK;
        Map<String, Object> env;
        Optional<Voucher> found = voucherRepo.findAllByVoucher(voucherCode);
        if (found.isPresent()) {
            env = JsonEnvelope.encloseSingleData(status.value(), found.get());
        } else {
            throw new CrudException(CrudException.ExceptionType.DataNotFound, "data voucher tidak ditemukan");
        }
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }

    @PostMapping("/payment")
    public ResponseEntity<Object> createPayment(@RequestBody Payment payment) throws CrudException {
        HttpStatus status = HttpStatus.CREATED;
        Optional<Payment> found = paymentRepo.findAllByInvoiceNumber(payment.getInvoiceNumber());

        if (found.isPresent()) {
            throw new CrudException(CrudException.ExceptionType.DataExisted, "cek kembali inputan yang kamu masukkan");
        }
        Payment savedUser = paymentRepo.save(payment);
        Map<String, Object> env = JsonEnvelope.encloseMessage(status.value(), "Data payment berhasil disimpan");
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }
    @GetMapping("/payment/{invoiceNumber}")
    public ResponseEntity<Object> readPayment(@PathVariable String invoiceNumber) throws CrudException {
        HttpStatus status = HttpStatus.OK;
        Map<String, Object> env;
        Optional<Payment> found = paymentRepo.findAllByInvoiceNumber(invoiceNumber);
        if (found.isPresent()) {
            env = JsonEnvelope.encloseSingleData(status.value(), found.get());
        } else {
            throw new CrudException(CrudException.ExceptionType.DataNotFound, "data payment tidak ditemukan");
        }
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }

    @PostMapping("/progress")
    public ResponseEntity<Object> recordProgress(@RequestBody Progress progress) throws CrudException {
        HttpStatus status = HttpStatus.CREATED;
        Optional<Progress> found = progressRepo.findAllByPhoneNumberAndModuleName(progress.getPhoneNumber(), progress.getModuleName());
        if (found.isPresent()) {
            throw new CrudException(CrudException.ExceptionType.DataExisted, "Kamu sudah menyelesaikan modul ini");
        }
        Progress savedUser = progressRepo.save(progress);
        Map<String, Object> env = JsonEnvelope.encloseMessage(status.value(), "Data progress berhasil disimpan");
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }
    @GetMapping("/progress/{phoneNumber}/{moduleName}")
    public ResponseEntity<Object> readProgress(@PathVariable String phoneNumber,
                                               @PathVariable String moduleName) throws CrudException {
        HttpStatus status = HttpStatus.OK;
        Map<String, Object> env;
        Optional<Progress> found = progressRepo.findAllByPhoneNumberAndModuleName(phoneNumber, moduleName);
        if (found.isPresent()) {
            Progress user = found.get();
            long now = new Date().getTime();
            if (now - user.getModuleClear().getTime() > (24/*Hours*/ * 60 /*minutes*/* 60 /*seconds*/* 1000/*milis*/)){
                env = JsonEnvelope.encloseSingleData(status.value(), user);
            }else{
                status = HttpStatus.ACCEPTED;
                env = JsonEnvelope.encloseMessage(status.value(), "belum 24 jam");
            }
        } else {
            throw new CrudException(CrudException.ExceptionType.DataNotFound, "Kamu belum menyelesaikan module");
        }
        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only
    }
}
