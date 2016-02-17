# Arch #

## Design ##

There are two ways to design of distributed application - push and poll models.

**Push Model**: Clients provide an Ice object with an operation that the chat server invokes to deliver
messages to clients. In this model, the chat server acts as the client and the chat client acts as the
server, that is, while the chat server delivers messages to clients, the normal roles are reversed.

**Pull Model**: Clients periodically invoke an operation on an object provided by the chat server to
retrieve messages that are sent by other users. In this model, chat clients and server are “pure”
clients and server because they exclusively act in the client or server role, respectively.

In general, the push model is both easier to implement and easier to scale

## Class Diagram ##

![http://snoopy.googlecode.com/svn-history/r19/wiki/img/model.png](http://snoopy.googlecode.com/svn-history/r19/wiki/img/model.png)


# Details #