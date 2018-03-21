#Author jdrobe10

Feature: Login as new users
	As an iTrust2 user
	I want to login with specific roles
	So that different users have access to different abilities

Scenario: Login as an Emergency Responder
Given that iTrust2 is running
When I login as an emergency responder with username <username> and password <password>
Then I am taken to the home page with my 10 most recent logs listed

Scenario: Login as a Lab Technician
Given that iTrust2 is running
When I login as a lab technician with username <username> and password <password>
Then I am taken to the home page with my 10 most recent logs listed
