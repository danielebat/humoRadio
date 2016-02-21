# HumoRadio

## Description
The idea is to create a recommender system based on user’s mood, with 2 fields: Happy/Sad and Relaxing/Exciting Levels. 
It is a Java web based application using JEE. The application consists of an HTML page in which the user specifies a genre, if he wants, and a specific mood
or he leaves it indifferent. In another HTML5 page a servlet will manage requests coming from the first page and will provide user the playlist to listen or will let the
user come back to change his mood.
The application will play songs through Youtube, displaying also the video related in order to have a better entertainment. We worked with the Youtube Api to
interact with player parameters.
Java language was exploited to manage all the logic behind the service, Javascript to handle Youtube player aspects and user interaction.
Through an anonymous procedure the user rates all songs he decides to listen assigning a vote based on the percetage of time listened. This system
is useful to provide songs to future users who will use the app to listen to the music provinding a new mood.
