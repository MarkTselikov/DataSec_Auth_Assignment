To run the server, you need to launch the rmiregistry first. To do so, you need to change the directory to the one that contains the 
.class files. To do so, you need to enter the "cd <YOUR_DIRECTORY>" in the cmd or terminal. 
Then, you need to run the "start rmiregistry" command.

Before starting the server, make sure you have the logins.txt file with filled credentials in it. 
The format of credentials is (Username,Hash_Password,Role). Role can be either "User" or "Admin".
If you are going to run the server from console, the credential file should be in the same directory as the .class files.
If you are going to run the server from IDE, it should be located in the root of the project.

The password should be stored in the hashed representation. 
If you want to create the credentials yourself, you can get the hashed value of a password by running the HashingTest.java file.
Set the "password" variable and the console should print the hashed value after running the file. 
We have also submitted a credential file with predefined credentials:
	admin - 123 	(Admin role)
	user1 - pass	(User role)

After all the prerequisites are satisfied, you can run the server. Once the server is running, you can also start the client.
The client will ask you to log in using the username and a plaintext password. After that, the menu will be displayed.
You can put a number of an option without a dot and press enter to access the server functionality.

You can also access the logs stored in the log.txt file. The same information will be displayed in the console of the server.

	
	*Note: if you need to run the server a second time, you also need to restart the rmiregistry to avoid RMI binding exceptions.