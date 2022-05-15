import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;


public class AgentClient extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new SimpleBehaviour() {
            private boolean first = true;
            private boolean accepted = false;

            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.PROPOSE && !accepted) {
                    ACLMessage accept = msg.createReply();
                    accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    accept.setContent("Yes, I want it");
                    send(accept);
                    accepted = true;
                    System.out.println("Client Agent: " + accept.getContent());
                } else if (msg != null && msg.getPerformative() == ACLMessage.QUERY_REF) {
                    String dish = null;
                    int nr = (int) (Math.random() * 8 + 1);
                    int h = (int) (Math.random() * 10 + 1);
                    String[] gateways = msg.getContent().split(" ");
                    for (String var : gateways) {
                        ACLMessage order = new ACLMessage(ACLMessage.QUERY_IF);
                        order.addReceiver(new AID(var, AID.ISLOCALNAME));
                        order.setLanguage("English");
                        order.setOntology("Reservation-Restaurant-Ontology");

                        //based on the restaurant, we want to order something different
                        switch (var) {
                            case "JarekGatewayAgent":
                            case "BarbaraGatewayAgent":
                                dish = Math.random() < 0.5 ? "pierogi " : "kotlet ";
                                break;
                            case "IzumiGatewayAgent":
                                dish = Math.random() < 0.5 ? "sushi " : "ramen ";
                                break;
                            case "CroqueGatewayAgent":
                                dish = Math.random() < 0.5 ? "quiche " : "crepes ";
                                break;
                        }

                        order.setContent(dish + "for " + nr + " people " + h + "PM");
                        send(order);
                        System.out.println("Client Agent ordered: " + order.getContent());

                    }

                }

                if (first) {
                    ACLMessage first_msg = new ACLMessage(ACLMessage.QUERY_IF);
                    first_msg.addReceiver(new AID("AgentDF", AID.ISLOCALNAME));
                    first_msg.setLanguage("English");
                    first_msg.setOntology("Reservation-Restaurant-Ontology");

                    switch ((int)(Math.random() * 4 + 1)) {
                        case 1:
                            first_msg.setContent("polish");
                            break;
                        case 2:
                            first_msg.setContent("japanese");
                            break;
                        case 3:
                            first_msg.setContent("german"); //this should return no
                            break;
                        case 4:
                            first_msg.setContent("french");
                            break;
                    }
                    send(first_msg);
                    first = false;

                    System.out.println("Client Agent: " + first_msg.getContent());

                }
            }

            @Override
            public boolean done() {
                return false;
            }

        });
    }
}