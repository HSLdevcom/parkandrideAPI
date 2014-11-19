package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.Service;
import fi.hsl.parkandride.core.domain.ServiceSearch;

public interface ServiceRepository {

    Service getService(long serviceId);

    SearchResults<Service> findServices(ServiceSearch search);

}
