# Salon Booking

A simple online booking system for a small hairdressing business, built with Java and Spring Boot.

## Why I'm building this

Bookings for  my neighbour regularly get mixed up, which means confused clients and lost time. This project replaces that with a clear process: clients request an appointment online, and the stylist decides what happens next, with everything showing up in her calendar.


## What it will do

- Clients request a date and time for a service
- The stylist can **confirm**, **reject with a reason**, or **propose a different time**
- Clients check their booking status through a personal link, no account needed
- Double bookings are prevented
- Confirmed bookings appear in the stylist's calendar

## Tech stack

- Java 17+ and Spring Boot
- Thymeleaf (web pages)
- PostgreSQL (database)
- Docker and Docker Compose (containers)
- Planned: automated deployment with GitHub Actions and hosting online

## Progress

I'm building this one small step at a time and committing daily.

- [x] Project skeleton with home page
- [ ] Booking model and database
- [ ] Booking request form
- [ ] Status page for clients
- [ ] Docker setup
- [ ] Stylist login and dashboard
- [ ] Confirm, reject and propose a new time
- [ ] Double-booking check
- [ ] Calendar feed
- [ ] Deployment