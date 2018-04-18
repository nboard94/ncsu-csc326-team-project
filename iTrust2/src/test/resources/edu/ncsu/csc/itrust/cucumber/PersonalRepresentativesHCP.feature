#Author kpresle
Feature: View Patient Representatives
	As a HCP
	I want to view my Patient's Representatives
	So That I can add representatives for the patient

Scenario: View Patient's Representatives
Given three patients exist
When I log in as a HCP
When I navigate to the Personal Representatives Page for HCPs
When I search for a patient
Then The list of patient's representatives are shown

Scenario: Add Representative for Patient
Given three patients exist
When I log in as a HCP
When I navigate to the Personal Representatives Page for HCPs
When I search for a patient
When I choose to add a representative for the patient
Then The list of patient's representatives are shown
And The new representative should appear in the patient's list of representatives