# [Backend] Rate-Limited Notification Service


## Description:


[Backend] Rate-Limited Notification Service

We have a Notification system that sends out email notifications of various types (status update, daily news, project invitations, etc). We need to protect recipients from getting too many emails, either due to system errors or due to abuse, so let’s limit the number of emails sent to them by implementing a rate-limited version of NotificationService.

The system must reject requests that are over the limit.

Some sample notification types and rate limit rules, e.g.:

* Status: not more than 2 per minute for each recipient

* News: not more than 1 per day for each recipient

* Marketing: not more than 3 per hour for each recipient

* Etc. these are just samples, the system might have several rate limit rules!

## Approach

This implementation is more focused on working with the problem at a logic level and providing an approximate solution. However, this problem could be solved using AWS SNS + SQS + Lambda. This cloud solution is the most suitable but not the one I am most familiar with.

## About the code
The following dependencies are being used in the code:
- H2 database
- Spring web
- Spring Boot Dev Tools
- Java Mail Sender
- Spring Data JPA
- Lombok
- Mockito

## Implementation

* springboot (v3.2.5)

* complete these properties for the SMTP server:

```
spring.mail.username=
spring.mail.password=
```

* NotificationController will handle the request for sending a new notification.
* A scheduled process will take all the pending notifications and will attempt to send them.


