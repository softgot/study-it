study-it
========

Spaced repetition software program.

This program is still in development. I have yet not added categories for card collections. At the moment users 
resembles card collection categories. If you want a card collection for a specific subject, then you could 
for example create a user with the subject as the username. 

The program has a study session where the user can open and study the expired cards of a card collection. Every card has
a knowledge level which is used to evaluate an expiration date for the card. The levels range from 1-8:

* Level 1 = 1 day
* Level 2 = 2 days
* Level 3 = 4 days
* Level 4 = 8 days
* Level 5 = 16 days
* Level 6 = 32 days
* Level 7 = 64 days 
* Level 8 = 128 days

The knowledge level of a card is set in the study session where card collections are studied. An incorrect answer will
set the knowledge level of a card to 1, which means the card will be outdated the next day. A correct answer will
increment the card level with 1. For example, if the user gives the right answer to a level 4 card, the new level of the
card is set to 5. Which means that the card will expire in 16 days. 

At the moment program menus and code comments are written in swedish but I'll translate it later.

TODO
====
* Add categories for card collections.
* Fix headers in main window.
* Translate program menus and program comments.
* Add the ability to choose expiration algorithm.
* Search function.

Dependencies
============
- Java 7

Install
=======
The program save cards into an sqlite3 database. In order for the program to function, an sqlite3 database must be 
available in $HOME/studyit/db/study_it, where $HOME is the user home directory and study_it the sqlite database. 
The database has to contain the table:

CREATE TABLE `users` (
  `userid` integer DEFAULT NULL
,  `username` varchar(50) DEFAULT NULL
,  `password` varchar(50) DEFAULT NULL
);

with the following row included: 

userid=1000

username='guest'

password=''
