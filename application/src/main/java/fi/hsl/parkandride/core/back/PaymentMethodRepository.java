package fi.hsl.parkandride.core.back;

import java.util.List;

import fi.hsl.parkandride.core.domain.PaymentMethod;

public interface PaymentMethodRepository {
    List<PaymentMethod> findAll();
}
