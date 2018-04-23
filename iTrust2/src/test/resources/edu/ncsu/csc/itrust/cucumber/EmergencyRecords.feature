#Author jdrobe10

Feature: Emergency Health Records
	As a Health Care Provider
	I want to access patient information in a printable form
	So that I can quickly receive information about the patient in an emergency

Scenario: Access Emergency Health Record
Given the patient in question exists
When I login as an HCP
When I navigate to the Emergency Health Records page
When I enter the patients MID
Then I am given an emergency health record for the patient
