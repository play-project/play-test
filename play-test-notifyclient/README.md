# WSN Notification CLI

Send notifications to a remote service from the commande line...

## Generate

mvn install

## Use

Once generated, launch it from the shell like

	sh target/bin/notifycli http://host:port/Service TopicName TopicNS TopicPrefix NB DELAY
	
> Real sample for default business topic provided by the DSB, sends 1000 notify at 150 ms interval:

	sh target/bin/notifycli http://localhost:8084/petals/services/NotificationConsumerPortService BusinessIntegrationTopic http://www.petalslink.org/integration/test/1.0 itg 1000 150
