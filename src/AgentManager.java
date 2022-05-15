import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentManager extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    System.out.println("Restaurant Manager has received: " + msg.getContent());
                    // generate random response
                    boolean chance = Math.random() < 0.5;
                    String manager_response;
                    ACLMessage reply = msg.createReply();

                    if (chance) {
                        manager_response = "20$";
                        reply.setPerformative(ACLMessage.CONFIRM);
                    }
                    else {
                        manager_response = "No";
                        reply.setPerformative(ACLMessage.REFUSE);
                    }

                    reply.setContent(manager_response);
                    send(reply);

                    System.out.println("Restaurant Manager Agent has answered: " + reply.getContent());
                } else block();
            }
        });
    }
}