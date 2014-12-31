package fi.hsl.parkandride.back;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Test;

import fi.hsl.parkandride.core.back.PaymentMethodRepository;

public class PaymentMethodDaoTest extends AbstractDaoTest {
    @Inject
    PaymentMethodRepository dao;

    @Test
    public void find_all() {
        assertThat(dao.findAll()).extracting("name.en").containsExactly(
                "Coins",
                "Notes",
                "Visa Debit",
                "Visa Electron",
                "American Express",
                "MasterCard",
                "DinersClub",
                "HSL travel card",
                "VR card",
                "HSL single ticket",
                "VR single ticket",
                "Other"
        );
    }
}
