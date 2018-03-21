#Author jdrobe10

Feature: Vaccinations
	As an HCP
	I want to view and create vaccinations
	So that patients can be vaccinated against different diseases

Scenario: View Vaccinations
Given that at least one vaccination exists
When I login as an HCP
When I navigate to the vaccinations page
Then I shown a list of current vaccinations

Scenario: Add Vaccination
Given that iTrust2 is running
When I login as an HCP
When I navigate to the vaccinations page
When I choose to add a vaccination
When I enter the information for a new vaccination
Then a new vaccination is created and shown on the vaccinations page
