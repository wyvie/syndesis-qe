@integration-import-export
Feature: Test functionality of integration import export feature

  Background: Clean application state
    Given clean application state
    Given "Camilla" logs into the Syndesis
    Given created connections
      | Twitter    | Twitter Listener | Twitter Listener | SyndesisQE Twitter listener account |
      | Salesforce | QE Salesforce    | QE Salesforce    | SyndesisQE salesforce test          |

    # create integration
    And "Camilla" navigates to the "Home" page
    And clicks on the "Create Integration" button to create a new integration.
    Then she is presented with a visual integration editor
    And she is prompted to select a "Start" connection from a list of available connections

    # select twitter connection
    When Camilla selects the "Twitter Listener" connection
    And she selects "Mention" integration action
    And Camilla clicks on the "Done" button
    Then she is prompted to select a "Finish" connection from a list of available connections

    # select salesforce connection
    When Camilla selects the "QE Salesforce" connection
    And she selects "Create or update record" integration action
    And she selects "Contact" from "sObjectName" dropdown
    And Camilla clicks on the "Next" button
    And she selects "TwitterScreenName" from "sObjectIdName" dropdown
    And Camilla clicks on the "Done" button
    Then she is presented with the "Add a Step" button

    # add data mapper step
    When Camilla clicks on the "Add a Step" button
    And she selects "Data Mapper" integration step
    Then she is presented with data mapper ui

    When she creates mapping from "user.screenName" to "TwitterScreenName__c"
    When she creates mapping from "text" to "Description"
    When she creates mapping from "user.name" to "FirstName"
    And she separates "user.name" into "FirstName" as "1" and "LastName" as "2" using "Space" separator
    And scroll "top" "right"
    And click on the "Done" button

    # finish and save integration
    When click on the "Save as Draft" button
    And she sets the integration name "Integration_import_export_test"
    And click on the "Publish" button
    # assert integration is present in list
    Then Camilla is presented with "Integration_import_export_test" integration details
    And Camilla clicks on the "Done" button
    And Integration "Integration_import_export_test" is present in integrations list
    # wait for integration to get in active state
    Then she waits until integration "Integration_import_export_test" gets into "Active" state

    # export the integration for import tests
    When Camilla selects the "Integration_import_export_test" integration
    Then Camilla is presented with "Integration_import_export_test" integration details
    And Camilla exports this integraion

    # now we have exported integration, we can clean state and try to import
    Given clean application state

    Then Wait until there is no integration pod with name "integrationimportexporttest"

    Given "Camilla" logs into the Syndesis
    And "Camilla" navigates to the "Integrations" page
    And Camilla clicks on the "Import" button

#
#  1. integration-import classic method
#
  @integration-import-export-classic
  Scenario: Create integration and test export feature of single integration

    Then Camilla imports integraion "Integration_import_export_test"

    # check draft status after import
    When Camilla selects the "Integration_import_export_test" integration
    And she is presented with "Draft" integration status on Integration Detail page

    # with this wait here imported integration finally starts but just to be sure I will not delete the above wait for pod deletion
    And she stays there for "15000" ms

    # start integration and wait for active state
    And Camilla starts integration "Integration_import_export_test"

    And "Camilla" navigates to the "Integrations" page
    Then she waits until integration "Integration_import_export_test" gets into "Active" state

#
#  2. integration-import with drag'n'drop
#
  @integration-import-export-with-drag-and-drop
  Scenario: Create integration and test export feature of single integration

    And Camilla drags exported integration "Integration_import_export_test" file to drag and drop area

    # check draft status after import
    When Camilla selects the "Integration_import_export_test" integration
    And she is presented with "Draft" integration status on Integration Detail page

    # with this wait here imported integration finally starts but just to be sure I will not delete the above wait for pod deletion
    And she stays there for "15000" ms

    # start integration and wait for active state
    And Camilla starts integration "Integration_import_export_test"

    And "Camilla" navigates to the "Integrations" page
    Then she waits until integration "Integration_import_export_test" gets into "Active" state
