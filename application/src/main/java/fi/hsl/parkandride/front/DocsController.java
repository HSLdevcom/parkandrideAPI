// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class DocsController {

    @RequestMapping(value = UrlSchema.DOCS, method = GET)
    public String docs() {
        return "redirect:" + UrlSchema.DOCS + "/index.html";
    }
}
