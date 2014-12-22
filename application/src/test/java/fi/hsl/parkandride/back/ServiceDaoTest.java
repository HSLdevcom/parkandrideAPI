package fi.hsl.parkandride.back;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hsl.parkandride.core.back.ServiceRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.dev.DevHelper;

public class ServiceDaoTest extends AbstractDaoTest {

    @Inject
    ServiceRepository serviceDao;

    @Test
    public void get_by_id() {
        Service service = serviceDao.getService(1);
        assertThat(service.id).isEqualTo(1l);
        assertThat(service.name).isEqualTo(new MultilingualString("Hissi", "Elevator", "Elevator"));
    }

    @Test
    public void find_all_defaults() {
        assertThat(serviceDao.findServices(new ServiceSearch()).results).extracting("name.fi").containsExactly(
                "Autopesu",
                "Ensiapu",
                "Esteetön WC",
                "Hissi",
                "Infopiste",
                "Inforuudut",
                "Kameravalvonta",
                "Katettu",
                "Kengänkiillotus",
                "Korjauspalvelu",
                "Käynnistysapu",
                "Lastenrattaiden vuokraus",
                "Lippuautomaatti",
                "Maksu portilla",
                "Maksutiski",
                "Paikkojen varausmahdollisuus",
                "Sateenvarjovuokraus",
                "Valaistus",
                "WC");
    }

    @Test
    public void find_all_sort_by_name_en() {
        ServiceSearch search = new ServiceSearch();
        search.sort = new Sort("name.en");
        assertThat(serviceDao.findServices(search).results).extracting("name.en").containsExactly(
                "Car Wash",
                "Covered",
                "Elevator",
                "Engine ignition aid",
                "First aid",
                "Handicapped Toilets",
                "Information Point",
                "Info screens",
                "Lighting",
                "Parking space reservation",
                "Pay Desk",
                "Payment at the gate",
                "Repair Shop",
                "Shoe Shine",
                "Stroller rental",
                "Surveillance cameras",
                "Toilets",
                "Umbrella rental",
                "Vending Machine"
        );
    }
}
