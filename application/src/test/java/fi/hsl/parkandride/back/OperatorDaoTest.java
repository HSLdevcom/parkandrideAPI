package fi.hsl.parkandride.back;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;

import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.domain.MultilingualString;
import fi.hsl.parkandride.core.domain.Operator;
import fi.hsl.parkandride.core.domain.OperatorSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.service.ValidationException;

public class OperatorDaoTest extends AbstractDaoTest {

    @Resource
    OperatorRepository operatorRepository;

    @Test
    public void operator_management_flow() {
        Operator op = new Operator("operator");
        long id = operatorRepository.insertOperator(op);
        assertThat(id).isGreaterThan(0);

        op = operatorRepository.getOperator(id);
        assertThat(op.id).isEqualTo(id);
        assertThat(op.name).isEqualTo(new MultilingualString("operator"));

        SearchResults<Operator> operators = operatorRepository.findOperators(new OperatorSearch());
        assertThat(operators.results).hasSize(1);
        assertThat(operators.results.get(0).name).isEqualTo(new MultilingualString("operator"));

        op.name = new MultilingualString("fi", "sv", "en");
        operatorRepository.updateOperator(id, op);
        op = operatorRepository.getOperator(id);
        assertThat(op.name).isEqualTo(new MultilingualString("fi", "sv", "en"));
    }

    @Test
    public void unique_name() {
        Operator op = new Operator("operator");
        operatorRepository.insertOperator(op);
        verifyUniqueName(op, "fi");
        verifyUniqueName(op, "sv");
        verifyUniqueName(op, "en");
    }

    private void verifyUniqueName(Operator op, String lang) {
        op.name = new MultilingualString("another");
        try {
            op.name.asMap().put(lang, "operator");
            operatorRepository.insertOperator(op);
            fail("should not allow duplicate names");
        } catch (ValidationException e) {
            assertThat(e.violations).hasSize(1);
            assertThat(e.violations.get(0).path).isEqualTo("name." + lang);
        }
    }

}
