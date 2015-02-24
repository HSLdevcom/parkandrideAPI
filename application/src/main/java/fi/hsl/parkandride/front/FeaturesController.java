package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.FEATURES;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeaturesController {

    @Inject Features features;

    @RequestMapping(method = GET, value = FEATURES, produces = APPLICATION_JSON_VALUE)
    public Features getFeatures() {
        return features;
    }

}
