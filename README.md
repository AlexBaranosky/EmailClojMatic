EmailClojMatic is a program to email you reminders from your gmail account. Has a full automated testing suite using the [Midje](https://github.com/marick/Midje/wiki) testing framework, so there are hopefully few bugs.


Your reminders.txt file might look something like this:
-------------------------------------------------------

    Sundays "go to church." notify 1 day in advance

    Every 15th of the month "pay bill X"

    Every 2nd Thursday, starting 2/10/2011 "Go to the Boston Coding Dojo Meetup"

    On 10/14/2010 & on 1/9/2011 "something on a specific date" notify 2 days in advance

    Every day "walk the dog"

    Every 4th day "do something else!" notify 1 day in advance

    On 12/25 "don't forget Christmas every year" notify 14 days in advance

It will only remind you, generally, once per day, so you don't have to worry about it spamming you with emails.

Setup
-----

To get start using the EmailClojMatic is a resources directory with a `reminders.txt` for the reminders you want to have delivered, and a `config.cljdata` to store your gmail address and password.  You can see samples of these files in the resources directory of the source, or above this section of the README.md

To get it working on your machine follow these steps:

1. edit config.cljdata to use your gmail account and password. It should look like `{ :gmail-address "you@gmail.com" :password "your password" }`
2. execute: `lein uberjar` to create a standalone jar
3. edit reminders.bat or reminders.sh to look like this: java -jar C:\path\to\emailclojmatic-1.0.0-SNAPSHOT-standalone.jar "Your name" "you@gmail.com" - You may list as many name and email address pairs as you'd like to have the reminders sent to
4. setup a cron job or a windows scheduled task to run the reminders.bat/sh say every half an hour. It keeps track of how many reminders it has already sent today so that it will only send you reminders once a day.
5. create a `reminders.txt` like the one shown above and palce it in the resources directory
