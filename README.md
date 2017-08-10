# LanguageGame

### Assignment, class and language
This is the solution to the assignment **LanguageGame** of class **software developement 2,** written in Java with my two team members [Korbinian Karl](https://github.com/korbster) and [Ehsan Moslehi](https://github.com/eca852).

### Requirements
To complete this assignment we should learn and practice how to handle threads and client server communication. Our goal was to write a game, where a server sends a german term to players (the clients) and awaits the translation of this term in a certain language. The players have to translate this term in the demanded language and send it back to the server as fast as they can. If no players answer is right, none of the players gets points. If there are several right answers, the player which send the right answer first gets the points, any other gets no points. The Server sends a new term either every player has sent an answer (no matter if the answer is right or wrong) or a certain amount of time is over. The game ends if the server has sent all terms and either every player has sent an answer or a certain amount of time is over. The player with the most points wins.

My task was to write the client class with a JavaFX graphics user interface. The GUI was built with the help of SceneBuilder. My team members wrote the server class and the test classes. Together we came up with a new way of client server communication.
