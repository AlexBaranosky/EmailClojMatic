EmailClojMatic
===========================

Program to email you reminders from your gmail account.  

Your reminders.txt file might look something like this:

    Sundays "go to church." notify 1 day in advance  
	
    Every 15th of the month "pay bill X"
	
    Every 2nd Thursday, starting 2/10/2011 "Go to the Boston Coding Dojo Meetup"
	
    2010 10 14 "something ona  specific date" notify 2 days in advance    
	
	Every day "walk the dog"
	
To get it working on your machine you will need to follow a few steps:

1. edit email.clj to use your gmail account and password i.e. (.setAuthentication "you@gmail.com" "your password")
2. edit utility.clj to make the resource function return the proper path to the resource folder of the project on your machine
3. execute: lein uberjar
4. edit reminders.bat to look like this: java -jar C:\path\to\emailclojmatic-1.0.0-SNAPSHOT-standalone.jar "Your name" "you@gmail.com" ["Other person" "them@gmail.com"]
5. setup a cron job or a windows scheduled task to run the reminders.bat say every hour (or similar script for Unix).  On windows you can have it run hidden, which will keep annoying popups from appearing every hour.

Enjoy.
