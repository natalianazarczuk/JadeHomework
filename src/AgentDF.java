import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;


public class AgentDF extends Agent {
    DFAgentDescription dfd;

    @Override
    protected void setup() {
        dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription pl = new ServiceDescription();
        pl.setType("polish");
        pl.setName("JarekGatewayAgent");
        dfd.addServices(pl);

        ServiceDescription pl2 = new ServiceDescription();
        pl2.setType("polish");
        pl2.setName("BarbaraGatewayAgent");
        dfd.addServices(pl2);

        ServiceDescription jap = new ServiceDescription();
        jap.setType("japanese");
        jap.setName("IzumiGatewayAgent");
        dfd.addServices(jap);

        ServiceDescription fr = new ServiceDescription();
        fr.setType("french");
        fr.setName("CroqueGatewayAgent");
        dfd.addServices(fr);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    //client sends what food he wants
                    Iterator services = dfd.getAllServices();
                    StringBuilder restaurants = new StringBuilder();

                    while(services.hasNext()) {
                        ServiceDescription sd = (ServiceDescription) services.next();
                        if (msg.getContent().equals(sd.getType())) {
                            restaurants.append(sd.getName()).append(" ");
                        }
                    }


                    if (restaurants.toString().isEmpty()){
                        ACLMessage end = new ACLMessage(ACLMessage.REFUSE);
                        end.addReceiver(new AID("AgentClient", AID.ISLOCALNAME));
                        end.setLanguage("English");
                        end.setOntology("Reservation-Restaurant-Ontology");
                        end.setContent("No available restaurant");
                        send(end);
                        System.out.println("AgentDF response: " + end.getContent());

                    } else {
                        ACLMessage reply = new ACLMessage(ACLMessage.QUERY_REF);
                        reply.addReceiver(new AID("AgentClient", AID.ISLOCALNAME));
                        reply.setLanguage("English");
                        reply.setOntology("Reservation-Restaurant-Ontology");
                        reply.setContent(restaurants.toString());
                        send(reply);
                        System.out.println("AgentDF response: " + reply.getContent());
                    }
                }
            }
        });

    }
}
