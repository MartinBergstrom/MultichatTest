# MultichatTest
This is a simple chat program which uses thread to retrieve/send messages.
Several clients can connect to the server which will broadcast all messages to the connected clients.

You can choose to run client/server with gui or in console mode.

For Client:
Example cmd with gui:
java Client "ip-address" "portnummer" gui

You can type what ever you want as third parameter, simply having a third will trigger gui mode.
Analogously for console mode, just write ip and portnummer:
java Client "ip-address" "portnummer"

For server:
Example cmd with gui:
java MainServerHandler "portnummer" gui123
Example cmd without gui:
java MainServerHandler "portnummer"
