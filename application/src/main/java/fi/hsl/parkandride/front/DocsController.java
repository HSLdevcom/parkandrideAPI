// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class DocsController {

    private final String swaggerUi;

    public DocsController() {
        CharSource sdoc = asCharSource(getResource("META-INF/resources/sdoc.jsp"), UTF_8);
        try {
            swaggerUi = CharStreams.toString(sdoc.openStream())
                    .replace("${pageContext.request.contextPath}", "");
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @RequestMapping(value = UrlSchema.DOCS, method = GET, produces = "text/html")
    @ResponseBody
    public String docs() {
        return swaggerUi;
    }

    @RequestMapping(value = UrlSchema.DOCS2, method = GET)
    public String docs2() {
        return "redirect:" + UrlSchema.DOCS2 + "/index.html";
    }
}
