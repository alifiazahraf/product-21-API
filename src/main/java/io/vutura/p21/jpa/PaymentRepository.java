package io.vutura.p21.jpa;

import io.vutura.p21.model.Payment;
import io.vutura.p21.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepository  extends CrudRepository<Payment, String> {
    Optional<Payment> findAllByInvoiceNumber(String invoiceNumber);
}
