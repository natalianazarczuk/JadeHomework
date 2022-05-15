import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;

public class IzumiGatewayAgent extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {

                    if (msg.getPerformative() == ACLMessage.QUERY_IF) { //ask the manager about clients request
                        System.out.println("IzumiGateway has received: " + msg.getContent());

                        ACLMessage msg2 = new ACLMessage(ACLMessage.QUERY_IF);
                        msg2.addReceiver(new AID("IzumiManagerAgent", AID.ISLOCALNAME));
                        msg2.setLanguage(msg.getLanguage());
                        msg2.setOntology(msg.getOntology());
                        msg2.setContent(msg.getContent());
                        this.myAgent.send(msg2);
                    } else if (msg.getPerformative() == ACLMessage.CONFIRM) { //tell client about the price
                        ACLMessage initiation = new ACLMessage(ACLMessage.PROPOSE);
                        initiation.addReceiver(new AID("AgentClient", AID.ISLOCALNAME));
                        initiation.setContent("We can prepare this order for " + msg.getContent());
                        addBehaviour(new ProposeInitiator(this.myAgent, initiation) {
                        });
                        System.out.println("IzumiGateway has answered the Client: " + initiation.getContent());

                    } else if (msg.getPerformative() == ACLMessage.REFUSE) { //tell client the dish can't be done
                        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                        response.addReceiver(new AID("AgentClient", AID.ISLOCALNAME));
                        response.setPerformative(ACLMessage.INFORM);
                        response.setContent("Sorry, we can't prepare this order");
                        send(response);
                        System.out.println("IzumiGateway has answered the Client: " + response.getContent());
                    }
                } else block();
            }
        });
    }
}