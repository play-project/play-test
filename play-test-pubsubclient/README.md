# PUBSUB CLI

## About

Simple Web Service Notification client/server which starts a local server and subscribes to a remote notification provider. Received notifications are displayed on the console...

## Howto

Launch the pubsub.sh shell script with the following parameters on the command line like:

> ./pubsub.sh localip localport remoteendpoint topicname topicns topicprefix

where:

- localip : your local IP you are launching the runtime from. Must not be localhost in case you subscribes to a remote host since it is used to create the subscriber endpoint notifications will be send to.
- localport : The local port your are listening to, used with localip to build the endpoint
- remoteendpoint : The generated subscribe message will be sent to this endpoint
- topicname : The Topic name you want to subscribe to
- topicns : The Topic Namespace you want to subscribe to
- topic prefix : The Topic prefix you want to subscribe to

Once started, type 'start' to create the local server and send the subscribe message to the remote endpoint. Type 'stop' to unsubscribe and stop the local server.

## Example

> ./pubsub.sh localhost 8978 http://remotehost/foo/Service TopicA http://petals.ow2.org/topic ow2

Real example for local DSB providing default business topic:

./pubsub.sh localhost 8978 http://localhost:8084/petals/services/NotificationProducerPortService BusinessIntegrationTopic http://www.petalslink.org/integration/test/1.0 itg 