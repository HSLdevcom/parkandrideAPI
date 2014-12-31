package fi.hsl.parkandride.core.service;

import java.util.List;

import fi.hsl.parkandride.core.back.PaymentMethodRepository;
import fi.hsl.parkandride.core.domain.PaymentMethod;

public class PaymentMethodService {
    private final PaymentMethodRepository repository;

    public PaymentMethodService(PaymentMethodRepository repository) {
        this.repository = repository;
    }

    @TransactionalRead
    public List<PaymentMethod> findAll() {
        return repository.findAll();
    }
}
