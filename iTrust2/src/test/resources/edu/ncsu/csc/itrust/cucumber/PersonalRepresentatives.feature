#Author jdrobe10

Feature: Personal Representatives
    As a Patient
    I want to add personal representatives
    So that people can make medical decisions on my behalf

Scenario: Add a Personal Representative
Given three patients exist
When I login as a patient
When I navigate to the personal representatives page
When I choose to add a personal representative
Then the patient is added as my representative

Scenario: Delete a Personal Representative
Given I am a patient and have a personal representative
When I login as a patient
When I navigate to the personal representatives page
When I choose to remove a personal representative
Then the representative is removed from my list of personal representatives

Scenario: Remove yourself as a Personal Representative
Given I am assigned as a personal representative
When I login as a patient
When I navigate to the personal representatives page
When I select the patient I do not want to represent
Then I am removed as their personal representative
