# MultichatTest
This is a simple chat program which uses thread to retrieve/send messages. You can send text, images and files.

Several clients can connect to the server. The server can choose which clients to talk to,
it can also broadcast to all connected clients

For Client:  
Example cmd with gui:  
java Client "ip-address" "portnummer" gui  
  
You can type what ever you want as third parameter, simply having a third will trigger gui mode.
  
For server:  
Example cmd with gui:  
java MainServerHandler "portnummer" gui123  


Typing messages is trivial and can be send by clicking the sendbutton or simply pressing the ENTER key.
Images and files can be send by using the menu item then using the explorer to select the file to send.

Sending images will simply show up on the receivers gui, sending files will download and save.
