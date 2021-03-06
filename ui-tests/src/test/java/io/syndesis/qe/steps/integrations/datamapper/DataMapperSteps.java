package io.syndesis.qe.steps.integrations.datamapper;

import static org.junit.Assert.assertThat;

import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.greaterThan;

import static com.codeborne.selenide.Condition.visible;

import io.syndesis.qe.pages.integrations.editor.add.steps.DataMapper;
import io.syndesis.qe.wait.OpenShiftWaitUtils;

import com.codeborne.selenide.SelenideElement;

import java.util.List;
import java.util.concurrent.TimeoutException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by sveres on 11/15/17.
 */
@Slf4j
public class DataMapperSteps {

    private DataMapper mapper = new DataMapper();

    /**
     * This step can create all types of data mapper mappings.
     * <p>
     * If you want to combine or separate functions, just use it as in this example:
     * <p>
     * Basic:           | user          | firstName             |
     * Combine:         | user; address | description           |
     * Separate:        | name          | firstName; lastName   |
     * <p>
     * For combine and separate, data mapper will automatically use default separator - space. Separator setting is not
     * implemented yet because it was not needed.
     *
     * @param table
     */
    @When("^create data mapper mappings$")
    public void createMapping(DataTable table) {
        mapper.switchToDatamapperIframe();

        // only automatically open unnamed collections for data mapping
        // if we want to open named collections, then use specific step for that
        mapper.openDataMapperUnnamedCollectionElement();

        for (List<String> row : table.cells()) {
            if (row.size() > 2) {
                mapper.doCreateMappingWithSeparator(row.get(0), row.get(1), row.get(2));
            } else {
                mapper.doCreateMapping(row.get(0), row.get(1));
            }
        }
        mapper.switchIframeBack();
    }

    @When("^open data mapper collection mappings$")
    public void openCollectionMappings() {
        mapper.switchToDatamapperIframe();
        mapper.openDataMapperCollectionElement();
        mapper.switchIframeBack();
    }

    @When("^open data mapper unnamed collection mappings$")
    public void openUnnamedCollectionMappings() {
        mapper.openDataMapperUnnamedCollectionElement();
    }

    @Then("^check visibility of data mapper ui$")
    public void dataMapperUIpresent() {
        log.info("data mapper ui must load and show fields count");
        try {
            OpenShiftWaitUtils.waitFor(() -> mapper.validate(), 1000 * 30);
        } catch (TimeoutException | InterruptedException e) {
            fail("Data mapper was not loaded in 30s!", e);
        }
        mapper.switchToDatamapperIframe();
        assertThat(mapper.fieldsCount(), greaterThan(0));
        mapper.switchIframeBack();
    }

    @When("^select \"([^\"]*)\" from \"([^\"]*)\" selector-dropdown$")
    public void selectFromDropDownByElement(String option, String selectAlias) {
        mapper.switchToDatamapperIframe();
        log.info(option);
        SelenideElement selectElement = mapper.getElementByAlias(selectAlias).shouldBe(visible);
        mapper.selectOption(selectElement, option);
        mapper.switchIframeBack();
    }

    @Then("^fill in \"([^\"]*)\" selector-input with \"([^\"]*)\" value$")
    public void fillActionConfigureField(String selectorAlias, String value) {
        mapper.switchToDatamapperIframe();
        SelenideElement inputElement = mapper.getElementByAlias(selectorAlias).shouldBe(visible);
        mapper.fillInput(inputElement, value);
        mapper.switchIframeBack();
    }

    @When("^define constant \"([^\"]*)\" of type \"([^\"]*)\" in data mapper$")
    public void defineConstantOfTypeInDataMapper(String value, String type) {
        mapper.switchToDatamapperIframe();
        mapper.addConstant(value, type);
        mapper.switchIframeBack();
    }

    @When("^define property \"([^\"]*)\" with value \"([^\"]*)\" of type \"([^\"]*)\" in data mapper$")
    public void definePropertyWithValueOfTypeInDataMapper(String name, String value, String type) {
        mapper.addProperty(name, value, type);
    }
}
