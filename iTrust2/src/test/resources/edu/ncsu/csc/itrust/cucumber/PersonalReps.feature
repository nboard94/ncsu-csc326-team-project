#Author jdrobe10

Feature: Personal Representatives
	As a Patient
	I want to add personal representatives
	So that people can make medical descisions on my behalf

Scenario: Add a Personal Representative
Given both patients exist
When I login a patient
When I navigate to the personal representatives page
When I choose to add a personal representative
When I enter my personal representative's ID <id>
Then the patient is added as my representative

Scenario: Edit a Personal Representative
Given three patients exist
When I login as a patient
When I navigate to the personal representatives page
When I choose to edit my personal representatives
When I select my first personal representative
When I enter a new representative's ID <id>
Then my personal representatives are updated with the new representative

Scenario: Delete a Personal Representative
Given I have a personal representative
When I login as a patient
When I navigate to the personal representatives page
When I choose to remove a personal representative with ID <id>
Then they representative is removed from my list of personal representatives
